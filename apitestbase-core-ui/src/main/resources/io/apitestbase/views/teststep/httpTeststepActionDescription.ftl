<#t>Invoke HTTP API "${ (endpoint.url??)?then(endpoint.url, 'null') }" using method ${ apiRequest.method }
<#if endpoint.username??> with username "${ endpoint.username }"</#if>.