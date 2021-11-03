<#ftl encoding='UTF-8'>

<#assign teststep = atomicRunResult.teststep>
<#if teststep.apiRequest??>  <#-- not all test steps have apiRequest (e.g. Wait step) -->
  <#assign apiRequest = teststep.apiRequest>
</#if>
<#assign stepOtherProperties = teststep.otherProperties>
<#if atomicRunResult.response??>  <#-- not all atomic step runs have response (e.g. Wait step) -->
  <#assign response = atomicRunResult.response>
</#if>
<#assign assertionVerifications = atomicRunResult.assertionVerifications>

<div class="row">
  <div class="col-lg-1">Action:</div>
  <div class="col-lg-11">
    <#include "${stepType?lower_case}TeststepActionDescription.ftl">
  </div>
</div>

<#-- Error info -->
<#if atomicRunResult.errorMessage??>
  <div class="row">
    <div class="col-lg-1">Error:</div>
    <div class="col-lg-11">${atomicRunResult.errorMessage}</div>
  </div>
</#if>

<#-- Request, Response, and Assertions info -->
<#assign teststepTypes = ["HTTP", "SOAP", "DB", "JMS", "FTP", "MQ", "AMQP", "MQTT", "HTTPStubRequestsCheck"]>
<#if teststepTypes?seq_contains(stepType) && !(stepType == 'MQ' && stepAction?? && stepAction == 'Clear')>
  <div class="form-group"></div> <#-- spacer -->

  <#assign hasRequestTab = !(stepType == 'MQ' && stepAction?? && (stepAction == 'CheckDepth' || stepAction == 'Dequeue')) &&
    !(stepType == 'JMS' && stepAction?? && (stepAction == 'CheckDepth' || stepAction == 'Clear' || stepAction == 'Browse')) &&
    !['HTTPStubRequestsCheck']?seq_contains(stepType)>
  <#assign hasResponseTab = !(stepType == 'MQ' && stepAction?? && (stepAction == 'Enqueue' || stepAction == 'Publish')) &&
    !(stepType == 'JMS' && stepAction?? && (stepAction == 'Send' || stepAction == 'Publish')) &&
    !['FTP', 'AMQP', 'MQTT']?seq_contains(stepType)>
  <#assign hasAssertionsTab = !(stepType == 'MQ' && stepAction?? && (stepAction == 'Enqueue' || stepAction == 'Publish')) &&
    !(stepType == 'JMS' && stepAction?? && (stepAction == 'Clear' || stepAction == 'Send' || stepAction == 'Publish')) &&
    !['FTP', 'AMQP', 'MQTT']?seq_contains(stepType)>
  <div>
    <#-- Nav tabs -->
    <ul class="nav nav-tabs tabs-in-test-report" role="tablist">
      <#-- use data-target attribute instead of href attribute on the anchor elements, to avoid spoiling routes of
        angular app on the test case edit view. Refer to https://stackoverflow.com/questions/19225968/bootstrap-tab-is-not-working-when-tab-with-data-target-instead-of-href for more details -->
      <#if hasRequestTab>
        <li role="presentation" ${ (hasResponseTab)?then('', 'class=active') }><a data-target="#${idPrefix}-${ atomicRunId?string.computer }-request" aria-controls="request" role="tab" data-toggle="tab">Request</a></li>
      </#if>
      <#if hasResponseTab>
        <#-- set Response tab to be active as response is the most interesting information -->
        <li role="presentation" class="active"><a data-target="#${idPrefix}-${ atomicRunId?string.computer }-response" aria-controls="response" role="tab" data-toggle="tab">Response</a></li>
      </#if>
      <#if hasAssertionsTab>
        <li role="presentation"><a data-target="#${idPrefix}-${ atomicRunId?string.computer }-assertions" aria-controls="assertions" role="tab" data-toggle="tab">Assertions</a></li>
      </#if>
    </ul>

    <#-- Tab panes -->
    <div class="tab-content" id="request-response-assertions-tab-panes">
      <#if hasRequestTab>
        <div role="tabpanel" class="tab-pane ${ (hasResponseTab)?then('', 'active') }" id="${idPrefix}-${ atomicRunId?string.computer }-request">
          <#include "teststepRequest.ftl">
        </div>
      </#if>
      <#if hasResponseTab>
        <div role="tabpanel" class="tab-pane active" id="${idPrefix}-${ atomicRunId?string.computer }-response">
          <#include "teststepResponse.ftl">
        </div>
      </#if>
      <#if hasAssertionsTab>
        <div role="tabpanel" class="tab-pane" id="${idPrefix}-${ atomicRunId?string.computer }-assertions">
          <#include "teststepAssertions.ftl">
        </div>
      </#if>
    </div>
  </div>
</#if>

<#-- Some additional info about the step run -->
<#if atomicRunResult.infoMessage??>
  <div class="row">
    <div class="col-lg-1">Info:</div>
    <div class="col-lg-11">${atomicRunResult.infoMessage}</div>
  </div>
</#if>