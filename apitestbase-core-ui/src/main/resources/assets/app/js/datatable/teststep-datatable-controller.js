'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsController.
angular.module('apitestbase').controller('TeststepDataTableController', ['$scope', 'GeneralUtils', '$stateParams',
    'TeststepDataTable',
  function($scope, GeneralUtils, $stateParams, TeststepDataTable) {
    $scope.findByTeststepId = function() {
      TeststepDataTable.get({ teststepId: $stateParams.teststepId }, function(dataTable) {
        //updateDataTableGrid(dataTable);
        console.log(dataTable);

        //  show the grid
        $scope.dataTable = dataTable;
      }, function(response) {
        GeneralUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.dataTableGridOptions = {
      enableSorting: false
    }
  }
]);