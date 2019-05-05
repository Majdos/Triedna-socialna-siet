import React from 'react';
import Article from './Article';
import ArticleGrid from 'layout/ArticleGrid';
import Alert from 'components/Alert';

export default function ArticleList(props) {
    if (!props.loading && props.articles.isEmpty()) {
        return (
            <ArticleGrid row >
                <Alert message="Zdá sa, že tu nič nie je. Budte prvý kto prispeje." buttonText="Ok" theme="primary" />   
            </ArticleGrid>
        )
    }

    const articles = props.articles.map(article =>
        <ArticleGrid row key={article._links.self.href}>                            
                <Article
                    article={article} 
                    onDelete={() => props.onDelete(article)}
                    onUpdate={props.onUpdate}
                    stompClient={props.stompClient}
                />     
        </ArticleGrid>
    );

    return (
        <div className="articles">   
            {articles}
        </div>
    );
}