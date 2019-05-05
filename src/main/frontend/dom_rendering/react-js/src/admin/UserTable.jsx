import React from 'react';
import { capitalize } from 'util/String';
import UserRow from './UserRow';

export default class UserTable extends React.Component {

    constructor(props) {
        super(props);
        this.performApply = this.performApply.bind(this);
        this.undoAll = this.undoAll.bind(this);
        this.memberRows = [];
    }

    pushRow(input) {
        if (input !== null) {
            this.memberRows.push(input);
        }
    }

    updateUser(userRow) {
        if (userRow.props.membership.user === userLink) {
            return;
        }
        if (userRow.canDelete()) {
            return userRow.delete();
        }
        else if (userRow.hasBeenUpdated()) {
            return userRow.update();
        }
    }

    performApply() {
        this.props.resetErrors();
        const promises = this.memberRows.map(user => {
            return this.updateUser(user);
        });
        return Promise.all(promises);
    }

    undoAll() {
        this.props.resetErrors();
        this.memberRows.forEach(x => x.undo());
    }

    componentWillUpdate() {
        this.memberRows = [];
    }

    render() {
        const { memberships, principalMembership, client, onUpdate, onUpdateError, onDelete, onDeleteError } = this.props;
        let index = 0;
        const userRows = memberships.map((membership) => {
            const myself = principalMembership != null && membership._links.self.href === principalMembership._links.self.href;
            return (
                <UserRow
                    ref={input => this.pushRow(input)}
                    key={membership._links.self.href}
                    membership={membership}
                    index={index++}
                    principalMembership={principalMembership}
                    onUpdate={onUpdate}
                    onUpdateError={onUpdateError}
                    onDelete={onDelete}
                    onDeleteError={onDeleteError}
                    disabled={myself}
                />
            );
        });

        return (
            <table className="table table-spsjm table-responsive-md admin-table" id="userTable">
                <thead className="thead-spsjm">
                    <tr>
                        <th scope="col text-center">#</th>
                        <th scope="col">Meno</th>
                        <th scope="col">Priezvisko</th>
                        <PermissionHeaders />
                        <th scope="col" className="text-center">Akcie</th>
                    </tr>
                </thead>
                <tbody>
                    {userRows}
                </tbody>
            </table>
        );
    }
}

function PermissionHeaders(props) {
    return allPermissions.map(permission => (
        <th scope="col" className="text-center" key={permission}>{capitalize(permission)}</th>
    ));
}