package io.apitestbase.resources;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.apitestbase.core.teststep.*;
import io.apitestbase.db.*;
import io.apitestbase.models.AppInfo;
import io.apitestbase.models.DataTable;
import io.apitestbase.models.UDP;
import io.apitestbase.models.assertion.Assertion;
import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.teststep.MQRFH2Folder;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.models.teststep.TeststepWrapper;
import io.apitestbase.models.teststep.apirequest.APIRequest;
import io.apitestbase.models.teststep.apirequest.APIRequestFile;
import io.apitestbase.models.teststep.apirequest.DBRequest;
import io.apitestbase.utils.GeneralUtils;
import io.apitestbase.utils.PasswordUtils;
import io.apitestbase.utils.XMLUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.apitestbase.APITestBaseConstants.IMPLICIT_PROPERTY_DATE_TIME_FORMAT;
import static io.apitestbase.APITestBaseConstants.IMPLICIT_PROPERTY_NAME_TEST_STEP_START_TIME;

@Path("/testcases/{testcaseId}/teststeps") @Produces({ MediaType.APPLICATION_JSON })
public class TeststepResource {
    private AppInfo appInfo;
    private TeststepDAO teststepDAO;
    private UDPDAO udpDAO;
    private UtilsDAO utilsDAO;
    private DataTableDAO dataTableDAO;
    private AssertionDAO assertionDAO;
    private EndpointDAO endpointDAO;

    public TeststepResource(AppInfo appInfo, TeststepDAO teststepDAO, UDPDAO udpDAO, UtilsDAO utilsDAO,
                            DataTableDAO dataTableDAO, AssertionDAO assertionDAO, EndpointDAO endpointDAO) {
        this.appInfo = appInfo;
        this.teststepDAO = teststepDAO;
        this.udpDAO = udpDAO;
        this.utilsDAO = utilsDAO;
        this.dataTableDAO = dataTableDAO;
        this.assertionDAO = assertionDAO;
        this.endpointDAO = endpointDAO;
    }

    @POST
    @PermitAll
    public Teststep create(Teststep teststep) throws JsonProcessingException {
        long teststepId = teststepDAO.insert(teststep, appInfo.getAppMode());

        return teststepDAO.findById_NoAssertions(teststepId);
    }

    private void populateParametersInWrapper(TeststepWrapper wrapper) {
        Teststep teststep = wrapper.getTeststep();
        if (Teststep.TYPE_DB.equals(teststep.getType())) {
            boolean isSQLRequestSingleSelectStatement;
            DBRequest dbRequest = (DBRequest) teststep.getApiRequest();
            try {
                isSQLRequestSingleSelectStatement = GeneralUtils.isSQLRequestSingleSelectStatement(dbRequest.getSqlScript());
            } catch (Exception e) {
                //  the SQL script is invalid, so it can't be a single select statement
                //  swallow the exception to avoid premature error message on UI (user is still editing the SQL script)
                //  the exception will popup on UI when user executes the script (the script will be reparsed at that time)
                isSQLRequestSingleSelectStatement = false;
            }
            wrapper.getParameters().put("isSQLRequestSingleSelectStatement", isSQLRequestSingleSelectStatement);
        }
    }

    @GET @Path("{teststepId}")
    @JsonView(ResourceJsonViews.TeststepEdit.class)
    public TeststepWrapper findById(@PathParam("teststepId") long teststepId) {
        TeststepWrapper wrapper = new TeststepWrapper();

        wrapper.setTeststep(_findById(teststepId));
        populateParametersInWrapper(wrapper);

        return wrapper;
    }

    private Teststep _findById(long teststepId) {
        Teststep teststep = teststepDAO.findById_NoAssertions(teststepId);
        PasswordUtils.maskEndpointPasswordForAPIResponse(teststep.getEndpoint());

        return teststep;
    }

