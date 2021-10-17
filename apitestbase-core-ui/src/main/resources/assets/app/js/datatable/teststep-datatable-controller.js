'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsController.
angular.module('apitestbase').controller('TeststepDataTableController', ['$scope', 'GeneralUtils', '$stateParams',
    'TeststepDataTable', 'DataTableUtils',
  function($scope, GeneralUtils, $stateParams, TeststepDataTable, DataTableUtils) {
    $scope.dataTableGridOptions = {
      enableSorting: false,
      onRegisterApi: function(gridApi) {
        gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
          DataTableUtils.updateStringCell($scope, TeststepDataTable, rowEntity[colDef.name].id, oldValue, newValue);
        });

        gridApi.colMovable.on.columnPositionChanged($scope, function(colDef, originalPosition, newPosition) {
          DataTableUtils.moveColumn($scope, TeststepDataTable, { teststepId: $stateParams.teststepId,
            fromSequence: colDef.dataTableColumnSequence }, newPosition);
        });
      }
    };

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

    $scope.deleteColumn = function(columnId) {
      DataTableUtils.deleteColumn($scope, TeststepDataTable,
        { teststepId: $stateParams.teststepId, columnId: columnId });
    };
  }
]);