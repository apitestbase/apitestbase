update teststep set api_request = '{"minClassName":".HTTPRequest",' ||
    replace(replace(substring(other_properties from 2 for length(other_properties) - 2), '"httpMethod":', '"method":'), '"httpHeaders":', '"headers":') ||
    ',"body":"' || STRINGENCODE(UTF8TOSTRING(request)) || '"}'
where type = 'HTTP';