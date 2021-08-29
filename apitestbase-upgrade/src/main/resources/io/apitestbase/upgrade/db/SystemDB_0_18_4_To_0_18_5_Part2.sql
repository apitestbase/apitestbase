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

update teststep set other_properties = null, api_request = '{"minClassName":".DBRequest",' ||
    '"sqlScript":"' || STRINGENCODE(UTF8TOSTRING(COALESCE(request, ''))) || '"}'
where type = 'DB';

update teststep set request = null where type = 'DB';

update teststep set api_request = '{"minClassName":".AMQPRequest",' ||
    '"body":"' || STRINGENCODE(UTF8TOSTRING(COALESCE(request, ''))) || '"}'
where type = 'AMQP';

update teststep set request = null where type = 'AMQP';

ALTER TABLE TESTSTEP DROP COLUMN "REQUEST";

delete from testcase_run;