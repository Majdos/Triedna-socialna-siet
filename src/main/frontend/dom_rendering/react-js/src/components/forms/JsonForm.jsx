import React from 'react';
import PropTypes from 'prop-types';
import Form from './Form';
import { client } from 'rest_api/client';
import { formatUrlForStomp } from 'util/Utility';

function handleSubmit(data, { action, method, headers, onSuccess, onError, injectedFields, handlers }) {
    return client({
        path: formatUrlForStomp(action),
        method: method,
        headers: headers,
        entity: Object.assign({}, data, injectedFields)
    }).then(response => {
        return onSuccess(response.entity, response);
    }).catch(error => {
        throw onError(error);
    });
}

export default function JsonForm(props) {
    return <Form {...props} onSubmit={(data) => handleSubmit(data, props)}>{props.children}</Form>
}

export function JsonFormCreate(props) {
    return <Form method="post" {...props} onSubmit={(data) => handleSubmit(data, props)}>{props.children}</Form>
}

export function JsonFormUpdate(props) {
    return <Form method="put" {...props} onSubmit={(data) => handleSubmit(data, props)}>{props.children}</Form>
}

const ownProps = {
    headers: {
        'Content-Type': 'application/json'
    },
    injectedFields: {},
    onSuccess: (entity, response) => { },
    onError: (error) => { }
}

JsonForm.defaultProps = Object.assign({}, Form.defaultProps, ownProps);
JsonForm.propTypes = Object.assign({}, Form.propTypes);

JsonFormCreate.defaultProps = Object.assign({}, JsonForm.defaultProps);
JsonFormCreate.propTypes = Object.assign({}, JsonForm.propTypes);

JsonFormUpdate.defaultProps = Object.assign({}, JsonForm.defaultProps);
JsonFormUpdate.propTypes = Object.assign({}, JsonForm.propTypes);
