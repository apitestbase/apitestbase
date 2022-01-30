<ul class="list-unstyled data-driven-teststep-caption-list">
  <#list stepIndividualRuns as stepIndividualRun>
    <li>
      <a href="#step-individual-run-${ stepIndividualRun.id?string.computer }">
        <h5 class="test-result-color-${ stepIndividualRun.result }">
          <strong>[${ stepIndividualRun.caption }]</strong>
        </h5>
      </a>
    </li>
  </#list>
</ul>