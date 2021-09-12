'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of AssertionsController.
//    ng-include also creates a scope.
angular.module('apitestbase').controller('XMLValidAgainstXSDController', ['$scope', 'GeneralUtils', 'Upload', '$window',
  function($scope, GeneralUtils, Upload, $window) {
    $scope.uploadXSDFile = function(file) {
      if (file) {
        var url = 'api/assertions/' + $scope.assertion.id + '/xsdFile';
        Upload.upload({
          url: url,
          data: {file: file}
        }).then(function successCallback(response) {
          $scope.$emit('successfullySaved');
          $scope.clearCurrentAssertionVerificationResult();
          $scope.assertion.otherProperties.fileName = file.name;
        }, function errorCallback(response) {
          GeneralUtils.openErrorHTTPResponseModal(response);
        });
      }
    };

    $scope.downloadXSDFile = function() {
      var url = 'api/assertions/' + $scope.assertion.id + '/xsdFile';
      $window.open(url, '_blank', '');
    };
  }
]);