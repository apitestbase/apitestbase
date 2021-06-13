'use strict';

angular.module('apitestbase').factory('Articles', ['$resource',
  function($resource) {
    return $resource('api/articles/:articleId', {
      articleId: '@id'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
