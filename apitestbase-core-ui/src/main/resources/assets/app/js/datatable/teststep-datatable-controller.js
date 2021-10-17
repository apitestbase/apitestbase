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
      TeststepDataTable.get({ teststepId: $stateParams.teststepId }, function(dataTable) {
        DataTableUtils.updateDataTableGridOptions($scope.dataTableGridOptions, dataTable);

        //  show the grid
        $scope.dataTable = dataTable;
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.addRow = function() {
      TeststepDataTable.addRow({ teststepId: $stateParams.teststepId }, {}, function(dataTable) {
        $scope.$emit('successfullySaved');
        DataTableUtils.updateDataTableGridOptions($scope.dataTableGridOptions, dataTable);
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.deleteRow = function(rowEntity) {
      TeststepDataTable.deleteRow({ teststepId: $stateParams.teststepId, rowSequence: rowEntity.Caption.rowSequence }, {
      }, function(dataTable) {
        $scope.$emit('successfullySaved');
        DataTableUtils.updateDataTableGridOptions($scope.dataTableGridOptions, dataTable);
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.addStringColumn = function() {
      TeststepDataTable.addColumn({ teststepId: $stateParams.teststepId }, {}, function(dataTable) {
        DataTableUtils.updateDataTableGridOptions($scope.dataTableGridOptions, dataTable, true);
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.afterColumnNameEdit = function(col, event) {
      if (event) {
        if (event.keyCode === 13 || event.keyCode === 27) {
          event.preventDefault();
        } else {                     // keys typed other than Enter and ESC do not trigger anything
          return;
        }
      }

      var colDef = col.colDef;
      delete colDef.headerCellTemplate;
      var oldName = colDef.name;
      var newName = col.name;

      if (newName !== oldName) {
        TeststepDataTable.renameColumn({
          teststepId: $stateParams.teststepId, columnId: colDef.dataTableColumnId, newName: newName
        }, {
        }, function(dataTable) {
          $scope.$emit('successfullySaved');
          DataTableUtils.updateDataTableGridOptions($scope.dataTableGridOptions, dataTable);
          DataTableUtils.refreshDataTableGrid($scope);
        }, function(response) {
          GeneralUtils.openErrorHTTPResponseModal(response);
        });
      } else {
        DataTableUtils.refreshDataTableGrid($scope);
      }
    };
  }
]);