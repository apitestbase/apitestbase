'use strict';

angular.module('apitestbase').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
      .state('folder', {
          url: '/folders/:folderId',
          templateUrl: '/ui/views/folders/edit.html'
      });
}]);