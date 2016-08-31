'use strict';

angular.module('irontest').controller('FolderTreeController', ['$scope',
  function($scope) {
    $scope.treeConfig = {
      core: {
        error: function(error) {
          alert('treeCtrl: error from js tree - ' + angular.toJson(error));
        },
        check_callback: true
      },
      plugins: [ 'contextmenu' ],
      version: 1          //  ngJsTree property
    };

    $scope.treeData = [
      { id: '1', parent: '#', text: 'Root', state: { opened: true} },
      { id: '2', parent: '1', text: 'Child 1', state: { opened: true} },
      { id: '3', parent: '1', text: 'Child 2 erewradfdsfasfasdfasdfasdfasdfasfdafdfasfaf', state: { opened: true }}
    ];
    for (var i = 3; i < 40; i++) {
      $scope.treeData.push({ id: '' + (i + 1), parent: '1', text: 'Child ' + i, state: { opened: true }});
    }

    var createNodeCB = function(e, item) {
      console.log('Added new node with the text ' + item.node.text);
    };

    /*var readyCB = function() {
      // tree is ready and $scope.treeInstance is ready for use
      console.log('JS Tree Ready');
    };*/

    $scope.treeEventsObj = {
      //ready: readyCB,
      create_node: createNodeCB
    }
  }
]);
