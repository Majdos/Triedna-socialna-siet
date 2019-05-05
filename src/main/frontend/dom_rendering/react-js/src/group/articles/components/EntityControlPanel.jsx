import React, { Fragment } from 'react';
import PropTypes from 'prop-types';
import Button from 'components/Button';
import ProcessingButton from 'components/ProcessingButton';
import { canWrite, canDelete, isOwner } from 'util/Permissions';

export default class EntityControlPanel extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const { size, outlined, editTheme, deleteTheme, style, onEdit, onDelete, processing, showAuthorControls } = this.props;

        return (
            <Fragment>
                {canWrite(permissions) && showAuthorControls &&
                    <Button
                        text="Upraviť"
                        icon="fa-pencil-square-o"
                        outlined={outlined}
                        theme={editTheme}
                        size={size}
                        onClick={onEdit}
                    />
                }
                {(showAuthorControls || canDelete(permissions)) &&
                    <ProcessingButton
                        buttonText="Vymazať"
                        statusText="Odstraňujem"
                        icon="fa-times"
                        outlined={outlined}
                        theme={deleteTheme}
                        size={size}
                        onClick={onDelete}
                    />
                }
            </Fragment>
        );
    }
}

EntityControlPanel.propTypes = {
    onEdit: PropTypes.func.isRequired,
    onDelete: PropTypes.func.isRequired
}

EntityControlPanel.defaultProps = {
    buttonSize: '',
    outlined: false,
    editTheme: 'primary',
    deleteTheme: 'danger'
};
