import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import {client} from './client';

const timeout = 5000;

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

export default class StompClient {
    
    constructor(endpoint) {
        this.socket = SockJS(endpoint);
        this.stompClient = Stomp.over(this.socket);
        this.stompClient.reconnect_delay = 250;
        this.connected = false;
        this.stompClient.connect({}, frame => {this.connected = true});
    }

    register(registrations) {
        return new Promise(async (resolve, reject) => {
            let end = Date.now() + timeout;
            
            while (!this.connected && end > Date.now()) {
                await sleep(150);
            }

            if (this.connected) {
                const subscriptions = registrations.map(registration => 
                   this.stompClient.subscribe(registration.route, registration.callback)             
                );
                resolve(subscriptions);
            }
            else {
                reject('Nepodarilo sa pripojit na server!');
            }
        });
    }

    fetchFromMessage(message, projection = 'preview') {
        return client({
            method: 'GET',
            path: message.body,
            params: {
                projection: projection
            }
        });
    }

}
