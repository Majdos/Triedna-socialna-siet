import React, { Fragment } from 'react';
import ProcessingButton from 'components/ProcessingButton';
import JsonForm from 'components/forms/JsonForm';
import Input from 'components/forms/Input';
import InputTextArea from 'components/forms/InputTextArea';
import Textarea from 'react-textarea-autosize';
import { StaticProcessingButton } from 'components/ProcessingButton';
import { formatUrlForStomp } from 'util/Utility';

export default class ArticleCommentForm extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        if (!isLoggedIn || !permissions.includes('WRITE')) {
            return null;
        }
        const { contentText, article, submitAttributes, className, placeholder, submit, cancel, minRows, rows } = this.props;

        const injectedFields = {
            article: formatUrlForStomp(article._links.self.href)
        };

        return (
            <JsonForm {...this.props} injectedFields={injectedFields} displayMessages={false}>
                {(form) =>
                    <Fragment>
                        {!submit && !cancel &&
                            <Input form={form} name="content" className="" placeholder={placeholder} value={contentText} min={2}>
                                <InputTextArea className={className} submitAttributes={submitAttributes} inputGroup />
                            </Input>
                        }
                        {(submit || cancel) &&
                            <Input form={form} name="content" className={className} placeholder={placeholder} value={contentText}>
                                <InputTextArea minRows={minRows} />
                            </Input>
                        }
                    </Fragment>
                }
            </JsonForm>
        );
    }
}

ArticleCommentForm.defaultProps = {
    method: 'post',
    action: '/api/articleComments',
    contentText: '',
    placeholder: 'Komentár',
    rows: 1,
    submit: false,
    cancel: false,
    submitAttributes: {
        buttonText: 'Odoslať',
        statusText: 'Odosielam'
    },
};