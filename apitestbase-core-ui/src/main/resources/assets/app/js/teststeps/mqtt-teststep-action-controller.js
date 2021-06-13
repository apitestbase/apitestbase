'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsActionController.
//    ng-include also creates a scope.
angular.module('apitestbase').controller('MQTTTeststepActionController', ['$scope', 'GeneralUtils', 'Teststeps',
    '$timeout',
  function($scope, GeneralUtils, Teststeps, $timeout) {
    var timer;
    $scope.steprun = {};

    var clearPreviousRunStatus = function() {
      if (timer) $timeout.cancel(timer);
      $scope.steprun = {};
    };

    $scope.endpointInfoIncomplete = function() {
      return !$scope.teststep.endpoint.url;
    };

    $scope.actionInfoIncomplete = function() {
      var teststep = $scope.teststep;
      return !teststep.otherProperties.topicString || !teststep.apiRequest.payload;
    };

    $scope.doAction = function() {
      clearPreviousRunStatus();

      var teststep = new Teststeps($scope.teststep);
      $scope.steprun.status = 'ongoing';
      teststep.$run(function(basicTeststepRun) {
        $scope.steprun.status = 'finished';
        timer = $timeout(function() {
          $scope.steprun.status = null;
        }, 15000);
      }, function(error) {
        $scope.steprun.status = 'failed';
        GeneralUtils.openErrorHTTPResponseModal(error);
      });
    };
  }
]);
