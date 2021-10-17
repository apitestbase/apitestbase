'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TestcasesController.
angular.module('apitestbase').controller('TestcaseDataTableController', ['$scope', 'GeneralUtils', '$stateParams',
    'TestcaseDataTable', 'DataTableUtils', '$uibModal', '$rootScope',
  function($scope, GeneralUtils, $stateParams, TestcaseDataTable, DataTableUtils, $uibModal, $rootScope) {
    var stringCellUpdate = function(dataTableCellId, newValue) {
      TestcaseDataTable.updateCell({
        testcaseId: $stateParams.testcaseId
      }, {
        id: dataTableCellId,
        value: newValue
      }, function() {
        $scope.$emit('successfullySaved');
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.dataTableGridOptions = {
      enableSorting: false,
      onRegisterApi: function(gridApi) {
        $scope.gridApi = gridApi;

        gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
          if (newValue !== oldValue) {
            stringCellUpdate(rowEntity[colDef.name].id, newValue);
          }
        });

        gridApi.colMovable.on.columnPositionChanged($scope, function(colDef, originalPosition, newPosition) {
          DataTableUtils.moveColumn($scope, TestcaseDataTable, { testcaseId: $stateParams.testcaseId,
            fromSequence: colDef.dataTableColumnSequence }, newPosition);
        });

        $scope.$parent.handleTestcaseRunResultOutlineAreaDisplay();
      }
    };

    $scope.$on('testcaseRunResultOutlineAreaShown', function() {
      if ($scope.gridApi) {
        $scope.gridApi.core.handleWindowResize();
      }
    });

    $scope.deleteColumn = function(columnId) {
      DataTableUtils.deleteColumn($scope, TestcaseDataTable,
        { testcaseId: $stateParams.testcaseId, columnId: columnId });
    };

    $scope.findByTestcaseId = function() {
      DataTableUtils.findByContainerId($scope, TestcaseDataTable, { testcaseId: $stateParams.testcaseId });
    };

    $scope.addColumn = function(columnType) {
      DataTableUtils.addColumn($scope, TestcaseDataTable,
        { testcaseId: $stateParams.testcaseId, columnType: columnType });
    };

    $scope.afterColumnNameEdit = function(col, event) {
      DataTableUtils.afterColumnNameEdit($scope, TestcaseDataTable, { testcaseId: $stateParams.testcaseId }, col, event);
    };

    $scope.addRow = function() {
      DataTableUtils.addRow($scope, TestcaseDataTable, { testcaseId: $stateParams.testcaseId });
    };

    $scope.deleteRow = function(rowEntity) {
      DataTableUtils.deleteRow($scope, TestcaseDataTable,
        { testcaseId: $stateParams.testcaseId, rowSequence: rowEntity.Caption.rowSequence });
    };

    $scope.stringCellDblClicked = function(rowEntity, col) {
      var columnName = col.name;
      var oldValue = rowEntity[columnName].value;

      //  open modal dialog
      var modalInstance = $uibModal.open({
        templateUrl: '/ui/views/testcases/datatable-string-cell-textarea-editor-modal.html',
        controller: 'DataTableStringCellTextareaEditorModalController',
        size: 'lg',
        windowClass: 'datatable-string-cell-textarea-editor-modal',
        resolve: {
          rowEntity: function() {
            return rowEntity;
          },
          columnName: function() {
            return columnName;
          }
        }
      });

      //  handle result from modal dialog
      modalInstance.result.then(function closed() {}, function dismissed() {
        var newValue = rowEntity[columnName].value;
        if (newValue !== oldValue) {
          stringCellUpdate(rowEntity[columnName].id, newValue); //  save immediately (no timeout)
        }
      });
    };

    $scope.selectManagedEndpoint = function(rowEntity, col) {
      var columnName = col.name;
      var endpointType = col.colDef.dataTableColumnType.replace('Endpoint', '');

      //  open modal dialog
      var modalInstance = $uibModal.open({
        templateUrl: '/ui/views/endpoints/list-modal.html',
        controller: 'SelectManagedEndpointModalController',
        size: 'lg',
        windowClass: 'select-managed-endpoint-modal',
        resolve: {
          endpointType: function() {
            return endpointType;
          },
          titleSuffix: function() {
            return 'for [' + rowEntity.Caption.value + '] > ' + columnName;
          }
        }
      });

      //  handle result from modal dialog
      modalInstance.result.then(function closed(selectedEndpoint) {
        TestcaseDataTable.updateCell({
          testcaseId: $stateParams.testcaseId
        }, {
          id: rowEntity[columnName].id,
          endpoint: { id: selectedEndpoint.id }
        }, function() {
          rowEntity[columnName].endpoint = selectedEndpoint;
          $scope.$emit('successfullySaved');
        }, function(response) {
          GeneralUtils.openErrorHTTPResponseModal(response);
        });
      }, function dismissed() {
        //  Modal dismissed. Do nothing.
      });
    };
  }
]);