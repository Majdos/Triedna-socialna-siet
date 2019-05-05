import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Searchbar from 'components/Searchbar';
import Portal from 'components/Portal';
import Button from 'components/Button';
import { formatClassName } from 'util/ClassFormatter';

class BottomMenu extends Component {
    constructor(props) {
        super(props);
        this.state = {
            show: props.show
        }
        this.hide = this.hide.bind(this);
        this.show = this.show.bind(this);
    }

    hide() {
        if (this.props.onClose) {
            this.props.onClose();
        }
        this.setState({ show: false });
    }

    show() {
        this.setState({ show: true });
    }

    render() {
        const { target, onFind, notFound, reset, searchApi, children } = this.props;
        return (
            <Portal target={target}>
                <div className={formatClassName('bottom-search', this.state.show ? null : 'd-none')}>
                    <div className="container-fluid">
                        <Searchbar searchApi={searchApi}
                            id="group-search"
                            className="col-lg-4 offset-lg-4 col-md-6 offset-md-3"
                            onFind={onFind}
                            notFound={notFound}
                            reset={reset}
                            label="Hľadať"
                            placeholder="meno skupiny, kľúčové slová, privilégia v skupine"
                            showHelp />
                    </div>
                    <Button theme={null} icon="fa-times" onClick={this.hide} className="close-bottom-button" />
                </div>
                {!this.state.show &&
                    <div className="bottom-menu">
                        {children({show: this.show, hide: this.hide})}
                    </div>
                }
            </Portal>
        );
    }
}

BottomMenu.propTypes = {
    show: PropTypes.bool,
    target: PropTypes.string.isRequired,
    onFind: PropTypes.func,
    notFound: PropTypes.func,
    onClose: PropTypes.func,
    reset: PropTypes.func,
    searchApi: PropTypes.object.isRequired,
};

BottomMenu.defaultProps = {
    show: false
};

export default BottomMenu;