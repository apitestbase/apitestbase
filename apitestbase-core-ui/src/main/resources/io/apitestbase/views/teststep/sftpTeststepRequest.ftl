<div class="row">
  <div class="col-lg-12">
    <#if apiRequest.class.simpleName = 'SftpPutRequestFileFromText'>
      <textarea class="form-control message-body-textarea" readonly>${ generalUtilsAdapter.prettyPrintJSONOrXML(apiRequest.fileContent) }</textarea>
    <#else>
      <a href="data:;base64,${ generalUtilsAdapter.base64EncodeByteArray(apiRequest.fileContent) }"
          download="${ apiRequest.fileName }">${ apiRequest.fileName }</a>
    </#if>
  </div>
</div>