import React from 'react';
import { formatClassName } from 'util/ClassFormatter';

export default class Dropdown extends React.PureComponent {
    constructor(props) {
        super(props);
        this.state = {
            toggled: false,
        };
        this.handleClick = this.handleClick.bind(this);
        this.show = this.show.bind(this);
        this.hide = this.hide.bind(this);
    }

    handleClick() {
        this.setState((prevState, props) => ({
            toggled: !prevState.toggled,
        }));
    }

    show(){
        this.setState({toggled: true});
    }

    hide() {
        this.setState({toggled: false});
    }

    render() {
        const { element, type, children, show } = this.props;
        const el = React.Children.only(element);
        return (
            <div className={formatClassName('position-relative', type, this.state.toggled || show ? 'show' : '')}>
                {React.cloneElement(el, { onClick: this.handleClick, show: this.show, hide: this.hide })}
                <div className={formatClassName('dropdown-menu', this.state.toggled || show ? 'show' : '')}>
                    {children}
                </div>
            </div>
        );
    }
}

Dropdown.defaultProps = {
    type: 'dropwdown',
    show: false
};