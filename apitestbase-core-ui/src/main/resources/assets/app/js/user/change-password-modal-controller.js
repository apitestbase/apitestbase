'use strict';

angular.module('apitestbase').controller('ChangePasswordModalController', ['$scope', '$uibModalInstance',
  function($scope, $uibModalInstance) {
    $scope.ok = function() {
      $uibModalInstance.close($scope.newPassword);
    };

    $scope.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };
  }
]);