import React, { PureComponent } from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';
import { StaticProcessingButton } from 'components/ProcessingButton';
import Button from 'components/Button';
import debounce from 'lodash.debounce';
import {pluralize} from 'util/String';

export default class Searchbar extends PureComponent {
    constructor(props) {
        super(props);
        this.state = {
            value: '',
            processing: false,
            searchResultCount: -1
        };
        this.searchApi = this.props.searchApi;
        this.onChange = this.onChange.bind(this);
        this.performChange = debounce(this.performChange, 250);
    }

    performChange(value) {
        this.setState({ value: value }, () => {
            if (this.props.liveSearch) {
                this.search();
            }
        });
    }

    onChange(event) {
        const target = event.target;
        const value = target.value;
        this.performChange(value);
    }

    search() {
        if (!this.state.value) {
            this.clear();
            return;
        }

        this.setState({ processing: true, searchResultCount: -1 });
        return this.searchApi.search(this.state.value).then(results => {
            this.setState({ processing: false, searchResultCount: results.length });
            return results;
        }).then(results => {
            if (results.length > 0) {
                this.props.onFind(results);              
            }
            else {
                this.props.notFound();
            }
        });
    }

    clear() {
        ReactDOM.findDOMNode(this.text).value = '';
        this.setState({
            value: '',
            searchResultCount: -1
        }, () => this.props.reset());
    }

    getHelpMessage() {
        const {searchResultCount, processing} = this.state;
        if (!this.props.showHelp) {
            return null;
        }

        if (searchResultCount === -1 && !processing) {
            return <small className="text-muted">Zadajte text pre vyhľadávanie</small>;
        }
        if (processing) {
            return <small className="text-primary">Vyhľadávam ...</small>;
        }
        if (searchResultCount === 0) {
            return <small className="text-danger">Nenašiel som ani jeden záznam</small>;
        }
        else {
            return null;
        }
    }

    render() {
        const { buttonText, className, statusText, icon, processingIcon, placeholder, liveSearch, label, id, showCancel } = this.props;

        return (
            <div className={className} id={`${id}-container`}>
                {label && <label htmlFor={id}>{label}</label>}
                <div className="input-group">
                    <input ref={text => this.text = text} id={id} className="form-control" onChange={this.onChange} defaultValue={this.state.value} placeholder={placeholder} />
                    <span className="input-group-btn">
                        {this.state.value && showCancel &&
                            <Button icon="fa-times" theme="danger" onClick={() => this.clear()} />
                        }
                        {(!liveSearch || !this.state.value) &&
                            <StaticProcessingButton buttonText={buttonText} statusText={statusText} icon={icon}
                                processingIcon={processingIcon} processing={this.state.processing} onClick={() => this.search()} />
                        }
                    </span>
                </div>
                {this.getHelpMessage()}
            </div>
        );
    }
}

Searchbar.defaultProps = {
    icon: 'fa-search',
    buttonText: null,
    statusText: null,
    liveSearch: false,
    label: null,
    showHelp: false,
    placeholder: null,
    showCancel: false,
    notFound: () => {},
    reset: () => {}
}

Searchbar.propTypes = {
    searchApi: PropTypes.object.isRequired,
    onFind: PropTypes.func.isRequired,
    notFound: PropTypes.func,
    reset: PropTypes.func
}