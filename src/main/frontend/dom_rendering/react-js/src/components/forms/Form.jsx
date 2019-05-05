import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { StaticProcessingButton } from 'components/ProcessingButton';
import Button from 'components/Button';
import { Map } from 'immutable';

export default class Form extends Component {
    constructor(props) {
        super(props);
        this.state = {
            processing: false,
            focused: false,
            message: null,
            hasError: false,
        };

        this.onCancel = this.onCancel.bind(this);
        this.addInput = this.addInput.bind(this);
        this.inputs = Map();
    }

    addInput(input) {
        if (!input) {
            return input;
        }
        this.inputs = this.inputs.set(input.props.name, input);
        return input;
    }

    removeInput(input) {
        this.inputs = this.inputs.delete(input);
    }

    startProcessing() {
        this.setState({ processing: true });
        this.inputs.forEach( x => x.setProcessing(true));
    }

    finishedProcessing() {
        this.setState({ processing: false });
        this.inputs.forEach( x => x.setProcessing(false));
    }

    setSuccessMesage(message) {
        this.setState({
            message: message,
            hasError: false
        });
    }

    setErrorMesage(message) {
        this.setState({
            message: message,
            hasError: true,
        });
    }

    reset() {
        this.setState({
            processing: false,
            message: null,
            hasError: false,
            focused: false
        });
        if (this.props.clear) {
            this.inputs.forEach(input => input.reset());
        }
    }

    getData() {
        return new Promise((resolve, reject) => {
            const values = this.inputs.map((input, key) => {
                input.sync();
                const errors = input.validate();
                const value = input.getValue();
                if (!errors.isEmpty()) {
                    reject('Formulár obsahuje chyby!'); 
                }
                return value;
            });
            resolve(values.toJS());
        });
    }

    handleSubmit(event) {
        const { onSubmit, action, autoReset } = this.props;
        const { hasError, data } = this.state;

        if (action == null && onSubmit === null) {
            throw new Error('In form either action or onSubmit must be specified');
        }

        if (onSubmit === null) {
            return true;
        }

        event.preventDefault();
        this.startProcessing();
        this.getData().then(data => onSubmit(data)).then((message) => {
            if (!autoReset) {
                return message;
            }

            this.finishedProcessing();
            this.setSuccessMesage(message ? message : 'Úspešne som odoslal dáta');
            this.reset();
            return message;
        }).catch((error) => {
            console.error(error);
            this.finishedProcessing();
            this.setErrorMesage(error.message ? error.message : error);
        });
        return true;
    }

    onCancel() {
        if (this.props.clearOnCancel) {
            this.form.blur();
            this.reset();
        }
        this.props.onCancel();
    }

    onFocusChange(value) {
        return () => {
            this.setState({ focused: value });
        }
    }
  
    render() {
        const { className, title, titleClass, method, action, target, children, displayMessages, errorClass, messageClass, submit, cancel, submitAttributes, cancelAttributes, liveValidating } = this.props;
        const { message, hasError, processing, data } = this.state;
        return (
            <form ref={form => this.form = form} className={className} method={method} action={action} target={target}
                onSubmit={(event) => this.handleSubmit(event)} onFocus={this.onFocusChange(true)} onBlur={this.onFocusChange(false)}>
                {title &&
                    <legend className={titleClass}>{title}</legend>
                }
                {displayMessages && message &&
                    <Message hasError={hasError} errorClass={errorClass} messageClass={messageClass}>{message}</Message>
                }
                {children(this)}
                {(submit || cancel) &&
                    <div className="controls">
                        {submit &&
                            <StaticProcessingButton {...submitAttributes} type="submit" processing={processing} disabled={hasError && liveValidating} />
                        }
                        {cancel &&
                            <Button {...cancelAttributes} onClick={this.onCancel} />
                        }
                    </div>
                }
            </form>
        );
    }
}

function Message({ hasError, children, errorClass, messageClass }) {
    return (
        <p className={hasError ? errorClass : messageClass}>{children}</p>
    );
}

Form.defaultProps = {
    className: 'form',
    title: null,
    titleClass: 'form-title',
    method: 'get',
    target: null,
    displayMessages: false,
    errorClass: 'text-danger',
    messageClass: 'text-primary',
    liveValidating: false,
    clearOnCancel: false,
    clear: true,
    autoReset: true,
    submit: false,
    cancel: false,
    submitAttributes: {
        buttonText: 'Odoslať',
        statusText: 'Spracuvávam',
        type: 'submit'
    },
    cancelAttributes: {
        text: 'Resetnúť',
        icon: 'fa-times',
        theme: 'danger'
    },
    onSubmit: null,
    onCancel: () => { }
};

Form.propTypes = {
    children: PropTypes.func.isRequired
};