update teststep set api_request = '{"minClassName":".HTTPRequest",' ||
    replace(replace(substring(other_properties from 2 for length(other_properties) - 2), '"httpMethod":', '"method":'), '"httpHeaders":', '"headers":') ||
    ',"body":"' || STRINGENCODE(UTF8TOSTRING(COALESCE(request, ''))) || '"}'
where type = 'HTTP';

update teststep set request = null, other_properties = null where type = 'HTTP';

update teststep set api_request = '{"minClassName":".SOAPRequest",' ||
    replace(substring(other_properties from 2 for length(other_properties) - 2), '"httpHeaders":', '"headers":') ||
    ',"body":"' || STRINGENCODE(UTF8TOSTRING(COALESCE(request, ''))) || '"}'
where type = 'SOAP';

update teststep set request = null, other_properties = null where type = 'SOAP';