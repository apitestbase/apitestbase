<#t>Put file ${ (apiRequest.class.simpleName = 'SftpPutRequestFileFromText')?then('(from text)', '') }
 to remote path "${ (stepOtherProperties.remoteFilePath??)?then(stepOtherProperties.remoteFilePath, 'null') }"
 on SFTP server "${ endpoint.constructedUrl }" with username "${ (endpoint.username??)?then(endpoint.username, 'null') }".