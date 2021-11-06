<div class="row">
  <#if apiRequest.class.simpleName == "MQEnqueueOrPublishFromTextRequest">
    <div class="col-lg-1">
      Body:
    </div>
    <div class="col-lg-11">
      <textarea class="form-control message-body-textarea" readonly>${ generalUtilsAdapter.prettyPrintJSONOrXML(apiRequest.body) }</textarea>
    </div>
  <#elseif apiRequest.class.simpleName == "MQEnqueueOrPublishFromFileRequest">
    <div class="col-lg-12">
      <a href="data:application/octet-stream;base64,${ generalUtilsAdapter.base64EncodeByteArray(apiRequest.fileContent) }"
         download="${ apiRequest.fileName }">${ apiRequest.fileName }</a>
    </div>
  </#if>
</div>
<#if apiRequest.rfh2Header??>
  <div class="form-group"></div> <#-- spacer -->
  <div class="row">
    <div class="col-lg-1">
      MQRFH2 Header Folders:
    </div>
    <div class="col-lg-11">
      <#list apiRequest.rfh2Header.folders as mqrfh2Folder>
        <div class="row">
          <div class="col-lg-12">
            <textarea class="form-control" rows="8" readonly>${ generalUtilsAdapter.prettyPrintJSONOrXML(mqrfh2Folder.string) }</textarea>
          </div>
        </div>
        <div class="form-group"></div> <#-- spacer -->
      </#list>
    </div>
  </div>
</#if>