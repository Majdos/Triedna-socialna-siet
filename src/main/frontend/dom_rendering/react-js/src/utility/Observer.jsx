import {Map, List} from 'immutable';

export default class Observer {
    constructor() {
        this.subscribers = Map();
    }

    subscribe(destination, callback, caller) {
        if (!this.subscribers.has(destination)) {
            this.subscribers= this.subscribers.set(destination, Map());
        }
        if (!this.subscribers.hasIn([destination, caller])) {
            this.subscribers = this.subscribers.setIn([destination, caller], List())
        }
        this.subscribers = this.subscribers.updateIn([destination, caller], list => list.push(callback))
    }

    unsubscribe(destination, caller) {
        this.subscribers = this.subscribers.removeIn([destination, caller]);
    }

    publish(destination, data) {
        this.subscribers.get(destination).forEach(caller => caller.forEach(callback => callback(data)));
    }
}