define(function(require) {
	'use strict';

	var interceptor = require('rest/interceptor');

	return interceptor({
		request: function (request /*, config, meta */) {
            if (request.path.indexOf('{') !== -1) {
				request.path = request.path.split('{')[0];
            }

            if(request.params != null){
                request.path = request.path + (request.path.indexOf('?') === -1 ? '?' : '&');
                Object.keys(request.params).forEach(key => {
                    request.path = request.path + key + '=' + request.params[key] + '&'; 
                });
                request.path = request.path.slice(0, -1);
            }
            return request;
		}
	});

});