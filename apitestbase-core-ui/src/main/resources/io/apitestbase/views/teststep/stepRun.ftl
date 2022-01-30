<#assign metaTeststep = stepRun.metaTeststep>
<#assign stepName = metaTeststep.name>
<#assign stepType = metaTeststep.type>
<#if metaTeststep.action??>  <#-- not all test steps have action (e.g. Wait step) -->
  <#assign stepAction = metaTeststep.action>
</#if>
<#if metaTeststep.endpoint??>  <#-- not all test steps have endpoint (e.g. Wait step) -->
  <#assign endpoint = metaTeststep.endpoint>
  <#assign endpointProperties = endpoint.otherProperties>
</#if>

<div class="row" id="step-run-${ stepRun.id?string.computer }">
  <div class="col-lg-11"><h4 ${ (stepRun.individualRuns??)?then('style="font-weight:bold"'?no_esc, '') }>${ stepName }</h4></div>
  <#if testcaseRun??>
    <div class="col-lg-1"><a href="#page-top">Top</a></div>
  </#if>
</div>

<div class="row">
  <div class="col-lg-1">Result:</div>
  <div class="col-lg-1 test-result-color-${stepRun.result}">${stepRun.result}</div>
  <div class="col-lg-1">Start Time:</div>
  <div class="col-lg-3">${ stepRun.startTime?datetime }</div>
  <div class="col-lg-1">Duration:</div>
  <div class="col-lg-1">${ stepRun.duration } ms</div>
</div>

<#if metaTeststep.description?? && metaTeststep.description?has_content>
  <div class="row">
    <div class="col-lg-1">Description:</div>
    <div class="col-lg-11">${ metaTeststep.description }</div>
  </div>
</#if>

<#if stepRun.atomicRunResult??>
  <#include "regularStepRun.ftl">
<#elseif stepRun.individualRuns??>
  <#include "dataDrivenStepRun.ftl">
<#elseif stepRun.repeatRuns??>
  <#include "repeatedStepRuns.ftl">
</#if>
<div class="row">&nbsp;</div>