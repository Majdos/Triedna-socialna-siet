import React from 'react';
import { List } from 'immutable';
import ArticleForm from './forms/ArticleForm';
import ArticleList from './stateful_components/ArticleList';
import dateFormat from 'config/DateFormat';
import ArticleGrid from 'layout/ArticleGrid';
import Alert from 'components/Alert';
import Loader from 'components/Loader';
import StompClient from 'rest_api/WebsocketListener';
import { client } from 'rest_api/client';
import { redirect, formatUrlForStomp } from 'util/Utility';
import Paging from 'util/Paging';
import Portal from 'components/Portal';
import Searchbar from 'components/Searchbar';
import SearchApi from 'js-worker-search';

const stompClient = new StompClient('/spsjm-social');

export default class ArticleContainer extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            articles: List(),
            pageSize: 15,
            page: 0,
            links: {},
            groupLink: groupLink,
            startTime: dateFormat(new Date(), 'springTimestamp'),
            loadingArticles: true,
            searchResults: List()
        };

        this.fetchPage = this.fetchPage.bind(this);
        this.onDelete = this.onDelete.bind(this);

        this.createArticleStomp = this.createArticleStomp.bind(this);
        this.updateArticleStomp = this.updateArticleStomp.bind(this);
        this.deleteArticleStomp = this.deleteArticleStomp.bind(this);
        this.onFind = this.onFind.bind(this);

        this.lastScrollUpdate = 0;
        this.paging = new Paging(this.state.pageSize);
        this.searchApi = new SearchApi();
    }

    performHypermediaFetch(move) {
        this.setState({ loadingArticles: true });

        return client({
            method: 'GET',
            path: this.state.links[move].href
        }).then(articleCollection => {
            this.setState((prev, props) => ({
                articles: prev.articles.concat(articleCollection.entity._embedded.articles),
                links: articleCollection.entity._links,
                loadingArticles: false,
                page: articleCollection.entity.page.number
            }));
        });
    }

    fetchHypermediaPage(move) {
        if (this.paging.needsToRefetch()) {
            const pageResult = this.paging.calculateRefetchPage(this.state.page);
            return this.fetchPage(this.state.pageSize, pageResult.getPageNumber(), pageResult.getIgnoreCount()).then(articleCollection => {
                this.paging.resetDeleteCount();
                this.fetchPage(this.state.pageSize, pageResult.getPageNumber() + 1, 0);
            });
        }

        return this.performHypermediaFetch(move);
    }

    fetchNextPage() {
        this.fetchHypermediaPage('next');
    }

    fetchPage(pageSize, page, ignoreCount) {
        ignoreCount = ignoreCount || 0;
        page = page || this.state.page;

        this.setState({ loadingArticles: true });
        return client({
            method: 'GET',
            path: this.state.groupLink + '/articles',
            params: {
                page: page,
                size: pageSize,
                sort: 'publicationDate,desc',
                timestamp: this.state.startTime
            }
        }).then(articleCollection => {
            if (!articleCollection.entity._embedded || !articleCollection.entity._embedded.articles) {
                console.log("Zda sa, ze tu niec nie je.");
                return null;
            }

            const articles = articleCollection.entity._embedded.articles;
            this.setState((prev, props) => ({
                articles: prev.articles.concat(articles.slice(ignoreCount)),
                links: articleCollection.entity._links,
            }));
            return articleCollection.entity._embedded.articles;
        }).then(articles => {
            this.setState({
                loadingArticles: false
            });
            if (articles) {
                articles.forEach(article => {
                    //TODO: klucove slova
                    this.searchApi.indexDocument(article._links.self.href, [article.header, article.publicationDate, article.author.fullname].join(' '));
                });
            }
        });
    }

    onDelete(article) {
        return client({ method: 'DELETE', path: article._links.self.href }).then(response => { }, error => {
            if (error.status.code === 403) {
                alert('Nemáte povolenie vymazať príspevok ' +
                    article._links.self.href);
                throw 'Nemáte povolenie vymazať príspevok';
            }
            else {
                throw 'Nepodarilo sa vymazat prispevok, skuste to znova prosim';
            }
        });
    }

    onFind(results) {

    }

    updateArticleStomp(message) {
        stompClient.fetchFromMessage(message).then(response => {
            const article = response.entity;
            const url = message.body;
            const index = this.state.articles.findIndex(article => formatUrlForStomp(article._links.self.href) === url);
            this.setState((prev, props) => ({
                articles: prev.articles.set(index, article)
            }));
        });
    }

    createArticleStomp(message) {
        stompClient.fetchFromMessage(message).then(response => {
            const article = response.entity;
            this.setState((prev, props) => ({
                articles: prev.articles.insert(0, article)
            }));
        });
    }

    deleteArticleStomp(message) {
        this.paging.incrementDeleteCount();
        const url = message.body;
        this.setState((prev, props) => ({
            articles: prev.articles.remove(prev.articles.findIndex(article => formatUrlForStomp(article._links.self.href) === url))
        }));
    }

    componentDidMount() {
        // nacitaj prvu stranku
        this.fetchPage(this.state.pageSize);

        stompClient.register([
            { route: `/topic/${this.state.groupLink}/newArticle`, callback: this.createArticleStomp },
            { route: `/topic/${this.state.groupLink}/updateArticle`, callback: this.updateArticleStomp },
            { route: `/topic/${this.state.groupLink}/deleteArticle`, callback: this.deleteArticleStomp }
        ]);

        window.addEventListener('scroll', () => {
            if (this.needsUpdate()) {
                this.fetchNextPage();
            }
        });
    }

    render() {
        return (
            <React.Fragment>
                <FormArea loggedIn={isLoggedIn} clearOnCancel />
                <ArticleList
                    loading={this.state.loadingArticles}
                    stompClient={stompClient}
                    articles={this.state.articles}
                    onDelete={this.onDelete}
                    onUpdate={this.onUpdate}
                />
                {this.state.loadingArticles &&
                    <ArticleGrid row>
                        <Loader
                            className="article-loader"
                            loaderSize="10px"
                            diameter="150px"
                            animationSpeed="0.9s"
                            animationType="linear"
                            text="Loading ..."
                        />
                    </ArticleGrid>
                }
            </React.Fragment>
        );
    }
    needsUpdate() {
        if (this.state.loadingArticles || !this.state.links.next || this.lastScrollUpdate + 450 > Date.now()) {
            return false;
        }

        const scrollY = document.documentElement.clientHeight + window.scrollY;
        const bottomY = document.documentElement.scrollHeight || document.documentElement.clientHeight;
        this.lastScrollUpdate = Date.now();

        if (scrollY >= bottomY * 0.75) {
            return true;
        }
        else {
            return false;
        }
    }
}

function FormArea(props) {
    if (props.loggedIn) {
        return (
            <ArticleGrid row>
                <ArticleForm header="Novy prispevok" clearOnCancel />
            </ArticleGrid>
        );
    }
    else {
        return (
            <ArticleGrid row>
                <Alert theme="danger" message="Pre vytvorenie clanku sa treba prihlasit!" buttonText="Login" onConfirm={() => redirect('/login')} />
            </ArticleGrid>
        );
    }
}