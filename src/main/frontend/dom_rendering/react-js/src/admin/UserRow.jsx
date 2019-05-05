import React from 'react';
import ReactDOM from 'react-dom';
import Button from 'components/Button';
import { List, Map } from 'immutable';
import { formatUrlForStomp } from 'util/Utility';
import { client } from 'rest_api/client';
import { isUser, isAdmin, isOwner } from 'util/Permissions';

export default class UserRow extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            startPermissions: List(props.membership.permissions),
            deleted: false,
            error: null
        };

        this.toggleDelete = this.toggleDelete.bind(this);
        this.undo = this.undo.bind(this);
        this.hasBeenUpdated = this.hasBeenUpdated.bind(this);
        this.canDelete = this.canDelete.bind(this);
        this.update = this.update.bind(this);
        this.delete = this.delete.bind(this);
        this.inputs = Map();
    }

    getPermissionsFromInputs() {
        let temp = [];
        this.inputs.forEach((element, key) => {
            if (ReactDOM.findDOMNode(element).checked) {
                temp.push(key);
            }
        });
        return List(temp);
    }

    formatErrorObject(message) {
        return { message: message, index: this.props.index, key: this.props.membership._links.self.href };
    }

    isCurrentUserSuperior() {
        const { membership, principalMembership } = this.props;
        return principalMembership == null || membership.authorityLevel < principalMembership.authorityLevel && isAdmin(principalMembership.permissions);
    }

    toggleDelete() {
        this.setState((prev) => ({
            deleted: !prev.deleted
        }));
    }

    undo() {
        this.inputs.forEach((input, key) => {
            ReactDOM.findDOMNode(input).checked = this.state.startPermissions.includes(key);
        });
        this.setState({
            deleted: false,
            error: null
        });
    }

    delete() {
        const { membership, onDelete, onDeleteError, index } = this.props;
        return client({
            method: 'DELETE',
            path: membership._links.self.href
        }).then(response => {
            onDelete(membership);
        }, error => {
            this.setState({ error: this.formatErrorObject(`Nepodarilo sa vymazat riadok cislo ${index + 1}`) });
            onDeleteError(this.state.error, membership);
        });
    }

    update() {
        const { membership, onUpdate, onUpdateError, index } = this.props;

        const updatedEntity = {
            group: formatUrlForStomp(membership._links.groups.href),
            user: formatUrlForStomp(membership._links.user.href.split("{")[0]),
            permissions: this.getPermissionsFromInputs()
        };

        return client({
            method: 'PUT',
            path: membership._links.self.href,
            headers: { 'Content-Type': 'application/json' },
            entity: updatedEntity
        }).then(response => {
            this.setState({
                startPermissions: this.getPermissionsFromInputs()
            });
            onUpdate(membership);
        }, error => {
            this.setState({ error: this.formatErrorObject(`Nepodarilo sa aktualizovat riadok cislo ${index + 1}`) });
            onUpdateError(this.state.error, membership);
        })
    }

    hasBeenUpdated() {
        const startPermissions = this.state.startPermissions;
        const currentPermissions = this.getPermissionsFromInputs();
        return !startPermissions.equals(currentPermissions);
    }

    canDelete() {
        return this.state.deleted;
    }

    getStatusClass() {
        if (this.state.error) {
            return 'table-danger';
        }
        else if (this.state.deleted) {
            return 'row-fade';
        }
        else if (this.props.disabled) {
            return 'text-muted';
        }
        else {
            return '';
        }
    }

    /**
     * Zisti ci je admin alebo sa jedna len o bug ¯\_(ツ)_/¯
     * @param {*} membership clen na skontrolovanie
     */
    isSuperUser(membership) {
        return membership == null;
    }

    isCommonOrAdminUser(membership) {
        if (membership == null) {
            return false;
        }
        return isUser(membership.permissions) || isAdmin(membership.permissions);
    }

    shouldDisablePermissionCheckbox(permission) {
        const {principalMembership} = this.props;
        if (this.isSuperUser(principalMembership)) {
            return false;
        }
        return !this.isCurrentUserSuperior() || permission === 'ROOT' || (!isOwner(principalMembership.permissions) && permission === 'ADMIN');
    }

    render() {
        const { membership, index, principalMembership, disabled } = this.props;
        const { user, permissions, authorityLevel } = membership;

        return (
            <tr className={this.getStatusClass()} id={`membership-row-${index}`} disabled={disabled}>
                <th scope="row" className="align-middle">{index + 1}</th>
                <td className="align-middle">{user.firstname}</td>
                <td className="align-middle">{user.lastname}</td>
                {
                    allPermissions.map((permission, index) => (
                        <td key={permission} className="text-center align-middle">
                            <input
                                ref={input => this.inputs = this.inputs.set(permission, input)}
                                type="checkbox"
                                defaultChecked={permissions.includes(permission)}
                                disabled={this.shouldDisablePermissionCheckbox(permission)}
                            />
                        </td>
                    ))
                }
                <td className="text-center controls">
                    {!this.state.deleted &&
                        <Button theme="danger" outlined icon="fa-times" onClick={this.toggleDelete} disabled={!this.isCurrentUserSuperior()} />
                    }
                    {this.state.deleted &&
                        <Button theme="danger" outlined icon="fa-plus" onClick={this.toggleDelete} disabled={!this.isCurrentUserSuperior()} />
                    }
                    <Button theme="primary" outlined icon="fa-undo" onClick={this.undo} disabled={this.state.delete || !this.isCurrentUserSuperior()} />
                </td>
            </tr>
        );
    }
} 
