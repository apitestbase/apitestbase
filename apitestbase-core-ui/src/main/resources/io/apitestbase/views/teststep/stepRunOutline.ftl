<li>
  <a href="#step-run-${ stepRun.id?string.computer }">
    <h5 class="test-result-color-${ stepRun.result }">
      <strong>${ stepRun.metaTeststep.name }</strong>
    </h5>
  </a>
</li>

<#if stepRun.individualRuns??>
  <#assign stepIndividualRuns = stepRun.individualRuns>
  <#include "stepIndividualRunsOutline.ftl">
</#if>

<#if stepRun.repeatRuns??>
  <ul class="list-unstyled teststep-repeatrun-list">
    <#list stepRun.repeatRuns as stepRepeatRun>
      <li>
        <a href="#step-repeat-run-${ stepRepeatRun.id?string.computer }">
          <h5 class="test-result-color-${ stepRepeatRun.result }">
            <strong>&lt;Repeat run ${ stepRepeatRun.index }&gt;</strong>
          </h5>
        </a>
      </li>
      <#if stepRepeatRun.individualRuns??>
        <#assign stepIndividualRuns = stepRepeatRun.individualRuns>
        <#include "stepIndividualRunsOutline.ftl">
      </#if>
    </#list>
  </ul>
</#if>

