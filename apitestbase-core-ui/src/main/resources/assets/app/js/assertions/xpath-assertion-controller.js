'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of AssertionsController.
//    ng-include also creates a scope.
angular.module('apitestbase').controller('XPathAssertionController', ['$scope', '$rootScope', 'uiGridConstants',
    'GeneralUtils',
  function($scope, $rootScope, uiGridConstants, GeneralUtils) {
    var createNamespacePrefix = function(gridMenuEvent) {
      $scope.assertion.otherProperties.namespacePrefixes.push(
        { prefix: 'ns1', namespace: 'http://com.mycompany/service1' }
      );
      $scope.clearCurrentAssertionVerificationResult();
      $scope.assertionUpdate();
    };

    var removeNamespacePrefix = function(gridMenuEvent) {
      var selectedRow = $scope.xPathNamespacePrefixGridApi.selection.getSelectedRows()[0];
      var namespacePrefixes = $scope.assertion.otherProperties.namespacePrefixes;
      GeneralUtils.deleteArrayElementByProperty(namespacePrefixes, '$$hashKey', selectedRow.$$hashKey);
      $scope.clearCurrentAssertionVerificationResult();
      $scope.assertionUpdate();
    };

    $scope.xPathNamespacePrefixesGridOptions = {
      data: 'assertion.otherProperties.namespacePrefixes',
      enableRowHeaderSelection: false, multiSelect: false, enableGridMenu: true, gridMenuShowHideColumns: false,
      enableColumnMenus: false, rowHeight: 20, enableHorizontalScrollbar: uiGridConstants.scrollbars.NEVER,
      columnDefs: [
        {
          name: 'prefix', width: 65, minWidth: 65, headerTooltip: 'Double click to edit',
          sort: { direction: uiGridConstants.ASC, priority: 1 }, enableCellEdit: true,
          editableCellTemplate: 'namespacePrefixGridEditableCellTemplate.html'
        },
        {
          name: 'namespace', headerTooltip: 'Double click to edit', enableCellEdit: true,
          editableCellTemplate: 'namespacePrefixGridEditableCellTemplate.html'
        }
      ],
      gridMenuCustomItems: [
        { title: 'Create', order: 210, action: createNamespacePrefix,
          shown: function() {
            return !$rootScope.appStatus.isForbidden();
          }
        },
        { title: 'Delete', order: 220, action: removeNamespacePrefix,
          shown: function() {
            return !$rootScope.appStatus.isForbidden() &&
              $scope.xPathNamespacePrefixGridApi.selection.getSelectedRows().length === 1;
          }
        }
      ],
      onRegisterApi: function (gridApi) {
        $scope.xPathNamespacePrefixGridApi = gridApi;
        gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
          if (newValue !== oldValue) {
            $scope.clearCurrentAssertionVerificationResult();
            $scope.assertionUpdate();
          }
        });
      }
    };
  }
]);