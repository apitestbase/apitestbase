update teststep set api_request = replace(replace(api_request, '"fileFrom":"Text",', ''), '"fileFrom":"File",', '')
where type = 'FTP';

update teststep set other_properties = '{' || substring(api_request from locate('"remoteFilePath":', api_request) for (locate(',"file', api_request) - locate('"remoteFilePath":', api_request))) || '}'
where type = 'FTP';

update teststep set api_request = replace(api_request, substring(api_request from locate('"remoteFilePath":', api_request) for (locate(',"file', api_request) - locate('"remoteFilePath":', api_request)) + 1), '')
where type = 'FTP';

