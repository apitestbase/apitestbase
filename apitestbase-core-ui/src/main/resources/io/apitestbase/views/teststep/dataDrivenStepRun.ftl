<#ftl encoding='UTF-8'>

<#list stepRun.individualRuns as stepIndividualRun>
  <#assign idPrefix = 'step-individual-run'>
  <#assign atomicRunId = stepIndividualRun.id>
  <#assign atomicRunResult = stepIndividualRun.atomicRunResult>

  <div class="row" id="${idPrefix}-${ stepIndividualRun.id?string.computer }">
    <div class="col-lg-11"><h4>[${ stepIndividualRun.caption }]</h4></div>
    <#if testcaseRun??>
      <div class="col-lg-1"><a href="#page-top">Top</a></div>
    </#if>
  </div>

  <div class="row">
    <div class="col-lg-1">Result:</div>
    <div class="col-lg-1 test-result-color-${stepIndividualRun.result}">${stepIndividualRun.result}</div>
    <div class="col-lg-1">Start Time:</div>
    <div class="col-lg-3">${ stepIndividualRun.startTime?datetime }</div>
    <div class="col-lg-1">Duration:</div>
    <div class="col-lg-1">${ stepIndividualRun.duration } ms</div>
  </div>

  <#include "stepAtomicRunResult.ftl">
</#list>