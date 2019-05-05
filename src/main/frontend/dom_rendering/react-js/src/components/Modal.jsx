import React from 'react';
import ReactModal from 'react-modal';
import ArticleGrid from 'layout/ArticleGrid';

const customStyles = {
    content: {
        left: 0,
        right: 0,
        top: '50%',
        bottom: 'auto',
        transform: 'translateY(-50%)',
        backgroundColor: 'transparent',
        border: 'none',
        width: '100%'
    },
    overlay: {
        zIndex: 2000,
    }
};

export default class Modal extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const { onClose, children, open, ...props } = this.props;

        return (
            <ReactModal
                isOpen={open}
                onRequestClose={onClose}
                closeTimeoutMS={0}
                style={customStyles}
                shouldCloseOnOverlayClick
                overlayClassName="modal-overlay"
            >
                <div className="container">
                    <ArticleGrid row center>
                        <div className="modal-majo">
                           {children}
                        </div>
                    </ArticleGrid>
                </div>
            </ReactModal>
        )
    }
}
