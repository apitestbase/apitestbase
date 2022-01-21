<div class="form-group"></div> <#-- spacer -->

<#if teststep.type == 'HTTP' || teststep.type == 'SOAP'>
  <#include "httpTeststepRequest.ftl">
<#elseif teststep.type == "JMS">
  <#include "jmsTeststepRequest.ftl">
<#elseif teststep.type == "DB">
  <#include "dbTeststepRequest.ftl">
<#elseif teststep.type == "FTP">
  <#include "ftpTeststepRequest.ftl">
<#elseif teststep.type == "SFTP">
  <#include "sftpTeststepRequest.ftl">
<#elseif teststep.type == "AMQP">
  <#include "amqpTeststepRequest.ftl">
<#elseif teststep.type == "MQ">
  <#include "mqTeststepRequest.ftl">
<#elseif teststep.type == "MQTT">
  <#include "mqttTeststepRequest.ftl">
</#if>