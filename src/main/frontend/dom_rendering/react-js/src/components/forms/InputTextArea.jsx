import Textarea from 'react-textarea-autosize';
import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { StaticProcessingButton } from 'components/ProcessingButton';
import { formatClassName } from 'util/ClassFormatter';

export default class InputTextArea extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const { submitAttributes, processing, inputGroup, className, inputClassName, inputRef, ...inputProps } = this.props;

        if (inputGroup) {
            return (
                <div className="input-group">
                    <Textarea ref={inputRef} className={inputClassName} {...inputProps} />
                    <span className="input-group-btn">
                        <StaticProcessingButton {...submitAttributes} type="submit" processing={processing} />
                    </span>
                </div>
            )
        }
        else {
            return <Textarea ref={inputRef} className={inputClassName} {...inputProps} />;
        }
    }
}

InputTextArea.defaultProps = {
    type: 'text',
    minRows: 1,
    inputGroup: false,
    submitAttributes: {
        buttonText: 'Odoslať',
        statusText: 'Spracuvávam',
        type: 'submit'
    },
}