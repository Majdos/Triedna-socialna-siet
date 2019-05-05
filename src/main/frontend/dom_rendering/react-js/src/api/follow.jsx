exports.follow = function follow(api, rootPath, relArray) {
    
	var root = api({
		method: 'GET',
		path: rootPath
	});

	return relArray.reduce((root, arrayItem) => {
		return traverseNext(root, arrayItem.rel, arrayItem);
	}, root);

	function traverseNext (root, rel, arrayItem) {
		return root.then( response => {
			if (hasEmbeddedRel(response.entity, rel)) {
				return response;
			}

			if(!response.entity._links) {
				return [];
			}

            return api({
                method: 'GET',
                path: response.entity._links[rel].href,
                params: arrayItem.params
            });
		});
	}

	function hasEmbeddedRel (entity, rel) {
		return entity._embedded && entity._embedded.hasOwnProperty(rel);
	}
};