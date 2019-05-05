import React from 'react';
import ArticleCommentForm from '../forms/ArticleCommentForm';
import Comment from '../components/Comment';
import ProcessingButton from 'components/ProcessingButton';
import ErrorMessage from 'components/ErrorMessage';

export default class ArticleComments extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        if (!this.props.showComments && this.props.commentsCount > 0) {
            return null;
        }

        return (
            <section className="article-comments">
                <CommentsSection {...this.props} />
                <BottomPanel {...this.props} onSubmit={this.handleCommentSubmit} />
           </section>
        );
    }
}

function CommentsSection(props) {
    if (props.showComments) {
        return props.comments.map(comment => 
                <Comment 
                    article={props.article}
                    comment={comment}
                    key={comment._links.self.href}
                    onDelete={() => props.onDelete(comment)}
                    onUpdate={props.onUpdate}
                />
        )
    }
    else {
        return null;  
    }
}

class BottomPanel extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            error: null
        };
    }

    handleSuccess(error) {
        this.setState({
            error: null
        });
    }

    handleError(error) {
        this.setState({
            error: 'Nepodarilo sa nacitat dalsie komentare'
        });
    }

    render() {
        const { comments, commentsCount, onSubmit, showMore, article, openComments } = this.props;

        if (comments.size === commentsCount || comments.isEmpty()) {
            return <ArticleCommentForm article={article} />;
        }
        else if (comments.size > 0) {
            return (
                <React.Fragment>
                    { this.state.error &&
                        <ErrorMessage error={this.state.error} noMargin center />  
                    } 
                    <div className="more-comments-panel">
                        <ProcessingButton 
                            buttonSize="sm" 
                            buttonText="Načítať ďalšie komentáre" 
                            statusText="Načitávam ďalšie komentáre"
                            onClick={showMore}
                            onSuccess={() => this.handleSuccess()}
                            onError={(error) => this.handleError(error)}
                        />
                    </div>
                </React.Fragment>
            );
        }
        else {
            return null;
        }
    }
}