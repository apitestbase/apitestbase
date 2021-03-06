'use strict';

angular.module('apitestbase').factory('TestcaseRuns', ['$resource',
  function($resource) {
    return $resource('api/testcaseruns/:testcaseRunId', {
      testcaseRunId: '@id'
    }, {
      getStepRunHTMLReport: {
        url: 'api/teststepruns/:stepRunId/htmlreport',
        method: 'GET',
        transformResponse: function (data) {  //  avoid angularjs turning response html into array of chars
          return { report: data };
        }
      },
      getStepIndividualRunHTMLReport: {
        url: 'api/teststepindividualruns/:stepIndividualRunId/htmlreport',
        method: 'GET',
        transformResponse: function (data) {  //  avoid angularjs turning response html into array of chars
          return { report: data };
        }
      },
      getStepRepeatRunHTMLReport: {
        url: 'api/teststeprepeatruns/:stepRepeatRunId/htmlreport',
        method: 'GET',
        transformResponse: function (data) {  //  avoid angularjs turning response html into array of chars
          return { report: data };
        }
      }
    });
  }
]);
