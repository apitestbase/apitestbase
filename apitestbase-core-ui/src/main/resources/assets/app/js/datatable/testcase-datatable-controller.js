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
          var toSequence = $scope.dataTableGridOptions.columnDefs[newPosition].dataTableColumnSequence;

          TestcaseDataTable.moveColumn({
            testcaseId: $scope.testcase.id,
            fromSequence: colDef.dataTableColumnSequence,
            toSequence: toSequence
          }, {}, function(dataTable) {
            $scope.$emit('successfullySaved');
              DataTableUtils.updateDataTableGridOptions($scope.dataTableGridOptions, dataTable);
              $scope.dataTable = dataTable;    // this is necessary as server side will change sequence values of data table columns (including the dragged column and some not-dragged columns).
          }, function(response) {
            GeneralUtils.openErrorHTTPResponseModal(response);
          });
        });

        $scope.$parent.handleTestcaseRunResultOutlineAreaDisplay();
      }
    };

    $scope.$on('testcaseRunResultOutlineAreaShown', function() {
      if ($scope.gridApi) {
        $scope.gridApi.core.handleWindowResize();
      }
    });

    var deleteColumn = function(columnId) {
      TestcaseDataTable.deleteColumn({ testcaseId: $stateParams.testcaseId, columnId: columnId }, {}, function(dataTable) {
        $scope.$emit('successfullySaved');
        DataTableUtils.updateDataTableGridOptions($scope.dataTableGridOptions, dataTable);
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.findByTestcaseId = function() {
      TestcaseDataTable.get({ testcaseId: $stateParams.testcaseId }, function(dataTable) {
        DataTableUtils.updateDataTableGridOptions($scope.dataTableGridOptions, dataTable);

        //  show the grid
        $scope.dataTable = dataTable;
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.addColumn = function(columnType) {
      TestcaseDataTable.addColumn({ testcaseId: $stateParams.testcaseId, columnType: columnType }, {}, function(dataTable) {
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
        TestcaseDataTable.renameColumn({
          testcaseId: $stateParams.testcaseId, columnId: colDef.dataTableColumnId, newName: newName
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

    $scope.addRow = function() {
      TestcaseDataTable.addRow({ testcaseId: $stateParams.testcaseId }, {}, function(dataTable) {
        $scope.$emit('successfullySaved');
        DataTableUtils.updateDataTableGridOptions($scope.dataTableGridOptions, dataTable);
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.deleteRow = function(rowEntity) {
      TestcaseDataTable.deleteRow({ testcaseId: $stateParams.testcaseId, rowSequence: rowEntity.Caption.rowSequence }, {
      }, function(dataTable) {
        $scope.$emit('successfullySaved');
        DataTableUtils.updateDataTableGridOptions($scope.dataTableGridOptions, dataTable);
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
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