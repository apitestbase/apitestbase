'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of the specific test step controller,
//    ng-include also creates a scope.
angular.module('apitestbase').controller('AssertionsController', ['$scope', '$rootScope', 'Assertions', '$stateParams',
    'uiGridConstants', 'GeneralUtils', '$timeout', '$http',
  function($scope, $rootScope, Assertions, $stateParams, uiGridConstants, GeneralUtils, $timeout, $http) {
    $scope.findAssertionsByTeststepId = function() {
      Assertions.query({ teststepId: $stateParams.teststepId }, function(returnAssertions) {
        $scope.assertions = returnAssertions;
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.findSingleByTeststepId = function() {
      Assertions.query({ teststepId: $stateParams.teststepId }, function(returnAssertions) {
        $scope.assertion = returnAssertions[0];
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.$watch('$parent.steprun.response', function() {
      $scope.assertionVerificationResults = {};
    });

    var removeSelectedAssertion = function() {
      var assertion = $scope.assertion;
      assertion.$remove(function(response) {
        delete $scope.assertion;
        GeneralUtils.deleteArrayElementByProperty($scope.assertions, 'id', assertion.id);
        $scope.$emit('successfullySaved');
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.assertionsGridOptions = {
      data: 'assertions',
      enableRowHeaderSelection: false, multiSelect: false, noUnselect: true,
      enableGridMenu: true, gridMenuShowHideColumns: false, enableColumnMenus: false,
      columnDefs: [
        {
          name: 'name', headerTooltip: 'Double click to edit',
          sort: { direction: uiGridConstants.ASC, priority: 1 }, cellTemplate: 'assertionGridNameCellTemplate.html',
          enableCellEdit: true, editableCellTemplate: 'assertionGridNameEditableCellTemplate.html'
        },
        { name: 'type', width: 80, minWidth: 80, enableCellEdit: false }
      ],
      gridMenuCustomItems: [
        { title: 'Delete', order: 210, action: removeSelectedAssertion,
          shown: function() {
            return !$rootScope.appStatus.isForbidden() &&
              $scope.assertionsGridApi.selection.getSelectedRows().length === 1;
          }
        }
      ],
      onRegisterApi: function (gridApi) {
        $scope.bottomPaneLoadedCallback();
        $scope.assertionsGridApi = gridApi;
        gridApi.selection.on.rowSelectionChanged($scope, function(row) {
          $scope.assertion = row.entity;
        });
        gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
          if (newValue !== oldValue) {
            $scope.assertionUpdate();
          }
        });
      }
    };

    var selectAssertionInGridByProperty = function(propertyName, propertyValue) {
      var assertions = $scope.assertions;
      var assertion = assertions.find(
        function(asrt) {
          return asrt[propertyName] === propertyValue;
        }
      );
      var gridApi = $scope.assertionsGridApi;
      gridApi.grid.modifyRows(assertions);
      gridApi.selection.selectRow(assertion);
    };

    $scope.clearCurrentAssertionVerificationResult = function() {
      delete $scope.assertionVerificationResults[$scope.assertion.id];
    };

    var timer;
    $scope.assertionAutoSave = function() {
      $scope.clearCurrentAssertionVerificationResult();
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.assertionUpdate();
      }, 2000);
    };

    $scope.assertionUpdate = function() {
      if (timer) $timeout.cancel(timer);  //  cancel existing timer if the update function is called directly (to avoid duplicate save)
      $scope.assertion.$update(function(response) {
        $scope.$emit('successfullySaved');
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.createAssertion = function(type) {
      var assertions = $scope.assertions;
      var name = GeneralUtils.getNextNameInSequence(assertions, type + ' ');
      var assertion = new Assertions({
        name: name,
        type: type,
        otherProperties: {}  //  adding this property here to avoid Jackson 'Missing property' error (http://stackoverflow.com/questions/28089484/deserialization-with-jsonsubtypes-for-no-value-missing-property-error)
      });

      assertion.$save({ teststepId: $stateParams.teststepId }, function(returnAssertion) {
        assertions.push(returnAssertion);
        $scope.$emit('successfullySaved');

        //  select newly created assertion in grid
        selectAssertionInGridByProperty('name', name);
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.verifyCurrentAssertion = function() {
      var assertion = $scope.assertion;

      //  resolve assertion input
      var input;
      var apiResponse = $scope.$parent.steprun.response;
      if ($scope.teststep.type === 'SOAP' || $scope.teststep.type === 'HTTP') {
        if (assertion.type === 'StatusCodeEqual') {
          input = apiResponse.statusCode;
        } else {
          input = apiResponse.httpBody;
        }
      } else if ($scope.teststep.type === 'JMS') {
        input = apiResponse.body;
      } else if ($scope.teststep.type === 'MQ') {
        if (assertion.type === 'HasAnMQRFH2FolderEqualToXml') {
          input = apiResponse.mqrfh2Header;
        } else {
          input = apiResponse.bodyAsText;
        }
      } else {
        input = apiResponse;
      }

      var url = 'api/assertions/' + assertion.id + '/verify';
      var assertionVerificationRequest = { input: input, assertion: assertion };
      $http
        .post(url, assertionVerificationRequest)
        .then(function successCallback(response) {
          var data = response.data;
          $scope.assertionVerificationResults[assertion.id] = data;
        }, function errorCallback(response) {
          GeneralUtils.openErrorHTTPResponseModal(response);
        });
    };
  }
]);
