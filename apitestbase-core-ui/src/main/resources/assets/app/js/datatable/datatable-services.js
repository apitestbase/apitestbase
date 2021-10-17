'use strict';

angular.module('apitestbase').factory('TestcaseDataTable', ['$resource', 'DataTableUtils',
  function($resource, DataTableUtils) {
    return $resource('api/testcases/:testcaseId/datatable/:verb', {}, DataTableUtils.resourceOperations);
  }
]).factory('TeststepDataTable', ['$resource', 'DataTableUtils',
  function($resource, DataTableUtils) {
    return $resource('api/teststeps/:teststepId/datatable/:verb', {}, DataTableUtils.resourceOperations);
  }
]).factory('DataTableUtils', ['$timeout', '$rootScope', 'GeneralUtils', function ($timeout, $rootScope, GeneralUtils) {
  var DATA_TABLE_GRID_EDITABLE_HEADER_CELL_TEMPLATE = 'dataTableGridEditableHeaderCellTemplate.html';

  var refreshDataTableGrid = function(scope) {
    var dataTable = scope.dataTable;
    delete scope.dataTable;
    $timeout(function() {
      scope.dataTable = dataTable;
    }, 0);
  };

  var getDefaultColumnDef = function(dataTableColumnId, columnName, dataTableColumnType, dataTableColumnSequence) {
    return {
      dataTableColumnId: dataTableColumnId,    //  not standard ui grid property for column def
      dataTableColumnType: dataTableColumnType,    //  not standard ui grid property for column def
      dataTableColumnSequence: dataTableColumnSequence,    //  not standard ui grid property for column def
      name: columnName,
      displayName: columnName,  //  need this line to avoid underscore in column name is not displayed in column header
      // determine column width according to the length of column name
      // assuming each character deserves 9 pixels (friendly to uppercase letters)
      // 30 pixels for displaying grid header menu arrow
      width: columnName.length * 9 + 30,
      enableColumnMenu: dataTableColumnSequence === 1 ? false : true,
      enableColumnMoving: dataTableColumnSequence === 1 ? false : true,
      enableHiding: false,
      menuItems: [
        {
          title: 'Rename Column',
          icon: 'glyphicon glyphicon-edit',
          action: function() {
            this.context.col.colDef.headerCellTemplate = DATA_TABLE_GRID_EDITABLE_HEADER_CELL_TEMPLATE;

            refreshDataTableGrid(this.grid.appScope.$parent);
          },
          shown: function() {
            return !$rootScope.appStatus.isForbidden();
          }
        },
        {
          title: 'Delete Column',
          icon: 'glyphicon glyphicon-trash',
          action: function() {
            this.grid.appScope.$parent.deleteColumn(this.context.col.colDef.dataTableColumnId);
          },
          shown: function() {
            return !$rootScope.appStatus.isForbidden();
          }
        }
      ]
    };
  };

  var updateDataTableGridOptions = function(dataTableGridOptions, dataTable, lastColumnHeaderInEditMode) {
    dataTableGridOptions.columnDefs = [];
    for (var i = 0; i < dataTable.columns.length; i++) {
      var dataTableColumn = dataTable.columns[i];
      var columnName = dataTableColumn.name;
      var uiGridColumn = getDefaultColumnDef(dataTableColumn.id, columnName, dataTableColumn.type, dataTableColumn.sequence);
      if (lastColumnHeaderInEditMode === true && i === dataTable.columns.length - 1) {
        uiGridColumn.headerCellTemplate = DATA_TABLE_GRID_EDITABLE_HEADER_CELL_TEMPLATE;
      }

      if (dataTableColumn.type === 'String') {    //  it is a string column
        uiGridColumn.enableCellEdit = true;
        uiGridColumn.enableCellEditOnFocus = true;
        uiGridColumn.editModelField = columnName + '.value';
        uiGridColumn.cellTemplate = 'dataTableGridStringCellTemplate.html';
        if (dataTableColumn.name === 'Caption') {    //  it is the caption column
          uiGridColumn.editableCellTemplate = 'dataTableGridCaptionEditableCellTemplate.html';
        } else {                                     //  it is a normal string column
          uiGridColumn.editableCellTemplate = 'dataTableGridStringEditableCellTemplate.html';
        }
      } else {                                    //  it is an endpoint column
        uiGridColumn.enableCellEdit = false;
        uiGridColumn.cellTemplate = 'dataTableGridEndpointCellTemplate.html';
      }
      dataTableGridOptions.columnDefs.push(uiGridColumn);
    }
    var deletionColumn = {
       name: 'delete.1',  //  give this column a name that is not able to be created by user
       displayName: 'Delete', width: 70, minWidth: 60, enableCellEdit: false, enableColumnMenu: false,
       enableColumnMoving: false, cellTemplate: 'dataTableGridDeleteCellTemplate.html'
    };
    dataTableGridOptions.columnDefs.push(deletionColumn);
    dataTableGridOptions.data = dataTable.rows;
  };

  return {
    resourceOperations: {
      addColumn: { method: 'POST', params: { verb: 'addColumn' } },
      deleteColumn: { method: 'POST', params: { verb: 'deleteColumn' } },
      renameColumn: { method: 'POST', params: { verb: 'renameColumn' } },
      moveColumn: { method: 'POST', params: { verb: 'moveColumn' } },
      addRow: { method: 'POST', params: { verb: 'addRow' } },
      deleteRow: { method: 'POST', params: { verb: 'deleteRow' } },
      updateCell: { method: 'PUT', url: 'api/dataTableCells/:cellId' }
    },

    findByContainerId: function(scope, restService, restRequestParams) {
      restService.get(restRequestParams, function(dataTable) {
        updateDataTableGridOptions(scope.dataTableGridOptions, dataTable);

        //  show the grid
        scope.dataTable = dataTable;
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    },

    addRow: function(scope, restService, restRequestParams) {
      restService.addRow(restRequestParams, {}, function(dataTable) {
        scope.$emit('successfullySaved');
        updateDataTableGridOptions(scope.dataTableGridOptions, dataTable);
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    },

    deleteRow: function(scope, restService, restRequestParams) {
      restService.deleteRow(restRequestParams, {
      }, function(dataTable) {
        scope.$emit('successfullySaved');
        updateDataTableGridOptions(scope.dataTableGridOptions, dataTable);
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    },

    addColumn: function(scope, restService, restRequestParams) {
      restService.addColumn(restRequestParams, {}, function(dataTable) {
        updateDataTableGridOptions(scope.dataTableGridOptions, dataTable, true);
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    },

    afterColumnNameEdit: function(scope, restService, restRequestParams, col, event) {
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
        restRequestParams.columnId = colDef.dataTableColumnId;
        restRequestParams.newName = newName;
        restService.renameColumn(restRequestParams, {
        }, function(dataTable) {
          scope.$emit('successfullySaved');
          updateDataTableGridOptions(scope.dataTableGridOptions, dataTable);
          refreshDataTableGrid(scope);
        }, function(response) {
          GeneralUtils.openErrorHTTPResponseModal(response);
        });
      } else {
        refreshDataTableGrid(scope);
      }
    },

    moveColumn: function(scope, restService, restRequestParams, newPosition) {
      var toSequence = scope.dataTableGridOptions.columnDefs[newPosition].dataTableColumnSequence;
      restRequestParams.toSequence = toSequence;
      restService.moveColumn(restRequestParams, {}, function(dataTable) {
        scope.$emit('successfullySaved');
        updateDataTableGridOptions(scope.dataTableGridOptions, dataTable);
        scope.dataTable = dataTable;    // this is necessary as server side will change sequence values of data table columns (including the dragged column and some not-dragged columns).
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    },

    deleteColumn: function(scope, restService, restRequestParams) {
      restService.deleteColumn(restRequestParams, {}, function(dataTable) {
        scope.$emit('successfullySaved');
        updateDataTableGridOptions(scope.dataTableGridOptions, dataTable);
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    },

    updateStringCell: function(scope, restService, cellId, oldValue, newValue) {
      if (newValue !== oldValue) {
        restService.updateCell({ cellId: cellId }, { value: newValue }, function() {
          scope.$emit('successfullySaved');
        }, function(response) {
          GeneralUtils.openErrorHTTPResponseModal(response);
        });
      }
    }
  };
}]);
