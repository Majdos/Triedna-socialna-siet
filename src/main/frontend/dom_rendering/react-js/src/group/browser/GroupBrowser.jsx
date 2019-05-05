import React, {Component, Fragment} from 'react';
import Immutable from 'immutable';
import ApiPoint, * as resource from 'rest_api/ApiPoint'
import GroupList from './GroupList';
import Button from 'components/Button';
import {InfiniteLoader, List, WindowScroller, CellMeasurerCache, CellMeasurer} from 'react-virtualized';
import {calculatePage} from 'util/Paging';
import Modal from 'components/Modal';
import CreateGroupform from './CreateGroupForm';
import BottomMenu from './BottomMenu';
import SearchApi from 'js-worker-search';
import throttle from 'lodash.throttle';
import StompClient from 'rest_api/WebsocketListener';
// import 'react-virtualized/styles.css'; // only needs to be imported once

const LOADED = true;
const LOADING = false;

const stompClient = new StompClient('/spsjm-social');

// 82 menu, 5 riadky
const cache = new CellMeasurerCache({
    defaultHeight: (window.innerHeight - 82) / 5,
    minHeight: 200,
    fixedWidth: true
});

function handleError(error, invited = false) {
    let message;
    switch (error.status.code) {
        case 403:
        case 404:
            if (invited) {
                message = 'Pozvanie bola zrušené';
            } else {
                message = 'Už nie ste členom skupiny';
            }
            break;

        default:
            message = error;
            break;
    }
    return message;
}

