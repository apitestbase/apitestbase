package io.apitestbase.resources;

import io.apitestbase.core.propertyextractor.PropertyExtractorRunner;
import io.apitestbase.core.propertyextractor.PropertyExtractorRunnerFactory;
import io.apitestbase.db.DataTableDAO;
import io.apitestbase.db.PropertyExtractorDAO;
import io.apitestbase.db.UserDefinedPropertyDAO;
import io.apitestbase.models.DataTable;
import io.apitestbase.models.UserDefinedProperty;
import io.apitestbase.models.propertyextractor.PropertyExtractionRequest;
import io.apitestbase.models.propertyextractor.PropertyExtractionResult;
import io.apitestbase.models.propertyextractor.PropertyExtractor;
import io.apitestbase.utils.IronTestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class PropertyExtractorResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssertionResource.class);

    private final UserDefinedPropertyDAO udpDAO;
    private final DataTableDAO dataTableDAO;
    private final PropertyExtractorDAO propertyExtractorDAO;

    public PropertyExtractorResource(UserDefinedPropertyDAO udpDAO, DataTableDAO dataTableDAO,
                                     PropertyExtractorDAO propertyExtractorDAO) {
        this.udpDAO = udpDAO;
        this.dataTableDAO = dataTableDAO;
        this.propertyExtractorDAO = propertyExtractorDAO;
    }

    @GET
    @Path("teststeps/{teststepId}/propertyExtractors")
    public List<PropertyExtractor> findByTeststepId(@PathParam("teststepId") long teststepId) {
        return propertyExtractorDAO.findByTeststepId(teststepId);
    }

    @POST
    @Path("teststeps/{teststepId}/propertyExtractors")
    @PermitAll
    public PropertyExtractor create(@PathParam("teststepId") long teststepId, PropertyExtractor propertyExtractor) {
        long id = propertyExtractorDAO.insert(teststepId, propertyExtractor);
        return propertyExtractorDAO.findById(id);
    }

    @PUT @Path("propertyExtractors/{propertyExtractorId}")
    @PermitAll
    public void update(PropertyExtractor propertyExtractor) {
        propertyExtractorDAO.update(propertyExtractor);
    }

    @DELETE @Path("propertyExtractors/{propertyExtractorId}")
    @PermitAll
    public void delete(@PathParam("propertyExtractorId") long propertyExtractorId) {
        propertyExtractorDAO.deleteById(propertyExtractorId);
    }

    /**
     * This is a stateless operation, i.e. not persisting anything in database.
     * @param propertyExtractionRequest
     * @return
     */
    @POST @Path("propertyExtractors/{propertyExtractorId}/extract")
    @PermitAll
    public PropertyExtractionResult extract(PropertyExtractionRequest propertyExtractionRequest) throws IOException {
        PropertyExtractor propertyExtractor = propertyExtractionRequest.getPropertyExtractor();

        //  gather referenceable string properties
        long testcaseId = propertyExtractorDAO.findTestcaseIdById(propertyExtractor.getId());
        List<UserDefinedProperty> testcaseUDPs = udpDAO.findByTestcaseId(testcaseId);
        Map<String, String> referenceableStringProperties = IronTestUtils.udpListToMap(testcaseUDPs);
        Set<String> udpNames = referenceableStringProperties.keySet();
        DataTable dataTable = dataTableDAO.getTestcaseDataTable(testcaseId, true);
        if (dataTable.getRows().size() > 0) {
            IronTestUtils.checkDuplicatePropertyNameBetweenDataTableAndUPDs(udpNames, dataTable);
            referenceableStringProperties.putAll(dataTable.getStringPropertiesInRow(0));
        }

        PropertyExtractorRunner propertyExtractorRunner = PropertyExtractorRunnerFactory.getInstance().create(
                propertyExtractor, referenceableStringProperties);
        String propertyExtractionInput = propertyExtractionRequest.getInput();
        PropertyExtractionResult result = new PropertyExtractionResult();
        try {
            result.setPropertyValue(propertyExtractorRunner.extract(propertyExtractionInput));
        } catch (Exception e) {
            LOGGER.error("Failed to extract property", e);
            result.setError(e.getMessage());
        }
        return result;
    }
}
