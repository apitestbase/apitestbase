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

  }
]);