export default class GroupBrowser extends Component {
    constructor(props) {
        super(props);
        this.state = {
            memberships: Immutable.List(),
            showMemberships: true,
            searchResults: Immutable.List(),
            processing: false,
            totalElements: 20,
            columnCount: 5,
            deleteModalOpen: false,
            groupFormState: false
        };

        this.loaderMap = {};
        this.heights = {};
        this.updateWindowDimensions = this.updateWindowDimensions.bind(this);
        this.recalculateGrid = throttle(this.recalculateGrid, 150);

        this.api = new ApiPoint('/api/principal/memberships', 'memberships', this.stateMutator);
        this.searchApi = new SearchApi();

        this.startProcessing = () => {
            this.setState({processing: true})
        };

        this.onFind = (searchResults) => {
            const results = this.state.memberships.filter(membership => searchResults.includes(resource.self(membership)));
            this.list.forceUpdate();
            this.setState({searchResults: results});
        };

        this.notFound = () => {
            this.setState({showMemberships: false});
        };

        this.searchReset = () => {
            this.setState({showMemberships: true, searchResults: this.state.memberships});
        };

        this.indexMembership = (membership) => {
            const group = membership.group;
            this.searchApi.indexDocument(resource.self(membership), [group.name, group.keywords.join(' '), group.description, membership.permissions.join(' ')].join(' '));
        };

        this.bottomPanelOnClose = () => {
            this.searchReset();
        };

        this.openGroupForm = () => {
            this.setState({groupFormState: true});
        };

        this.closeGroupForm = () => {
            this.setState({groupFormState: false});
        };

        this.fetch = (startIndex, stopIndex) => {
            if (this.state.processing) {
                return;
            }

            this.startProcessing();
            this._loadMoreRowsStartIndex = startIndex;
            this._loadMoreRowsStopIndex = stopIndex;

            const start = startIndex * this.state.columnCount;
            const recordCount = (stopIndex - startIndex + 1) * this.state.columnCount;

            for (let i = start; i < start + recordCount; i++) {
                this.loaderMap[i] = LOADING
            }

            return this.api.get({
                params: {
                    offset: start,
                    limit: recordCount
                }
            }).then(result => {
                const memberships = result.resources.memberships;
                const total = result.resources.totalElements;

                this.setState(prev => {
                    const result = prev.memberships.withMutations(list => {
                        for (let i = start; i < start + memberships.length; i++) {
                            const membership = memberships[i - start];
                            list.set(i, membership);
                            this.loaderMap[i] = LOADED;
                            this.indexMembership(membership);
                        }
                    });
                    return {
                        memberships: result,
                        totalElements: total,
                        processing: false
                    };
                });
                return result;
            });
        }

        this.isIndexLoaded = (index) => {
            const row = calculatePage(this.state.columnCount, index);
            for (let i = (row - 1) * this.state.columnCount; i < row * this.state.columnCount; i++) {
                if (this.loaderMap[i] == LOADING) {
                    return false;
                }
            }
            return true;
        };

        this.isRowLoaded = (index) => {
            if (!this.state.searchResults.isEmpty()) {
                return true;
            }

            const start = index * this.state.columnCount;
            const end = Math.min(this.state.totalElements, (index + 1) * this.state.columnCount);

            for (let i = start; i < end; i++) {
                if (this.loaderMap[i] == null || this.loaderMap[i] == LOADING) {
                    return false;
                }
            }
            return true;
        };

        /**
         * Prepocita list a vycisti cache pamat
         * @param {number} index index riadka
         */
        this.recomputeHeight = (index) => {
            return () => {
                cache.clear(index);
                this.list.recomputeRowHeights(index);
            }
        }

        this.rowRenderer = ({index, isVisible, isScrolling, key, style, parent}) => {
            const start = index * this.state.columnCount;
            const end = (index + 1) * this.state.columnCount;
            let memberships;

            if (!this.state.searchResults.isEmpty()) {
                memberships = this.state.searchResults;
            } else if (!this.state.showMemberships) {
                return null;
            } else {
                memberships = this.state.memberships;
            }

            const subMemberships = memberships.slice(start, end);
            return (
                <CellMeasurer
                    cache={cache}
                    columnIndex={0}
                    key={key}
                    parent={parent}
                    rowIndex={index}
                >
                    <GroupList
                        memberships={subMemberships}
                        columnCount={this.state.columnCount}
                        loaded={this.isRowLoaded(index)}
                        style={style}
                        onDelete={this.handleDeleteMembership}
                        acceptInvite={this.handleAcceptInvite}
                        recomputeHeight={this.recomputeHeight(index)}/>
                </CellMeasurer>
            );
        }

        this.deleteMembership = (membership) => {
            const memberships = this.state.memberships;
            const index = memberships.findIndex(member => resource.equalsEntity(member, membership));

            const result = memberships.withMutations(list => {
                for (let i = index; i < list.size - 1; i++) {
                    const next = list.get(i + 1);
                    list.set(i, next);
                    this.loaderMap[i] = LOADED;
                }
                this.loaderMap[list.size - 1] = LOADING;
            });

            this.setState(prev => ({
                memberships: result.delete(-1),
                totalElements: prev.totalElements - 1,
                processing: false
            }), () => this.list.forceUpdateGrid());
        };

        this.acceptInvite = (membership) => {
            const index = this.state.memberships.findIndex(member => resource.equalsEntity(member, membership));
            this.setState(prev => ({
                processing: false,
                memberships: prev.memberships.set(index, membership)
            }), () => this.list.forceUpdateGrid());
        };

        //TODO: zase strankovanie, sight....
        // implementovat aj na search RESULTS
        this.handleDeleteMembership = (membership) => {
            this.startProcessing();
            return this.api.delete({entity: membership}).then(response => {
                this.deleteMembership(membership);
            }, error => {
                handleError(error, membership.invited);
                if (error.status.code === 404 || error.status.code === 403) {
                    this.deleteMembership(membership);
                }
            });
        };

        this.handleAcceptInvite = (membership) => {
            this.startProcessing();
            membership.invited = false;
            return this.api.put({entity: membership}).then(response => {
                this.acceptInvite(membership);
            }, error => {
                handleError(error, true);
                if (error.status.code === 404 || error.status.code === 403) {
                    this.deleteMembership(membership);
                }
            });
        };

        this.getRowCount = () => {
            if (!this.state.searchResults.isEmpty()) {
                return this.state.searchResults.size;
            }
            if (!this.state.showMemberships) {
                return 0;
            }

            return Math.ceil(this.state.totalElements / this.state.columnCount);
        }

        this.onCreateMembership = (message) => {
            this.setState(prev => ({totalElements: prev.totalElements + 1}), () => this.list.forceUpdate());
        };

        this.onCanceledMembership = (message) => {
            const memberships = this.state.memberships;
            const membership = message.body;
            if (!memberships.some(member => resource.self(member) === membership)) {
                this.setState(prev => ({totalElements: prev.totalElements - 1}), () => this.list.forceUpdate());
            }
        };
    }