    @PUT @Path("{teststepId}")
    @PermitAll
    @JsonView(ResourceJsonViews.TeststepEdit.class)
    public TeststepWrapper update(Teststep teststep) throws Exception {
        //  Restore otherProperties from system database for existing JSONValidAgainstJSONSchema or XMLValidAgainstXSD
        //  assertions, as they are not supposed to be updated through this API (currently used for UI only).
        //  Without this code, whenever a new JSONValidAgainstJSONSchema or XMLValidAgainstXSD assertion is added, or an
        //  existing JSONValidAgainstJSONSchema or XMLValidAgainstXSD assertion is deleted, all existing
        //  JSONValidAgainstJSONSchema or XMLValidAgainstXSD assertions in the same test step will see their
        //  otherProperties.fileBytes set to null in system database.
        List<Assertion> assertions = teststep.getAssertions();
        for (Assertion assertion: assertions) {
            if (assertion.getId() != null && (Assertion.TYPE_JSON_VALID_AGAINST_JSON_SCHEMA.endsWith(assertion.getType()) ||
                    Assertion.TYPE_XML_VALID_AGAINST_XSD.endsWith(assertion.getType()))) {
                assertion.setOtherProperties(assertionDAO.findById(assertion.getId()).getOtherProperties());
            }
        }

        teststepDAO.update(teststep);

        TeststepWrapper wrapper = new TeststepWrapper();
        Teststep newTeststep = _findById(teststep.getId());
        wrapper.setTeststep(newTeststep);
        populateParametersInWrapper(wrapper);

        return wrapper;
    }

    @DELETE @Path("{teststepId}")
    @PermitAll
    public void delete(@PathParam("teststepId") long teststepId) {
        teststepDAO.deleteById(teststepId);
    }

    @POST @Path("{teststepId}/unmanageEndpoint")
    @PermitAll
    public Teststep unmanageEndpoint(@PathParam("teststepId") long teststepId) {
        teststepDAO.unmanageEndpoint(teststepId);

        return _findById(teststepId);
    }

