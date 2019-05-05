let config = require('./webpack.config');
const webpack = require('webpack');
const MinifyPlugin = require("babel-minify-webpack-plugin");

config.plugins.push(new MinifyPlugin());
config.devtool = undefined;
config.mode = 'production';

module.exports = config;