import React from 'react';
import ReactMarkdown from 'react-markdown';
import CommentSectionControlButton from './CommentSectionControlButton';
import SplitDiv from 'components/SplitDiv';
import EntityControlPanel from './EntityControlPanel';
import ProcessingStatus from 'components/ProcessingStatus';
import ErrorMessage from 'components/ErrorMessage';

export default class ArticleData extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            processingDelete: false,
            error: null
        }

        this.handleDeleteClick = this.handleDeleteClick.bind(this);
        this.handleEditClick = this.handleEditClick.bind(this);

        this.mounted = false;
    }

    setError(error) {
        this.setState({
            error: error
        });
    }

    resetDeleteState() {
        this.setState({
            processingDelete: false,
            error: null
        });
    }

    handleEditClick() {
        this.setState({error: null});
        this.props.onEdit();
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
        return (
            <React.Fragment>
                <ArticleHeader header={this.props.article.header} />
                <ProcessingStatus statusText="Vymazavam" processing={this.state.processingDelete} noMargin />
                <ErrorMessage error={this.state.error} noMargin />
                <ArticleSubheader author={this.props.article.author} />
                <ArticleText text={this.props.article.text} />                 
                <ArticleBottomPanel 
                    onDelete={this.handleDeleteClick} 
                    onEdit={this.handleEditClick}
                    processing={this.state.processingDelete}
                    commentsCount={this.props.commentsCount} 
                    showComments={this.props.showComments}
                    showAuthorControls={this.props.article.showAuthorControls} 
                    toggleShow={this.props.toggleShow} 
                    toggleHide={this.props.toggleHide}
                />
            </React.Fragment>
        ); 
    }
}

function ArticleHeader(props) {
    return (
        <h2 className="news-title text-left mr-auto">
            {props.header}
        </h2>
    );
}

function ArticleSubheader(props) {
    let author = props.author;  
    return (
        <span>
            <p className="news-subtitle text-muted">
                <span> od </span> 
                <a href={"/users/" + author.firstname + author.lastname}>
                    {author.lastname}
                </a>
            </p>
        </span>
    );
}

function ArticleText(props) {
    return (
        <React.Fragment>
            <ReactMarkdown source={props.text} className="markdown" />
        </React.Fragment>
    );
}

function ArticleBottomPanel(props) {
    const {onEdit, onDelete, processing, showAuthorControls} = props;
    return (
        <SplitDiv className="control-split-div"
            leftElements={
                <EntityControlPanel onEdit={onEdit} onDelete={onDelete} processing={processing} showAuthorControls={showAuthorControls} />
                
            } 
            rightElements={
                <CommentSectionControlButton {...props} />
            }
        />
    );
}
