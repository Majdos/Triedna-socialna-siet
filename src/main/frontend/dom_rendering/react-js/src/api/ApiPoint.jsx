import { client } from './client';
import Paging from 'util/Paging';

export default class ApiPoint {
    constructor(endpoint, resourceName, stateMutator) {
        this.endpoint = endpoint;
        this.resourceName = resourceName;
    }

    get(config, ignoreCount = 0) {
        return client({
            method: 'get',
            path: this.endpoint,
            ...config
        }).then(response => {
            const entity = response.entity;
            let result = {};
            if (entity._embedded) {

                result.resources = entity._embedded[this.resourceName];
                result.page = entity.page;
                result.links = entity._links;

            }
            else if (typeof entity === 'array' || typeof entity === 'object' || this.resourceName === undefined) {
                result.resources = entity;
            }
            else {
                throw new Error('Wrong entity type');
            }
            return result;
        });
    }

    post(config) {
        return client({
            method: 'post',
            path: this.endpoint,
            headers: {
                'Content-Type': 'application/json'
            },
            ...config
        }).then(response => {
            return response;
        });
    }

    delete({ entity, ...config }) {
        return client({
            method: 'delete',
            path: entity._links.self.href,
            ...config
        }).then(response => {
            return response;
        });
    }

    put(config) {
        return client({
            method: 'put',
            path: config.entity._links.self.href,
            headers: {
                'Content-Type': 'application/json'
            },
            ...config
        }).then(response => {
            return response;
        });
    }


    patch(config) {
        const { entity, updatedData, ...other } = config;
        let resultEntity = {};

        for (const data of Object.keys(updatedData)) {
            resultEntity[data] = updatedData[data];
        }

        return client({
            path: entity._links.self.href,
            method: 'patch',
            entity: resultEntity,
            headers: {
                'Content-Type': 'application/json'
            },
            ...other
        }).then(response => {
            return response;
        });
    }

    findAndDeleteResource(list, item) {
        const index = list.findIndex(value => value._links.self.href === item._links.self.href);
        if (index >= 0) {
            return list.remove(index);
        }
        return list;
    }
}

export function self(entity) {
    return entity._links.self.href;
}

export function equalsEntity(a, b) {
    return self(a) === self(b);
}