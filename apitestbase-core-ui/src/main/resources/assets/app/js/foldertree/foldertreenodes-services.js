'use strict';

angular.module('apitestbase').factory('FolderTreeNodes', ['$resource',
  function($resource) {
    return $resource('api/foldertreenodes', {}, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
