'use strict';

angular.module('apitestbase').controller('UDPValueTextareaEditorModalController', ['$scope', 'udp',
  function($scope, udp) {
    $scope.udp = udp;
  }
]);
