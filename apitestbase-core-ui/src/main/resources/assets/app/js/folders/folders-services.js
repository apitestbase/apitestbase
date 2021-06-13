'use strict';

angular.module('apitestbase').factory('Folders', ['$resource',
  function($resource) {
    return $resource('api/folders/:folderId', {
      folderId: '@id'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
