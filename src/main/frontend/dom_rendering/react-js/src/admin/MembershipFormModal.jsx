import React from 'react';
import ReactDOM from 'react-dom';
import Modal from 'components/Modal';
import ArticleGrid from 'layout/ArticleGrid';
import JsonForm from 'components/forms/JsonForm';
import Input from 'components/forms/Input';

const DEFAULT_PERMISSION = 'WRITE';

const injectedFields = {
    group: groupLink,
    permissions: [DEFAULT_PERMISSION],
    invited: true
};

const cancelAttributes = {
    text: 'Zatvoriť',
    icon: 'fa-times',
    theme: 'danger'
};

function handleError(error) {
    switch (error.status.code) {
        case 0:
            throw 'Nemáte prístup k Internetu';
        case 404:
            throw 'Užívateľ nebol nájdený';
        case 409:
            throw 'Užívateľ už je členom vašej skupiny';
        case 412:
            throw 'Niekto upravil dáta pred Vami, reloadnite stránku znova prosím';
        default:
            throw (error.status.code >= 500 ? 'Skúste to neskôršie prosím' : error.status.text);
    }
}

export default class MembershipFormModal extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const { onClose, open, ...props } = this.props;

        return (
            <Modal
                open={open}
                onClose={onClose}
            >
                <JsonForm {...props}
                    onCancel={onClose}
                    injectedFields={injectedFields}
                    onError={handleError}
                    cancelAttributes={
                        {
                            text: 'Zatvoriť',
                            icon: 'fa-times',
                            theme: 'danger'
                        }
                    }
                    displayMessages>
                    {(form) =>
                        <Input form={form} type="email" name="email" label="Email nového člena" />
                    }
                </JsonForm>
            </Modal>
        )
    }
}
