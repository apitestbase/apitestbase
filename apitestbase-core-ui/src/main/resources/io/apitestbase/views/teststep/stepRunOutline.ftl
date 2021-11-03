<li>
  <#if stepRun.atomicRunResult??>
    <#assign stepName = stepRun.atomicRunResult.teststep.name>
  <#elseif stepRun.individualRuns??>
    <#assign stepName = stepRun.individualRuns[0].atomicRunResult.teststep.name>
  </#if>

  <a href="#step-run-${ stepRun.id?string.computer }">
    <h5 class="test-result-color-${ stepRun.result }">
      <strong>${ stepName }</strong>
    </h5>
  </a>

  <#if stepRun.individualRuns??>
    <ul class="list-unstyled data-driven-teststep-caption-list">
      <#list stepRun.individualRuns as stepIndividualRun>
        <li>
          <a href="#step-individual-run-${ stepIndividualRun.id?string.computer }">
            <h5 class="test-result-color-${ stepIndividualRun.result }">
              <strong>[${ stepIndividualRun.caption }]</strong>
            </h5>
          </a>
        </li>
      </#list>
    </ul>
  </#if>
</li>