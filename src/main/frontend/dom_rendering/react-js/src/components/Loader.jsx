import React from 'react';
export default class Loader extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {

        const {loaderSize, diameter, animationSpeed, animationType, text, className} = this.props;

        const loaderStyle = {
            borderWidth: loaderSize,
            width: diameter,
            height: diameter,
            animation: `spin ${animationSpeed} ${animationType} infinite`  
        };

        return (
            <div className="loader-wrapper">
                <div className={`loader` + (className ? ' ' + className : '')} style={loaderStyle}></div>
                <p>{text}</p>
            </div>
        );
    }
}
