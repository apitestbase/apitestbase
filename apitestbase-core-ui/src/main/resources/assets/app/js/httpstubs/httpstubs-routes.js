'use strict';

angular.module('apitestbase').config(['$stateProvider', function ($stateProvider) {
  $stateProvider
    .state('httpstub_edit', {
        url: '/testcases/:testcaseId/httpstubs/:httpStubId/edit',
        templateUrl: '/ui/views/httpstubs/edit.html'
    });
}]);