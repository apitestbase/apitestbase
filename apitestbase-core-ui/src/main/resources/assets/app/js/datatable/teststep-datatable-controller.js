'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsController.
angular.module('apitestbase').controller('TeststepDataTableController', ['$scope', 'GeneralUtils', '$stateParams',
    'TeststepDataTable', 'DataTableUtils',
  function($scope, GeneralUtils, $stateParams, TeststepDataTable, DataTableUtils) {
    $scope.dataTableGridOptions = {
      enableSorting: false
    }

    $scope.findByTeststepId = function() {
      DataTableUtils.findByContainerId($scope, TeststepDataTable, { teststepId: $stateParams.teststepId });
    };

    $scope.addRow = function() {
      DataTableUtils.addRow($scope, TeststepDataTable, { teststepId: $stateParams.teststepId });
    };

    $scope.deleteRow = function(rowEntity) {
      DataTableUtils.deleteRow($scope, TeststepDataTable,
        { teststepId: $stateParams.teststepId, rowSequence: rowEntity.Caption.rowSequence });
    };

    $scope.addStringColumn = function() {
      DataTableUtils.addColumn($scope, TeststepDataTable, { teststepId: $stateParams.teststepId });
    };

    $scope.afterColumnNameEdit = function(col, event) {
      DataTableUtils.afterColumnNameEdit($scope, TeststepDataTable, { teststepId: $stateParams.teststepId }, col, event);
    };
  }
]);