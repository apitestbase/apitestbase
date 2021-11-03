<#-- Extra response info for test step that has response HTTP headers -->
<#if response.httpHeaders?? && (response.httpHeaders?size > 0)>
  <div class="row">
    <div class="col-lg-12">HTTP Headers:</div>
  </div>
  <div class="form-group"></div> <#-- spacer -->
  <div class="row">
    <div class="col-lg-12">
      <textarea rows="6" class="form-control" readonly>${ response.httpHeaders?join("\n") }</textarea>
    </div>
  </div>
</#if>