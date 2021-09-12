'use strict';

angular.module('apitestbase').factory('Assertions', ['$resource',
  function($resource) {
    return $resource('api/teststeps/:teststepId/assertions', {
    }, {
      update: {
        method: 'PUT',
        url: 'api/assertions/:assertionId',
        params: { assertionId: '@id' }
      },
      remove: {
        method: 'DELETE',
        url: 'api/assertions/:assertionId',
        params: { assertionId: '@id' }
      }
    });
  }
]);
