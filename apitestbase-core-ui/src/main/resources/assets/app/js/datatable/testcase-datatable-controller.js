'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TestcasesController.
angular.module('apitestbase').controller('TestcaseDataTableController', ['$scope', '$stateParams', 'TestcaseDataTable',
    'DataTableUtils', '$uibModal', 'GeneralUtils',
  function($scope, $stateParams, TestcaseDataTable, DataTableUtils, $uibModal, GeneralUtils) {
    $scope.dataTableGridOptions = {
      enableSorting: false,
      onRegisterApi: function(gridApi) {
        $scope.gridApi = gridApi;

        gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
          DataTableUtils.updateStringCell($scope, TestcaseDataTable, rowEntity[colDef.name].id, oldValue, newValue);
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
      DataTableUtils.stringCellDblClicked($scope, TestcaseDataTable, rowEntity, col);
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
        TestcaseDataTable.updateCell({ cellId: rowEntity[columnName].id }, { endpoint: { id: selectedEndpoint.id }
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