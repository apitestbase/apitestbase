<div class="col-lg-12">
  <div class="row">XPath "${ (assertionProperties.xPath??)?then(assertionProperties.xPath, 'null') }" evaluates to</div>
  <div class="row">
    <textarea class="form-control" rows="6" readonly>${ (assertionProperties.expectedValue??)?then(generalUtilsAdatper.prettyPrintJSONOrXML(assertionProperties.expectedValue), 'null') }</textarea>
  </div>
</div>