    componentDidMount() {
        // this.fetch(0, 5);
        this.updateWindowDimensions();
        window.addEventListener('resize', this.updateWindowDimensions);

        this.subscriptions = stompClient.register([
            {route: `${channel}/newMembership`, callback: this.onCreateMembership},
            {route: `${channel}/deleteMembership`, callback: this.onCanceledMembership}
        ]);
    }

    componentWillUnmount() {
        window.removeEventListener('resize', this.updateWindowDimensions);
        this.subscriptions.forEach(subscription => subscription.unsubscribe());
    }

    recalculateGrid() {
        cache.clearAll();
        this.list.recomputeRowHeights();
    }

    updateWindowDimensions() {
        if (this.state.processing) {
            return;
        }
        const width = window.innerWidth;
        if (width < 750) {
            this.setState({columnCount: 1});
        } else if (width < 850) {
            this.setState({columnCount: 2});
        } else if (width < 950) {
            this.setState({columnCount: 3});
        } else if (width < 1200) {
            this.setState({columnCount: 4});
        } else {
            this.setState({columnCount: 5});
        }
        //this.recalculateGrid();
    }

    setRef(list) {
        if (list != null) {
            this.list = list;
        }
    }

    renderBottomMenu() {
        return (
            <BottomMenu
                target="footer"
                onFind={this.onFind}
                notFound={this.notFound}
                reset={this.searchReset}
                searchApi={this.searchApi}
                onClose={this.bottomPanelOnClose}>
                {({show, hide}) => (
                    <Fragment>
                        <Button
                            theme={null}
                            icon="fa-search"
                            onClick={show}
                            className="menu-button"/>
                        <Button
                            theme={null}
                            icon="fa-plus"
                            onClick={this.openGroupForm}
                            className="menu-button complementary"/>
                    </Fragment>
                )}
            </BottomMenu>
        );
    }

    render() {
        return (
            <Fragment>
                <CreateGroupform open={this.state.groupFormState} closeHandler={this.closeGroupForm}/>
                {this.renderBottomMenu()}
                <div>
                    <InfiniteLoader
                        isRowLoaded={({index}) => this.isRowLoaded(index)}
                        loadMoreRows={({startIndex, stopIndex}) => this.fetch(startIndex, stopIndex)}
                        rowCount={this.getRowCount()}
                    >
                        {({onRowsRendered, registerChild}) => (
                            <WindowScroller>
                                {({height, width, isScrolling, onChildScroll, scrollTop}) => (
                                    <List
                                        autoHeight
                                        autoWidth
                                        className="infinite-list"
                                        ref={list => {
                                            this.setRef(list);
                                            registerChild(list);
                                        }}
                                        height={height}
                                        onRowsRendered={onRowsRendered}
                                        isScrolling={isScrolling}
                                        onScroll={onChildScroll}
                                        scrollTop={scrollTop}
                                        rowCount={this.getRowCount()}
                                        rowHeight={cache.rowHeight}
                                        rowRenderer={this.rowRenderer}
                                        width={width}
                                        deferredMeasurementCache={cache}
                                    />
                                )}
                            </WindowScroller>
                        )}
                    </InfiniteLoader>
                </div>
            </Fragment>
        );
    }
}
