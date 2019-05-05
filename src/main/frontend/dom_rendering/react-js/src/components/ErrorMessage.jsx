import React from 'react';
export default class ErrorMessage extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const { error, marginBottom, noMargin, center } = this.props;
        const mb = noMargin ? '' : `mb-${marginBottom}`;
        if (error) {
            return (
                <p className={`${mb} text-danger ${center ? 'text-center' : ''}`}><i className="fa fa-exclamation-circle fa-lg" aria-hidden="true"></i> {error}</p>
            );
        }
        else {
            return null;
        }
    }
}

ErrorMessage.defaultProps = {
    error: 'Nepodarilo sa previest operaciu, skuste to neskor znova prosim',
    marginBottom: 4,
    noMargin: false,
    center: false
};