    /**
     * Run a test step individually (not as part of test case running).
     * This is a stateless operation, i.e. not persisting anything in database.
     * @param teststep
     * @return
     */
    @POST @Path("{teststepId}/run")
    @PermitAll
    public BasicTeststepRun run(Teststep teststep) throws Exception {
        //  fetch API request binary if its type is file
        if (teststep.getApiRequest() instanceof APIRequestFile) {
            teststep.setApiRequest(teststepDAO.getAPIRequestById(teststep.getId()));
        }

        Endpoint endpoint = teststep.getEndpoint();
        if (endpoint != null) {
            endpoint.setPassword(endpointDAO.getEncryptedPasswordById(endpoint.getId()));
        }

        //  gather referenceable string properties and endpoint properties
        List<UDP> testcaseUDPs = udpDAO.findByTestcaseId(teststep.getTestcaseId());
        Map<String, String> referenceableStringProperties = GeneralUtils.udpListToMap(testcaseUDPs);
        referenceableStringProperties.put(IMPLICIT_PROPERTY_NAME_TEST_STEP_START_TIME,
                IMPLICIT_PROPERTY_DATE_TIME_FORMAT.format(new Date()));
        DataTable teststepDataTable = dataTableDAO.getTeststepDataTable(teststep.getId(), true);
        DataTable testcaseDataTable = dataTableDAO.getTestcaseDataTable(teststep.getTestcaseId(), true);
        GeneralUtils.checkDuplicatePropertyNames(referenceableStringProperties.keySet(),
                teststepDataTable.getNonCaptionColumnNames(), testcaseDataTable.getNonCaptionColumnNames());
        Map<String, Endpoint> referenceableEndpointProperties = new HashMap<>();
        if (teststepDataTable.getRows().size() > 0) {
            referenceableStringProperties.putAll(teststepDataTable.getStringPropertiesInRow(0));
        }
        if (testcaseDataTable.getRows().size() > 0) {
            referenceableStringProperties.putAll(testcaseDataTable.getStringPropertiesInRow(0));
            referenceableEndpointProperties.putAll(testcaseDataTable.getEndpointPropertiesInRow(0));
        }

        //  run the test step
        TeststepRunner teststepRunner = TeststepRunnerFactory.getInstance().newTeststepRunner(
                teststep, utilsDAO, referenceableStringProperties, referenceableEndpointProperties,
                null, null);
        BasicTeststepRun basicTeststepRun = teststepRunner.run();

        //  for better display in browser, transform JSON/XML response to be pretty-printed
        switch (teststep.getType()) {
            case Teststep.TYPE_SOAP:
                HTTPAPIResponse soapAPIResponse = (HTTPAPIResponse) basicTeststepRun.getResponse();
                soapAPIResponse.setHttpBody(XMLUtils.prettyPrintXML(soapAPIResponse.getHttpBody()));
                break;
            case Teststep.TYPE_HTTP:
                HTTPAPIResponse httpAPIResponse = (HTTPAPIResponse) basicTeststepRun.getResponse();
                httpAPIResponse.setHttpBody(GeneralUtils.prettyPrintJSONOrXML(httpAPIResponse.getHttpBody()));
                break;
            case Teststep.TYPE_MQ:
                if (Teststep.ACTION_DEQUEUE.equals(teststep.getAction())) {
                    MQDequeueResponse mqDequeueResponse = (MQDequeueResponse) basicTeststepRun.getResponse();
                    if (mqDequeueResponse != null) {
                        mqDequeueResponse.setBodyAsText(GeneralUtils.prettyPrintJSONOrXML(mqDequeueResponse.getBodyAsText()));
                        if (mqDequeueResponse.getMqrfh2Header() != null) {
                            for (MQRFH2Folder mqrfh2Folder: mqDequeueResponse.getMqrfh2Header().getFolders()) {
                                mqrfh2Folder.setString(GeneralUtils.prettyPrintJSONOrXML(mqrfh2Folder.getString()));
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }

        return basicTeststepRun;
    }

    /**
     * Save the uploaded file as Teststep's API request file.
     * Use @POST instead of @PUT because ng-file-upload seems not working with PUT.
     * @param teststepId
     * @param inputStream
     * @param contentDispositionHeader
     * @return
     * @throws IOException
     */
    @POST @Path("{teststepId}/apiRequestFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @PermitAll
    @JsonView(ResourceJsonViews.TeststepEdit.class)
    public Teststep saveAPIRequestFile(@PathParam("teststepId") long teststepId,
                                    @FormDataParam("file") InputStream inputStream,
                                    @FormDataParam("file") FormDataContentDisposition contentDispositionHeader) throws IOException {
        return teststepDAO.saveApiRequestFile(teststepId, contentDispositionHeader.getFileName(), inputStream);
    }

    /**
     * Download Teststep's API request file.
     * @param teststepId
     * @return
     */
    @GET @Path("{teststepId}/apiRequestFile")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getAPIRequestFile(@PathParam("teststepId") long teststepId) {
        APIRequest apiRequest = teststepDAO.getAPIRequestById(teststepId);
        String fileName = null;
        byte[] fileBytes = null;
        if (apiRequest instanceof APIRequestFile) {
            APIRequestFile apiRequestFile = (APIRequestFile) apiRequest;
            fileBytes = apiRequestFile.getFileContent();
            fileName = apiRequestFile.getFileName();
        }

        return Response.ok(fileBytes)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .build();
    }

    @POST @Path("{teststepId}/useEndpointProperty")
    @PermitAll
    public Teststep useEndpointProperty(Teststep teststep) {
        teststepDAO.useEndpointProperty(teststep);

        return teststepDAO.findById_NoAssertions(teststep.getId());
    }

    @POST @Path("{teststepId}/useDirectEndpoint")
    @PermitAll
    public Teststep useDirectEndpoint(Teststep teststep) throws JsonProcessingException {
        teststepDAO.useDirectEndpoint(teststep, appInfo.getAppMode());

        return teststepDAO.findById_NoAssertions(teststep.getId());
    }
}
