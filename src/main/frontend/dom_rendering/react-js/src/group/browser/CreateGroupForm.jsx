import React, { Fragment } from 'react';
import JsonForm from 'components/forms/JsonForm';
import Input from 'components/forms/Input';
import Modal from 'components/Modal';

const GROUP_NAME_PATTERN = /^[a-zA-Z][A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$/;
const PATTERN_HELP = 'Meno skupiny môže obsahovať len písmená abecedy a znaky \"-_\". Prvý znak musí byť písmeno.';
const KEYWORD_LIMIT = 20;

function validate(value) {
    const values = value.split(/\s+/);
    for (const keyword of values) {
        if (keyword.length > KEYWORD_LIMIT) {
            return `Kľúčové slovo nemôže presahovať ${KEYWORD_LIMIT} ${pluralize(KEYWORD_LIMIT, 'znak', 'znaky', 'znakov')}`;
        }
    }
    return null;
}

function parse(value) {
    return value.split(/\s+/);
}

function handleError(error, invited = false) {
    let message;
    switch (error.status.code) {
        case 403:
            message = 'Nemate právo vytvoriť skupinu';
            break;
        case 409:
            message = 'Skupina už existuje';
            break;
        default:
            message = error;
            break;
    }
    return message;
}

export function CreateGroupModal({ closeHandler, open }) {
    return (
        <Modal
            open={open}
            onClose={closeHandler}>
            <JsonForm
                autoReset={false}
                method="post"
                action="/api/groups"
                title="Vytvoriť skupinu"
                onCancel={closeHandler}
                onSuccess={closeHandler}
                onError={handleError}
                cancelAttributes={
                    {
                        text: 'Zatvoriť',
                        icon: 'fa-times',
                        theme: 'danger'
                    }
                }
                displayMessages
                submit
                cancel>
                {(form) =>
                    <Fragment>
                        <Input form={form} type="text" name="name" label="Meno skupiny" pattern={GROUP_NAME_PATTERN} patternHelp={PATTERN_HELP} max={25} required />
                        <Input form={form} type="text" name="description" label="Popis skupiny" max={50} />
                        <Input form={form} parse={parse} placeholder="kľúčové slová oddelené medzerami" type="text" name="keywords" label="Kľučové slova skupiny" max={200} validation={validate} />
                    </Fragment>
                }
            </JsonForm>
        </Modal>
    );
}

export default CreateGroupModal;