'use strict';

angular.module('mockserver').controller('StubRequestController', ['$scope', 'MockServer', 'GeneralUtils',
    '$stateParams',
  function($scope, MockServer, GeneralUtils, $stateParams) {
    $scope.find = function() {
      MockServer.findStubRequestById({ stubRequestId: $stateParams.stubRequestId }, function(stubRequest) {
        $scope.stubRequest = stubRequest;

        $scope.stubRequestHeadersStr = GeneralUtils.formatHTTPHeadersObj(stubRequest.request.headers);
        $scope.stubResponseHeadersStr = GeneralUtils.formatHTTPHeadersObj(stubRequest.response.headers);
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);