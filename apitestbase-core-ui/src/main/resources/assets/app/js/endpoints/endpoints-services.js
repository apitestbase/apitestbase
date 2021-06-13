'use strict';

angular.module('apitestbase').factory('ManagedEndpoints', ['$resource',
  function($resource) {
    return $resource('api/endpoints/:endpointId', {
      endpointId: '@id'
    }, {
      save: {
        method: 'POST',
        url: 'api/environments/:environmentId/endpoints'
      },
      update: {
        method: 'PUT'
      }
    });
  }
]);
