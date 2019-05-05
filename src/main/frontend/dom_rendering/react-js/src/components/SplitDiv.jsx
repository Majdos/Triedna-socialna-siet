import React from 'react';
export default class SplitDiv extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className={'split-div' + (this.props.className ? ' ' + this.props.className : '')}>
                <div className="split-div-left">
                    {this.props.leftElements}
                </div>
                <div className="split-div-right">
                    {this.props.rightElements}
                </div>
            </div>
        );
    }
}