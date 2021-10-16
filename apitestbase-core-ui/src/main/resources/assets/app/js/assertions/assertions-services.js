'use strict';

angular.module('apitestbase').factory('Assertions', ['$resource',
  function($resource) {
    return $resource('api/teststeps/:teststepId/assertions', {
      assertionId: '@id'
    }, {
      update: {
        method: 'PUT',
        url: 'api/assertions/:assertionId'
      },
      remove: {
        method: 'DELETE',
        url: 'api/assertions/:assertionId'
      }
    });
  }
]);
