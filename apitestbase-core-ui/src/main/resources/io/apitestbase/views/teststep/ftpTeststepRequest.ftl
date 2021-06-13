<div class="row">
  <div class="col-lg-12">
    <#if apiRequest.fileFrom = 'Text'>
      <textarea class="form-control message-body-textarea" readonly>${ generalUtilsAdatper.prettyPrintJSONOrXML(apiRequest.fileContent) }</textarea>
    <#else>
      <a href="data:;base64,${ generalUtilsAdatper.base64EncodeByteArray(apiRequest.fileContent) }"
          download="${ apiRequest.fileName }">${ apiRequest.fileName }</a>
    </#if>
  </div>
</div>