import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import ReactDOM from 'react-dom';
import Input from 'components/forms/Input';
import InputWrapper from 'components/forms/InputWrapper';
import InputDiv from 'components/forms/InputDiv';
import Button from 'components/Button';

export default class NumericTextInput extends Component {
    constructor(props) {
        super(props)
    }

    render() {
        const { processing, ...props } = this.props;
        return (
            <Input type="number" {...props} min={1} disabled={processing}>
                <InputDiv className="input-group">
                    <InputWrapper />
                    <InputDiv className="input-group-btn">
                        <Button icon="fa-check" type="submit" disabled={processing} />
                    </InputDiv>
                </InputDiv>
            </Input>
        );
    }
}

NumericTextInput.defaultProps = {
    label: null,
};

NumericTextInput.propTypes = {
    name: PropTypes.string.isRequired
}
