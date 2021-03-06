'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsActionController.
//    ng-include also creates a scope.
angular.module('apitestbase').controller('FTPTeststepActionController', ['$scope', 'GeneralUtils', '$timeout',
    'Teststeps', 'Upload', '$window',
  function($scope, GeneralUtils, $timeout, Teststeps, Upload, $window) {
    var timer;
    $scope.steprun = {};

    var clearPreviousRunStatus = function() {
      if (timer) $timeout.cancel(timer);
      $scope.steprun = {};
    };

    $scope.fileFromChanged = function(isValid) {
      clearPreviousRunStatus();

      var teststep = $scope.teststep;
      teststep.apiRequest = { minClassName: teststep.apiRequest.minClassName };

      //  update test step immediately (no timeout)
      $scope.update(isValid);
    };

    $scope.endpointInfoIncomplete = function() {
      var endpoint = $scope.teststep.endpoint;
      return !endpoint.host || !endpoint.port;
    };

    $scope.actionInfoIncomplete = function() {
      var apiRequest = $scope.teststep.apiRequest;
      var otherProperties = $scope.teststep.otherProperties;
      return !otherProperties.remoteFilePath ||
        (apiRequest.minClassName === '.FtpPutRequestFileFromText' && !apiRequest.fileContent) ||
        (apiRequest.minClassName === '.FtpPutRequestFileFromFile' && !apiRequest.fileName);
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

    $scope.uploadAPIRequestFile = function(file) {
      if (file) {
        var url = 'api/testcases/' + $scope.teststep.testcaseId + '/teststeps/' + $scope.teststep.id + '/apiRequestFile';
        Upload.upload({
          url: url,
          data: {file: file}
        }).then(function successCallback(response) {
          $scope.$emit('successfullySaved');
          $scope.setTeststep(new Teststeps(response.data));
        }, function errorCallback(response) {
          GeneralUtils.openErrorHTTPResponseModal(response);
        });
      }
    };

    $scope.downloadAPIRequestFile = function() {
      var url = 'api/testcases/' + $scope.teststep.testcaseId + '/teststeps/' + $scope.teststep.id + '/apiRequestFile';
      $window.open(url, '_blank', '');
    };
  }
]);
