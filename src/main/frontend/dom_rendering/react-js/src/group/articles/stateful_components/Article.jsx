import React from 'react';
import ArticleData from '../components/ArticleData';
import ArticleComments from './ArticleComments';
import { List } from 'immutable';
import CommentSectionControlButton from '../components/CommentSectionControlButton';
import ArticleForm from '../forms/ArticleForm';
import dateFormat from 'config/DateFormat';
import { formatUrlForStomp, extractProtocolAndHostFromUrl } from 'util/Utility';
import { client } from 'rest_api/client';
import Paging from 'util/Paging';

const submitButtonConfig = {
    buttonText: 'Uložiť',
    statusText: 'Ukladám',
    icon: 'fa-save'
};

export default class Article extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            comments: List(),
            commentsCount: props.article.commentsCount,
            showComments: false,
            page: 0,
            pageSize: 5,
            links: {},
            editingArticle: false,
            loading: false
        }

        this.onArticleCommentCreate = this.onArticleCommentCreate.bind(this);
        this.onArticleCommentDelete = this.onArticleCommentDelete.bind(this);
        this.onArticleCommentUpdate = this.onArticleCommentUpdate.bind(this);

        this.showComments = this.showComments.bind(this);
        this.hideComments = this.hideComments.bind(this);
        this.editArticle = this.editArticle.bind(this);
        this.stopEditingArticle = this.stopEditingArticle.bind(this);

        this.fetchNextPage = this.fetchNextPage.bind(this);

        this.createArticleComment = this.createArticleComment.bind(this);
        this.updateArticleComment = this.updateArticleComment.bind(this);
        this.deleteArticleComment = this.deleteArticleComment.bind(this);

        this.paging = new Paging(this.state.pageSize);
    }

    fetchHypermediaPage(move) {
        if (move in this.state.links === false) {
            console.log('koniec');
            return;
        }

        return client({
            method: 'GET',
            path: this.state.links[move].href,
        }).then(commentsCollection => {
            this.setState((prev, props) => ({
                comments: prev.comments.concat(commentsCollection.entity._embedded.articleComments),
                links: commentsCollection.entity._links,
                page: commentsCollection.entity.page.number
            }));
        });
    }

    fetchNextPage() {
        if (this.paging.needsToRefetch()) {
            const pageResult = this.paging.calculateRefetchPage(this.state.page);
            return this.fetchPage(this.state.pageSize, pageResult.getPageNumber(), pageResult.getIgnoreCount()).then(commentCollection => {
                this.paging.resetDeleteCount();
                this.fetchPage(this.state.pageSize, pageResult.getPageNumber() + 1, 0);
            });
        }

        return this.fetchHypermediaPage('next');
    }

    fetchPage(pageSize, page = this.state.page, ignoreCount = 0) {
        return client({
            method: 'GET',
            path: this.props.article._links.comments.href,
            params: {
                page: page,
                size: pageSize,
            }
        }).then(commentCollection => {
            this.setState((prev, props) => ({
                comments: prev.comments.concat(commentCollection.entity._embedded.articleComments.slice(ignoreCount)),
                links: commentCollection.entity._links,
            }));
            return commentCollection;
        });
    }

    onArticleCommentCreate(newComment) {
        return client({
            method: 'POST',
            path: '/api/articleComments',
            entity: newComment,
            headers: { 'Content-Type': 'application/json' }
        }).then(() => {
            this.setState({
                showComments: true
            });
        });
    }

    onArticleCommentUpdate(comment, updatedComment) {
        return client({
            method: 'PUT',
            path: comment._links.self.href,
            entity: updatedComment,
            headers: { 'Content-Type': 'application/json' }
        });
    }

    onArticleCommentDelete(comment) {
        return client({ method: 'DELETE', path: comment._links.articleComment.href }).then(response => {

        }, error => {
            if (error.status.code === 403) {
                alert('Nemáte povolenie vymazať komentár ' +
                    comment._links.self.href);
                throw 'Nemáte povolenie vymazať komentár';
            }
            else {
                throw 'Nepodarilo sa vymazat komentar, skuste to neskor prosim';
            }
        });
    }

    showComments() {
        if (this.state.comments.isEmpty() || !this.state.links) {
            this.setState({
                loading: true
            });

            this.fetchPage(this.state.pageSize).then(commentCollection => {
                this.setState((prev, props) => ({
                    showComments: true,
                    loading: false
                }));
            });
        }
        else {
            this.setState({ showComments: true });
        }
    }

    hideComments() {
        this.setState({ showComments: false });
    }

    editArticle() {
        this.setState({ editingArticle: true });
    }

    stopEditingArticle() {
        this.setState({ editingArticle: false });
    }

    createArticleComment(message) {
        if (this.state.commentsCount > this.state.comments.size) {
            this.setState((prev, props) => ({
                commentsCount: prev.commentsCount + 1
            }));
            return;
        }

        this.props.stompClient.fetchFromMessage(message).then(response => {
            this.setState((prev, props) => ({
                comments: prev.comments.push(response.entity),
                commentsCount: prev.commentsCount + 1
            }));
        });
    }

    updateArticleComment(message) {
        this.props.stompClient.fetchFromMessage(message).then(response => {
            let comment = response.entity;
            const url = message.body;
            let key = this.state.comments.findIndex(x => formatUrlForStomp(x._links.articleComment.href) === url);
            if (key !== -1) {
                this.setState((prev, props) => ({
                    comments: prev.comments.set(key, comment)
                }));
            }
        });
    }

    deleteArticleComment(message) {
        const url = message.body;
        const key = this.state.comments.findIndex(x => formatUrlForStomp(x._links.articleComment.href) === url);
        if (key !== -1) {
            this.paging.incrementDeleteCount();
            this.setState((prev, props) => ({
                commentsCount: prev.commentsCount - 1,
                comments: prev.comments.remove(key)
            }));
        }
        else {
            this.setState((prev, props) => ({
                commentsCount: prev.commentsCount - 1
            }));
        }
    }

    formatRoute(type) {
        return '/topic/' + formatUrlForStomp(this.props.article._links.self.href) + '/' + type + 'Comment';
    }

    componentDidMount() {
        this.props.stompClient.register([
            { route: this.formatRoute('new'), callback: this.createArticleComment },
            { route: this.formatRoute('update'), callback: this.updateArticleComment },
            { route: this.formatRoute('delete'), callback: this.deleteArticleComment }
        ]).then(registrations => { this.subscriptions = registrations });
    }

    componentWillUnmount() {
        this.subscriptions.forEach(subsription => subsription.unsubscribe());
        this.subscriptions = null;
    }

    render() {
        let article = this.props.article;
        return (
            <article className="article-card p-md-4">
                {!this.state.editingArticle &&
                    <ArticleData
                        article={article}
                        onDelete={this.props.onDelete}
                        onEdit={this.editArticle}
                        commentsCount={this.state.commentsCount}
                        showComments={this.state.showComments}
                        toggleShow={this.showComments}
                        toggleHide={this.hideComments}
                    />
                }

                {this.state.editingArticle &&
                    <ArticleForm
                        method="put"
                        action={this.props.article._links.self.href}
                        onSuccess={this.stopEditingArticle}
                        onCancel={this.stopEditingArticle}
                        title="Úprava príspevku"
                        headerText={article.header}
                        contentText={article.text}
                        submitAttributes={submitButtonConfig}
                        submit cancel
                        autoReset={false}
                    >
                        {/* <CommentSectionControlButton
                            commentsCount={this.state.commentsCount}
                            showComments={this.state.showComments}
                            toggleShow={this.showComments}
                            toggleHide={this.hideComments}
                        /> */}
                    </ArticleForm>
                }

                <ArticleComments
                    comments={this.state.comments}
                    commentsCount={this.state.commentsCount}
                    showComments={this.state.showComments}
                    showMore={this.fetchNextPage}
                    article={this.props.article}
                    onDelete={this.onArticleCommentDelete}
                    onCreate={this.onArticleCommentCreate}
                    onUpdate={this.onArticleCommentUpdate}
                />
            </article>
        );
    }
}
