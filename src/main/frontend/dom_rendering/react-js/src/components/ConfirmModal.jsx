import React, { Component } from 'react';
import Modal from 'components/Modal';
import PropTypes from 'prop-types';

class ConfirmModal extends Component {

    constructor(props) {
        super(props);
        this.handleAccept = this.handleAccept.bind(this);
    }

    handleAccept() {
        let promise = this.props.action();
        this.props.onClose();
        return promise;
    }

    render() {
        const { action, onClose, text, header, open } = this.props;
        return (
            <Modal open={open} onClose={onClose}>
                <div className="card">
                    <div className="card-body">
                        <h5 className="card-title">{header}</h5>
                        <p className="card-text">{text}</p>
                        <div className="controls">
                            <button className="btn btn-primary" onClick={this.handleAccept}>Áno <i className="fa fa-lg fa-check"></i></button>
                            <button className="btn btn-danger" onClick={onClose}>Nie <i className="fa fa-lg fa-times"></i></button>
                        </div>
                    </div>
                </div>
            </Modal>
        );
    }
}

ConfirmModal.propTypes = {
    action: PropTypes.func.isRequired,
    onClose: PropTypes.func.isRequired,
    text: PropTypes.string.isRequired,
    header: PropTypes.string.isRequired
};

export default ConfirmModal;
