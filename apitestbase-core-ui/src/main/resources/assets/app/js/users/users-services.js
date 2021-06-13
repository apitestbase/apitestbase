'use strict';

angular.module('apitestbase').factory('Users', ['$resource',
  function($resource) {
    return $resource('api/users/:userId', {
      userId: '@id'
    }, {
      updatePassword: {
        method: 'PUT',
        url: 'api/users/:userId/password'
      }
    });
  }
]);
