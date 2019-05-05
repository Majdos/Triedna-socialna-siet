import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';

export default class Portal extends Component {
    constructor(props) {
        super(props);
        this.target = document.getElementById(props.target);
        this.el = document.createElement('div');
        this.el.className = 'portal';
    }

    componentWillMount() {
        this.target.appendChild(this.el);
    }

    componentWillUnmount() {
        this.target.removeChild(this.el);
    }

    render() {
        return ReactDOM.createPortal(this.props.children, this.el);
    }
}

Portal.propTypes = {
    target: PropTypes.string.isRequired
};
