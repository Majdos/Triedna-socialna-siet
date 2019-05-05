import React, { Fragment } from 'react';
import dateFormat from 'dateformat';
import Input from 'components/forms/Input';
import InputTextArea from 'components/forms/InputTextArea';
import FormControls from './FormControls';
import { redirect } from 'util/Utility';
import Alert from 'components/Alert';
import JsonForm from 'components/forms/JsonForm';

const defaultRows = 5;


export default class ArticleForm extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        if (!isLoggedIn || !permissions.includes('WRITE')) {
            return null;
        }

        const { method, action, title, headerText, contentText, children } = this.props;

        return (
            <JsonForm {...this.props} injectedFields={{ group: groupLink }} submit cancel>
                {(form) =>
                    <Fragment>
                        <Input form={form} value={headerText} label="Názov článku" placeholder="Názov" help="minimálne 5 znakov" name="header" min={5} required />
                        <Input form={form} value={contentText} label="Obsah" placeholder="Obsah" help="minimálne 5 znakov" name="text" min={5} required>
                            <InputTextArea minRows={5} />
                        </Input>
                    </Fragment>
                }
            </JsonForm>
        );
    }
}

ArticleForm.defaultProps = {
    method: 'post',
    action: '/api/articles',
    title: '',
    contentText: '',
    headerText: '',
    submitAttributes: {
        buttonText: 'Odoslať',
        statusText: 'Spracuvávam',
        type: 'submit'
    },
    cancelAttributes: {
        text: 'Zrušiť',
        type: 'reset',
        icon: 'fa-ban',
        theme: 'danger'
    }
};