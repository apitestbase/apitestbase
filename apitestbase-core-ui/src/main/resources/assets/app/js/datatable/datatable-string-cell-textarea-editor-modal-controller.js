'use strict';

angular.module('apitestbase').controller('DataTableStringCellTextareaEditorModalController', ['$scope', 'rowEntity',
    'columnName',
  function($scope, rowEntity, columnName) {
    $scope.rowEntity = rowEntity;
    $scope.columnName = columnName;
  }
]);
