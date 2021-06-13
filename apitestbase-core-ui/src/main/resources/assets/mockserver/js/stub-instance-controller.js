'use strict';

angular.module('mockserver').controller('StubInstanceController', ['$scope', 'MockServer', 'GeneralUtils',
    '$stateParams',
  function($scope, MockServer, GeneralUtils, $stateParams) {
    $scope.find = function() {
      MockServer.findStubInstanceById({ stubInstanceId: $stateParams.stubInstanceId }, function(stubInstance, responseHeadersFn, statusCode, statusText) {
        if (statusCode === 204) {    //  on 204 returned, stubInstance is a promise instead of null
          $scope.stubInstance = null;
        } else {
          $scope.stubInstance = stubInstance;
          $scope.requestBodyMainPattern = GeneralUtils.getRequestBodyMainPattern(
            stubInstance.request.method, stubInstance.request.bodyPatterns);

          //  construct stubRequestHeadersStr
          var stubRequestHeadersStr = '';
          var requestHeaders = stubInstance.request.headers;
          if (requestHeaders) {
            Object.keys(requestHeaders).forEach(function(key, index) {
              if (index > 0) {
                stubRequestHeadersStr += '\n';
              }
              var operator = Object.keys(requestHeaders[key])[0];
              if (operator === 'anything') {
                stubRequestHeadersStr += key + ' is anything';
              } else {
                stubRequestHeadersStr += key + ' ' + operator + ' ' + requestHeaders[key][operator];
              }
            });
          }
          $scope.stubRequestHeadersStr = stubRequestHeadersStr;

          $scope.stubResponseHeadersStr = GeneralUtils.formatHTTPHeadersObj(stubInstance.response.headers);
        }
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);