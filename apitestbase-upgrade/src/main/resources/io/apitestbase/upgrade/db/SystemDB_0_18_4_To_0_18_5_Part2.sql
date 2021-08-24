update teststep set api_request =
    '{"minClassName":".MQEnqueueOrPublishFromTextRequest",' ||
    '"body":"' || STRINGENCODE(UTF8TOSTRING(COALESCE(request, ''))) || '",' ||
    substring(other_properties from LOCATE('"rfh2Header":', other_properties))
where type = 'MQ' and action in ('Enqueue', 'Publish') and request_type = 'Text';

update teststep set other_properties = replace(other_properties, substring(other_properties from locate(',"rfh2Header":', other_properties)), '') || '}'
where type = 'MQ';

update teststep set request = null, step_data_backup = null where type = 'MQ';

update teststep set api_request = null where type = 'MQ' and action in ('CheckDepth', 'Clear', 'Dequeue');

ALTER TABLE TESTSTEP DROP COLUMN "REQUEST_TYPE";
ALTER TABLE TESTSTEP DROP COLUMN "REQUEST_FILENAME";
