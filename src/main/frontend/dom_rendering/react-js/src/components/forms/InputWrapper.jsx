import React, { PureComponent } from 'react';

export default class InputWrapper extends PureComponent {
    constructor(props) {
        super(props);
    }

    render() {
        const { inputRef, type, name, id, inputClassName, placeholder, value, required, onChange, onBlur, disabled, processing } = this.props;
        return (
            <input ref={inputRef}
                type={type} name={name} id={name} className={inputClassName}
                placeholder={placeholder} defaultValue={value}
                required={required} onBlur={onBlur} disabled={disabled || processing} />
        );
    }
}
