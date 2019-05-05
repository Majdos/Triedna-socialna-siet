import React from 'react';
import ReactDOM from 'react-dom';
import AdminView from 'adminView';
import Modal from 'react-modal';

Modal.setAppElement('#main');

ReactDOM.render(
    <AdminView />,
    document.getElementById("main")
);