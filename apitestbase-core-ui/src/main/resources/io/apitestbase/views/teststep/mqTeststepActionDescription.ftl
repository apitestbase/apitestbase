<#if stepOtherProperties.destinationType == "Queue">
  <#if teststep.action??>
    <#if teststep.action == "Clear">
      Clear
    <#elseif teststep.action == "Enqueue">
      Enqueue message
      <#if apiRequest.class.simpleName == "MQEnqueueOrPublishFromTextRequest">
        from text
      <#elseif apiRequest.class.simpleName == "MQEnqueueOrPublishFromFileRequest">
        from file "${ apiRequest.fileName }"
      </#if>
      into
    <#elseif teststep.action == "CheckDepth">
      Check depth of
    <#elseif teststep.action == "Dequeue">
      Dequeue message from
    </#if>
  </#if>
  queue "${ (stepOtherProperties.queueName??)?then(stepOtherProperties.queueName, 'null') }"
<#elseif stepOtherProperties.destinationType == "Topic">
  Publish message
  <#if apiRequest.class.simpleName == "MQEnqueueOrPublishFromTextRequest">
    from text
  <#elseif apiRequest.class.simpleName == "MQEnqueueOrPublishFromFileRequest">
    from file "${ apiRequest.fileName }"
  </#if>
  onto topic with topic string "${ (stepOtherProperties.topicString??)?then(stepOtherProperties.topicString, 'null') }"
</#if>
<#t>on queue manager "${ (endpoint.constructedUrl??)?then(endpoint.constructedUrl, 'null') }"
<#t><#if endpointProperties.connectionMode == "Client"> through channel "${ (endpointProperties.svrConnChannelName??)?then(endpointProperties.svrConnChannelName, 'null') }"</#if>.