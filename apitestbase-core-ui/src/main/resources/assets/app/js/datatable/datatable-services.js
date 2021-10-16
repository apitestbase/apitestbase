'use strict';

angular.module('apitestbase').factory('TestcaseDataTable', ['$resource', 'DataTableUtils',
  function($resource, DataTableUtils) {
    return $resource('api/testcases/:testcaseId/datatable/:verb', {}, DataTableUtils.resourceOperations);
  }
]).factory('TeststepDataTable', ['$resource', 'DataTableUtils',
  function($resource, DataTableUtils) {
    return $resource('api/teststeps/:teststepId/datatable/:verb', {}, DataTableUtils.resourceOperations);
  }
]).factory('DataTableUtils', function () {
  return {
    resourceOperations: {
      addColumn: { method: 'POST', params: { verb: 'addColumn' } },
      deleteColumn: { method: 'POST', params: { verb: 'deleteColumn' } },
      renameColumn: { method: 'POST', params: { verb: 'renameColumn' } },
      moveColumn: { method: 'POST', params: { verb: 'moveColumn' } },
      addRow: { method: 'POST', params: { verb: 'addRow' } },
      deleteRow: { method: 'POST', params: { verb: 'deleteRow' } },
      updateCell: { method: 'POST', params: { verb: 'updateCell' } }
    },

    aaa: function(a, b, c) {
    }
  };
});
