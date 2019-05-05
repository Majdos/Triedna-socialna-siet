import React from 'react';
export default class ProcesingStatus extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const { children, processing, marginBottom, noMargin } = this.props;
        const mb = noMargin ? '' : `mb-${marginBottom}`;
        if (processing) {
            return (
                <p className={`${mb} text-primary`}>{children} <i className="fa fa-circle-o-notch fa-spin fa-lg fa-fw"></i></p>          
            );
        }
        else {
            return null;
        }
    }
}

ProcesingStatus.defaultProps = {
    children: 'Spracuvavam',
    marginBottom: 4,
    noMargin: false
};