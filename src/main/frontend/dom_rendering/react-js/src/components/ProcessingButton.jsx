import React from 'react';
import PropTypes from 'prop-types';
import Button from './Button';

export default class ProcessingButton extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            processing: false,
            error: null
        };
        this.handleClick = this.handleClick.bind(this);
        this.mounted = false;
    }

    componentDidMount() {
        this.mounted = true;
    }

    componentWillUnmount() {
        this.mounted = false;
    }

    handleError(error) {
        if(!this.mounted) {
            return;
        }
        this.setState({
            processing: false,
            error: error
        });
        this.props.onError(error);
    }

    handleSuccess() {
        if(!this.mounted) {
            return;
        }

        this.setState({
            processing: false,
            error: null
        });
        this.props.onSuccess();
    }

    handleClick() {
        if (!this.props.onClick) {
            return;
        }

        this.setState({ processing: true });
        Promise.resolve(this.props.onClick()).then(() => this.handleSuccess()).catch((error) => this.handleError(error));
    }

    render() {
        return <StaticProcessingButton {...this.props} processing={this.state.processing} onClick={this.handleClick} />
    }
}

export function StaticProcessingButton(props) {
    const { onClick, outlined, theme, size, buttonText, statusText, icon, processingIcon, disabled, type, processing } = props;
    if (!processing) {
        return (
            <Button type={type} text={buttonText} icon={icon} outlined={outlined} theme={theme} size={size} onClick={onClick} disabled={disabled} />
        );
    }
    else {
        return (
            <Button type={type} text={statusText} icon={processingIcon} outlined={outlined} theme={theme} size={size} onClick={onClick} disabled />
        );
    }
}

ProcessingButton.defaultProps = {
    buttonText: 'Process',
    statusText: 'Spracuvavam',
    type: 'button',
    icon: '',
    processingIcon: 'fa-circle-o-notch fa-spin fa-lg fa-fw',
    outlined: false,
    size: null,
    theme: 'primary',
    disabled: false,
    processing: false,
    onSuccess: () => {},
    onError: (error) => {},
};

StaticProcessingButton.defaultProps = ProcessingButton.defaultProps;