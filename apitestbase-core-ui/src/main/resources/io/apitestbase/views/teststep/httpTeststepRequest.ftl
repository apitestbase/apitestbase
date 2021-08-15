<div class="row">
  <div class="col-lg-12">HTTP Headers:</div>
</div>
<div class="form-group"></div> <#-- spacer -->
<div class="row">
  <div class="col-lg-12">
    <textarea rows="6" class="form-control" readonly>${ apiRequest.headers?join("\n") }</textarea>
  </div>
</div>

<#if teststep.type == 'SOAP' || (teststep.type == 'HTTP' && apiRequest.method != 'GET' && apiRequest.method != 'DELETE')>
  <div class="form-group"></div> <#-- spacer -->
  <div class="row">
    <div class="col-lg-12">HTTP Body:</div>
  </div>
  <div class="form-group"></div> <#-- spacer -->
  <div class="row">
    <div class="col-lg-12">
      <textarea class="form-control message-body-textarea" readonly>${ generalUtilsAdatper.prettyPrintJSONOrXML(apiRequest.body) }</textarea>
    </div>
  </div>
</#if>