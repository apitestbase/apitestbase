'use strict';

angular.module('apitestbase').controller('CreateUserModalController', ['$scope', '$uibModalInstance',
  function($scope, $uibModalInstance) {
    $scope.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };

    $scope.ok = function() {
      $uibModalInstance.close($scope.username);
    };
  }
]);
