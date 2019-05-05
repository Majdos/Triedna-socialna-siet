import React from 'react';
import ProcessingButtom from 'components/ProcessingButton';

export default class CommentSectionControlButton extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const { commentsCount, showComments, toggleShow, toggleHide } = this.props;
        if (commentsCount == 0) {
            return ( 
                <p className="text-muted mb-0">Bez komentarov</p>
            );
        }
        
        else if (!showComments) {
            return ( 
                <button type="button" className="btn btn-primary" onClick={toggleShow}>
                    Komentáre <span className="badge badge-pill bg-white text-primary">{commentsCount}</span>
                </button>
            );
        }
        else {
            return <button type="button" className="btn btn-primary" onClick={toggleHide}>Skry komentáre</button>;
        }
    }
}