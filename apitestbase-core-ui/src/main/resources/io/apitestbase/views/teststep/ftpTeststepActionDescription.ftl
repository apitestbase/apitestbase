<#t>Put file ${ (apiRequest.class.simpleName = 'FtpPutRequestFileFromText')?then('(from text)', '') }
 to remote path "${ (stepOtherProperties.remoteFilePath??)?then(stepOtherProperties.remoteFilePath, 'null') }"
 on FTP server "${ endpoint.constructedUrl }" with username "${ (endpoint.username??)?then(endpoint.username, 'null') }".