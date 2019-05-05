import React from 'react';
import Textarea from 'react-textarea-autosize';
import dateFormat from 'dateformat';
import ArticleCommentFrom from '../forms/ArticleCommentForm';
import EntityControlPanel from './EntityControlPanel';
import ProcessingStatus from 'components/ProcessingStatus';
import ErrorMessage from 'components/ErrorMessage';
import { canDelete } from 'util/Permissions';

const submitButtonConfig = {
    buttonText: 'Uložiť',
    icon: 'fa-save',
    size: 'sm'
};

const cancelButtonConfig = {
    text: 'Zrušiť',
    theme: 'danger',
    icon: 'fa-ban',
    size: 'sm'
};


export default class Comment extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            editing: false,
            show: true,
            processingDelete: false,
            error: null
        }

        this.startEditing = this.startEditing.bind(this);
        this.stopEditing = this.stopEditing.bind(this);
        this.handleDeleteClick = this.handleDeleteClick.bind(this);
        this.mounted = false;
    }

    startEditing() {
        this.setState({editing: true, error: null});
    }

    stopEditing() {
        this.setState({editing: false});
    }

    resetDeleteState() {
        this.setState({
            processingDelete: false,
            error: null
        });
    }

    handleDeleteSuccess() {
        /** TODO: Vymazat ? */
    }

    handleDeleteError(error) {
        if (!this.mounted) {
            return;
        }

        if (typeof error !== 'string') {
            error = 'Nepodarilo sa odoslat data na server';
        }

        this.setState({
            processingDelete: false,
            error: error
        });
    }

    handleDeleteClick() {
        if (!this.mounted) {
            return;
        }
        this.resetDeleteState();
        this.setState({processingDelete: true});
        this.props.onDelete().then(() => this.handleDeleteSuccess()).catch((error) => this.handleDeleteError(error));
    }

    componentDidMount() {
        this.mounted = true;
    }

    componentWillUnmount() {
        this.mounted = false;
    }

    render() {
        const comment = this.props.comment;

        return (
            <div className="card">
                <div className="card-body">
                    <Title comment={comment} />
                    <ProcessingStatus statusText="Vymazavam" processing={this.state.processingDelete} />
                    <ErrorMessage error={this.state.error} />
                    <Body
                        onCancel={this.stopEditing}
                        onSuccess={this.stopEditing} 
                        comment={comment}
                        editing={this.state.editing}
                        article={this.props.article}
                    />
                    <Controls 
                        showAuthorControls={comment.showAuthorControls} 
                        onDelete={this.handleDeleteClick}
                        onEdit={this.startEditing}
                        comment={comment}
                        editing={this.state.editing}
                        processingDelete={this.state.processingDelete}
                    />
                </div>
            </div>
        );
    }
}

function Title(props) {
    const comment = props.comment;
    
    return (
        <section>
            <a className="card-subtitle text-primary mr-2" href={comment._links.schoolUser} rel="author">{comment.author.firstname + ' ' + comment.author.lastname}</a>
            <span className="text-muted mr-2">v</span>
            <time className="card-subtitle text-muted">{dateFormat(new Date(comment.postedTime), 'dd.mm.yyyy HH:MM:ss')}</time>
        </section>
    );
}

function Body(props) {
    const comment = props.comment;

    if (props.editing) {
        return (
            <ArticleCommentFrom
                method="put"
                action={comment._links.self.href}
                submitAttributes={submitButtonConfig}
                cancelAttributes={cancelButtonConfig}
                contentText={comment.content}
                onCancel={props.onCancel}
                onSuccess={props.onSuccess}
                article={props.article}
                submit cancel
                autoReset={false}
            />
        );
    }

    return (
        <article className="card-text">
            {comment.content}
        </article>
    );
}

function Controls(props) {
    if ((props.showAuthorControls || canDelete(permissions)) && !props.editing) {
        return (
            <section className="comment-controls">
                <EntityControlPanel 
                    onEdit={props.onEdit}
                    onDelete={props.onDelete}
                    processing={props.processingDelete}
                    showAuthorControls={props.showAuthorControls}
                    size="sm"
                />
            </section>
        );
    }
    else {
        return null;
    }
}