import React from 'react';
import PropTypes from 'prop-types';

export default class Alert extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            disposed: false
        };
    }

    handleConfirm() {
        Promise.resolve(this.props.onConfirm()).then(() => {
            this.setState({
                disposed: true
            });
        });
    }

    render() {
        if (this.state.disposed) {
            return null;
        }
        const { buttonText, message, icon, outlined, buttonSize, theme, onConfirm } = this.props;
        return (
             <div className={`alert alert-${theme} show d-flex justify-content-between align-items-center`}
                role="alert">
                <strong>{message}</strong>
                <button className={`ml-2 btn btn-${theme}`} type="button" aria-label={buttonText} onClick={() => this.handleConfirm()}>
                    <span aria-hidden="false">{this.props.buttonText}</span>
                </button>
            </div>
        );
    }
}

Alert.propTypes = {
    buttonText: PropTypes.string.isRequired,
    message: PropTypes.string.isRequired
}

Alert.defaultProps = {
    icon: '',
    outlined: false,
    buttonSize: null,
    theme: 'danger',
    onConfirm: () => {}
};