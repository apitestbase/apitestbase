<div class="row" id="step-repeat-run-${ stepRepeatRun.id?string.computer }">
  <div class="col-lg-11"><h4>&lt;Repeat run ${ stepRepeatRun.index }&gt;</h4></div>
  <#if testcaseRun??>
    <div class="col-lg-1"><a href="#page-top">Top</a></div>
  </#if>
</div>

<div class="row">
  <div class="col-lg-1">Result:</div>
  <div class="col-lg-1 test-result-color-${stepRepeatRun.result}">${stepRepeatRun.result}</div>
  <div class="col-lg-1">Start Time:</div>
  <div class="col-lg-3">${ stepRepeatRun.startTime?datetime }</div>
  <div class="col-lg-1">Duration:</div>
  <div class="col-lg-1">${ stepRepeatRun.duration } ms</div>
</div>

<#if stepRepeatRun.atomicRunResult??>
  <#include "regularStepRepeatRun.ftl">
<#elseif stepRepeatRun.individualRuns??>
  <#include "dataDrivenStepRepeatRun.ftl">
</#if>