<div class="row">
  <div class="col-lg-12">
    <ul class="list-unstyled">
      <#list testcaseRun.stepRuns as stepRun>
        <#include "teststep/stepRunOutline.ftl">
      </#list>
    </ul>
  </div>
</div>

<div class="separator"></div>

<#list testcaseRun.stepRuns as stepRun>
  <#include "teststep/stepRun.ftl">
</#list>