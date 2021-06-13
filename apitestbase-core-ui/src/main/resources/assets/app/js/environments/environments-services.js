'use strict';

angular.module('apitestbase').factory('Environments', ['$resource',
  function($resource) {
    return $resource('api/environments/:environmentId', {
      environmentId: '@id'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
