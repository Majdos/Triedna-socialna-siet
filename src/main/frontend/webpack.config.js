const CopyWebpackPlugin = require('copy-webpack-plugin');
const webpack = require('webpack');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

const path = require('path');

const APP_DIR = path.resolve(__dirname, 'dom_rendering/react-js/src/');
const SCSS_DIR = path.resolve(__dirname, 'styles/src/scss');

const BUILD_DIR = path.resolve(__dirname, '../resources/static/js/');
const STATIC_DIR = path.resolve(__dirname, '../resources/static/');

const extractSass = new MiniCssExtractPlugin({
    filename: "../css/[name].css",
    disable: process.env.NODE_ENV === "development"
});

module.exports = {
    entry: {
        group: path.join(APP_DIR, 'group.jsx'),
        groupBrowser: path.join(APP_DIR, 'groupBrowser.jsx'),
        admin: path.join(APP_DIR, 'admin.jsx'),
        index: path.join(SCSS_DIR, 'index.scss'),
        global: path.join(SCSS_DIR, 'global.scss'),
        groupBrowserStylesheet: path.join(SCSS_DIR, 'groupBrowser.scss'),
        frontPage: path.join(SCSS_DIR, 'frontPage.scss'),
    },
    mode: 'development',
    resolve: {
        alias: {
            util: path.join(APP_DIR, 'utility'),
            rest_api: path.join(APP_DIR, 'api'),
            components: path.join(APP_DIR, 'components'),
            layout: path.join(APP_DIR, 'components/layout'),
            config: path.join(APP_DIR, 'config'),
            root: APP_DIR,
            groupController: path.join(APP_DIR, 'group/articles/ArticleContainer.jsx'),
            adminView: path.join(APP_DIR, 'admin/AdminView.jsx')
        },
        extensions: ['*', '.js', '.jsx'],
    },
    devtool: 'source-map',
    cache: true,
    node: {
        net: 'empty',
    },
    output: {
        path: BUILD_DIR,
        filename: '[name].js'
    },
    module: {
        rules: [
            {
                test: /(\.js|\.jsx)$/,
                exclude: /(node_modules)/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        cacheDirectory: true,
                        presets: ['@babel/preset-react']
                    }
                }
            },
            {
                test: /\.css?$/,
                exclude: /(node_modules)/,
                use: [{loader: 'style-loader'}, {loader: 'css-loader'}]
            },
            {
                test: /\.scss?$/,
                exclude: /(node_modules)/,
                use: [
                    {
                        loader: MiniCssExtractPlugin.loader,
                        options: {
                            hmr: process.env.NODE_ENV === 'development',
                        },
                    },
                    'css-loader',
                    'sass-loader'
                ],
            }
        ]
    },

    plugins: [
        new CopyWebpackPlugin([
            {from: './styles/maturita-bootstrap/dist/css/bootstrap.min.css', to: path.join(STATIC_DIR, 'css/')},
            {from: './styles/maturita-bootstrap/dist/js/bootstrap.min.js', to: path.join(STATIC_DIR, 'js/')},
            {
                from: './styles/font-awesome/css/font-awesome.min.css',
                to: path.join(STATIC_DIR, 'css/font-awesome/css/')
            },
            {from: './styles/font-awesome/fonts', to: path.join(STATIC_DIR, 'css/font-awesome/fonts/')},
            {from: './styles/src/assets', to: path.join(STATIC_DIR, 'images/')}
        ]),
        extractSass
    ]
};