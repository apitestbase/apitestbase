<div class="form-group"></div> <#-- spacer -->

<div>
  <#-- Nav tabs -->
  <ul class="nav nav-tabs tabs-in-test-report" role="tablist">
    <#-- use data-target attribute instead of href attribute on the anchor elements, to avoid spoiling routes of
      angular app on the test case edit view. Refer to https://stackoverflow.com/questions/19225968/bootstrap-tab-is-not-working-when-tab-with-data-target-instead-of-href for more details -->
    <li role="presentation"><a data-target="#${idPrefix}-${ atomicRunId?string.computer }-request-properties" aria-controls="properties" role="tab" data-toggle="tab">Properties</a></li>
    <#-- set Body tab to be active as Body is the most interesting information -->
    <li role="presentation" class="active"><a data-target="#${idPrefix}-${ atomicRunId?string.computer }-request-body" aria-controls="body" role="tab" data-toggle="tab">Body</a></li>
  </ul>

  <#-- Tab panes -->
  <div class="tab-content">
    <#-- Properties tab pane -->
    <div role="tabpanel" class="tab-pane" id="${idPrefix}-${ atomicRunId?string.computer }-request-properties">
      <#if (apiRequest.properties?size > 0)>
        <div class="form-group"></div> <#-- spacer -->
        <div class="form-group">
          <div class="col-lg-8">
            <table class="table table-condensed table-bordered table-hover">
              <thead>
                <tr>
                  <th width="10%">Type</th>
                  <th width="30%">Name</th>
                  <th>Value</th>
                </tr>
              </thead>
              <tbody>
                <#list apiRequest.properties as property>
                  <tr>
                    <td>${ property.type }</td>
                    <td>${ property.name }</td>
                    <td>${ property.value }</td>
                  </tr>
                </#list>
              </tbody>
            </table>
          </div>
        </div>
      </#if>
    </div>
    <#-- Body tab pane -->
    <div role="tabpanel" class="tab-pane active" id="${idPrefix}-${ atomicRunId?string.computer }-request-body">
      <div class="form-group">
        <div class="col-lg-12">
          <textarea class="form-control message-body-textarea" readonly>${ generalUtilsAdapter.prettyPrintJSONOrXML(apiRequest.body) }</textarea>
        </div>
      </div>
    </div>
  </div>
</div>