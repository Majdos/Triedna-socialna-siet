import React from 'react';
import ReactDOM from 'react-dom';
import GroupBrowser from './group/browser/GroupBrowser';
import Modal from 'react-modal';

Modal.setAppElement('#main');
ReactDOM.render(
    <GroupBrowser />,
    document.getElementById("main")
);