<#import "../formatHTTPHeadersObj.ftl" as fmt>

<#list stepRun.response.allServeEvents as stubRequest>
  <div class="row">
    <div class="col-lg-1">Stub Request:</div>
    <form class="form-horizontal col-lg-11" role="form" novalidate>
      <div class="form-group">
        <div class="col-lg-2">Logged Time:</div>
        <div class="col-lg-3">${ stubRequest.request.loggedDate?number_to_datetime }</div>
      </div>
      <div class="form-group">
        <div class="col-lg-1">Method:</div>
        <div class="col-lg-1">${ stubRequest.request.method }</div>
        <div class="col-lg-2">Absolute URL:</div>
        <div class="col-lg-8">${ stubRequest.request.absoluteUrl }</div>
      </div>
      <div class="form-group">
        <div class="col-lg-1">Client IP:</div>
        <div class="col-lg-1">${ stubRequest.request.clientIp }</div>
        <div class="col-lg-offset-4 col-lg-2">Response Status Code:</div>
        <div class="col-lg-1">${ stubRequest.response.status }</div>
        <div class="col-lg-2">Response Delayed (ms):</div>
        <div class="col-lg-1">${ stubRequest.timing.addedDelay }</div>
      </div>
      <div class="form-group">
        <div class="col-lg-2">Request Headers:</div>
        <div class="col-lg-offset-4 col-lg-2">Response Headers:</div>
      </div>
      <div class="form-group">
        <div class="col-lg-6">
          <textarea name="requestHeaders" rows="5" class="form-control" readonly><#if stubRequest.request.headers??><@fmt.formatHTTPHeadersObj object=stubRequest.request.headers/></#if></textarea>
        </div>
        <div class="col-lg-6">
          <textarea name="responseHeaders" rows="5" class="form-control" readonly><#if stubRequest.response.headers??><@fmt.formatHTTPHeadersObj object=stubRequest.response.headers/></#if></textarea>
        </div>
      </div>
      <div class="form-group">
        <div class="col-lg-2">Request Body:</div>
        <div class="col-lg-offset-4 col-lg-2">Response Body:</div>
      </div>
      <div class="form-group">
        <div class="col-lg-6">
          <textarea name="requestBody" rows="8" class="form-control" readonly>${ stubRequest.request.body }</textarea>
        </div>
        <div class="col-lg-6">
          <textarea name="responseBody" rows="8" class="form-control" readonly>${ stubRequest.response.body }</textarea>
        </div>
      </div>
    </form>
  </div>
</#list>