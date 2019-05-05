import React, { Fragment } from 'react';
import {formatClassName} from 'util/ClassFormatter';
export default class Button extends React.PureComponent {

    constructor(props) {
        super(props);
    }

    outline() {
        return this.props.outlined ? 'outline-' : '';
    }

    theme() {
        if (this.props.theme == null) {
            return null;
        }
        return `btn-${this.outline()}${this.props.theme}`;
    }

    btnSize() {
        return this.props.size ? `btn-${this.props.size}` : null;
    }

    render() {
        const { onClick, size, text, icon, disabled, type, children, className } = this.props;
        return (
            <button type={type} className={formatClassName(className, 'btn', this.theme(), this.btnSize())} onClick={onClick} disabled={disabled}>
                { (text || icon) &&
                    <Fragment>
                        {text} <i className={`fa fa-lg ${icon}`} aria-hidden="true"></i>
                    </Fragment>
                }
                { !text &&
                    <Fragment>
                        {children}
                    </Fragment>
                }
            </button>
        );
    }

}

Button.defaultProps = {
    type: 'button',
    icon: null,
    text: null,
    outlined: false,
    size: null,
    theme: 'primary',
    className: ''
};