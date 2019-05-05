import React, { Fragment } from 'react';
import { client } from 'rest_api/client';
import { List } from 'immutable';
import { capitalize, pluralize } from 'util/String';
import { removeQuery } from 'util/Utility';
import ArticleGrid from 'layout/ArticleGrid';
import Button from 'components/Button';
import ProcessingButton from 'components/ProcessingButton';
import Searchbar from 'components/Searchbar';
import UserTable from './UserTable';
import MembershipFormModal from './MembershipFormModal';
import Loader from 'components/Loader';
import SearchApi from 'js-worker-search'
import NumericTextInput from './NumericTextInput';
import Form from 'components/forms/Form';

export default class AdminView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            pageSize: 20,
            page: 0,
            lastPage: 0,
            total: 0,
            memberships: List(),
            open: false,
            links: { self: { href: apiLink } },
            loading: true,
            errors: List(),
            updatedUsers: 0,
            deletedUsers: 0,
            searchResults: List()
        };
        this.fetchPage = this.fetchPage.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
        this.onUpdateError = this.onUpdateError.bind(this);
        this.onDelete = this.onDelete.bind(this);
        this.onDeleteError = this.onDeleteError.bind(this);
        this.clearErrors = this.clearErrors.bind(this);
        this.addError = this.addError.bind(this);
        this.onFind = this.onFind.bind(this);
        this.changePageSize = this.changePageSize.bind(this);

        this.apply = this.apply.bind(this);
        this.undoAll = this.undoAll.bind(this);

        this.searchApi = new SearchApi();
    }

    isCurrentUser(membership) {
        return membership._links.user.href === userLink;
    }

    getMembersToRender() {
        const { memberships, searchResults } = this.state;
        if (searchResults.isEmpty()) {
            return memberships;
        }
        else {
            return memberships.filter(membership => searchResults.contains(membership._links.self.href));
        }
    }

    fetchCurrentPage() {
        return this.fetchPage(this.state.pageSize, this.state.page);
    }

    fetchPage(size, number) {

        this.setState({ loading: true, searchResults: List() })

        return client({
            method: 'GET',
            path: removeQuery(this.state.links.self.href),
            params: {
                page: number,
                size: size
            }
        }).then(response => {
            this.setState({
                loading: false,
                pageSize: size,
                page: number,
                total: response.entity.page.totalElements,
                lastPage: response.entity.page.totalPages - 1,
                links: response.entity._links,
                memberships: List(response.entity._embedded.memberships),
            });
            return response.entity._embedded.memberships;
        }).then(members => {
            members.forEach(member => {
                if (this.isCurrentUser(member)) {
                    return;
                }
                this.searchApi.indexDocument(member._links.self.href, [member.user.fullname, member.group.name, ...member.permissions].join(' '));
            });
        });
    }

    changePageSize(newPageSize) {
        this.fetchPage(newPageSize, 0);
    }

    fetchHypermediaPage(move) {
        if (!(move in this.state.links)) {
            console.log('Koniec');
            return Promise.reject(new Error('Dana strana neexistuje'));
        }
        const direction = move === 'next' ? 1 : -1;
        return this.fetchPage(this.state.pageSize, this.state.page + direction);
    }

    fetchNextPage() {
        return this.fetchHypermediaPage('next');
    }

    fetchPrevPage() {
        return this.fetchHypermediaPage('prev');
    }

    clearErrors() {
        this.setState({
            errors: List()
        });
    }

    onUpdate(membership) {
        const key = this.state.memberships.findIndex(x => x._links.self.href === membership._links.self.href);
        if (key !== -1) {
            this.setState((prev) => ({
                memberships: prev.memberships.set(key, membership),
                updatedUsers: prev.updatedUsers + 1
            }));
        }
        return this.fetchCurrentPage();
    }

    onDelete(membership) {
        const key = this.state.memberships.findIndex(x => x._links.self.href === membership._links.self.href);
        if (key !== -1) {
            this.setState((prev) => ({
                memberships: prev.memberships.remove(key),
                deletedUsers: prev.deletedUsers + 1
            }));
            return this.fetchCurrentPage();
        }
        return null;
    }

    addError(error) {
        this.setState((prev) => ({
            errors: prev.errors.push(error)
        }));
    }

    onUpdateError(error, membership) {
        this.addError(error);
    }

    onDeleteError(error, membership) {
        this.addError(error);
    }

    onFind(results) {
        this.setState({
            searchResults: List(results)
        });
    }

    apply() {
        if (this.table) {
            return this.table.performApply().then(x => this.fetchCurrentPage());
        }
        return null;
    }

    undoAll() {
        if (this.table) {
            return this.table.undoAll();
        }
        return null;
    }

    componentDidMount() {
        this.fetchCurrentPage();
    }

    //TODO: filtrovanie
    render() {
        const { searchResults } = this.state;

        const principalMembership = this.state.memberships.find(membership => this.isCurrentUser(membership));
        let index = 0;

        return (
            <Fragment>
                <MembershipFormModal
                    action={apiLink}
                    method="post"
                    title="Nový člen"
                    onClose={() => this.setState({ open: false })}
                    open={this.state.open}
                    onSuccess={(entity, response) => this.fetchCurrentPage()}
                    submit cancel
                    displayMessages />

                <ArticleGrid row className="top-spacing">
                    <Form onSubmit={(data) => this.fetchPage(data.pageSize, data.page - 1)} clear={false}>
                        {(form) =>
                            <Fragment>
                                <div className="form-group">
                                    <NumericTextInput form={form} name="page" processing={this.state.loading}
                                        label="Číslo strany" value={this.state.page + 1} max={this.state.lastPage + 1} required />
                                </div>
                                <div className="form-group">
                                    <NumericTextInput form={form} name="pageSize" processing={this.state.loading}
                                        label="Veľkosť strany" value={this.state.pageSize} max={this.state.total} required />
                                </div>
                            </Fragment>
                        }
                    </Form>
                    <div className="form-group">
                        <Searchbar
                            searchApi={this.searchApi}
                            className="w-100"
                            id="search-bar"
                            onFind={this.onFind}
                            reset={() => this.setState({ searchResults: List() })}
                            label="Vyhľadávač" showHelp />
                    </div>
                </ArticleGrid>

                {!this.state.loading &&
                    <Fragment>
                        <ArticleGrid row>
                            <h3 className="text-primary">
                                Strana {this.state.page + 1} z {this.state.lastPage + 1}, {this.state.memberships.size} {pluralize(this.state.memberships.size, 'záznam', 'záznamy', 'záznamov')}
                            </h3>
                            <UserTable
                                ref={input => this.table = input}
                                memberships={this.getMembersToRender()}
                                principalMembership={principalMembership}
                                onUpdate={this.onUpdate}
                                onUpdateError={this.onUpdateError}
                                onDelete={this.onDelete}
                                onDeleteError={this.onDeleteError}
                                resetErrors={this.clearErrors}
                            />
                            <Errors errors={this.state.errors} />
                            <NoErrors errors={this.state.errors} />
                        </ArticleGrid>
                        <ArticleGrid row center colClassName="controls">
                            <ProcessingButton icon="fa-check" buttonText="Vykonaj" onClick={this.apply} />
                            <Button icon="fa-undo" text="Vráť zmeny" onClick={this.undoAll} />
                            <Button icon="fa-plus-circle" text="Nový člen" onClick={() => this.setState({ open: true })} />
                        </ArticleGrid>
                    </Fragment>
                }
                {this.state.loading &&
                    <ArticleGrid row>
                        <Loader
                            className="article-loader"
                            loaderSize="10px"
                            diameter="150px"
                            animationSpeed="0.9s"
                            animationType="linear"
                            text="Loading ..."
                        />
                    </ArticleGrid>
                }
            </Fragment>
        );
    }
}

function Errors(props) {
    const { errors } = props;
    if (errors.isEmpty()) {
        return null;
    }

    return (
        <section className="table-messages">
            {
                errors.map((error) => (
                    <a className="text-danger text-center text-md-left d-block" href={`#membership-row-${error.index}`} key={error.key}>{error.message}</a>
                ))
            }
        </section>
    );
}

function NoErrors(props) {
    const { errors } = props;
    if (errors.isEmpty()) {
        return (
            <section className="table-messages">
                <p className="text-primary text-center text-md-left">Všetko je v poriadku <i className="fa fa-lg fa-check text-success"></i></p>
            </section>
        );
    }
    else {
        return null;
    }
}