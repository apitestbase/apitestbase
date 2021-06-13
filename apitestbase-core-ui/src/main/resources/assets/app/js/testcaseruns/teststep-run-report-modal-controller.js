'use strict';

angular.module('apitestbase').controller('TeststepRunReportModalController', ['$scope', 'stepRunReport',
  function($scope, stepRunReport) {
    $scope.stepRunReport = stepRunReport;
  }
]);
