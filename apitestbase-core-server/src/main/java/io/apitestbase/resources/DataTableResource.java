package io.apitestbase.resources;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.db.DataTableCellDAO;
import io.apitestbase.db.DataTableColumnDAO;
import io.apitestbase.db.DataTableDAO;
import io.apitestbase.models.DataTable;
import io.apitestbase.models.DataTableCell;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class DataTableResource {
    private DataTableDAO dataTableDAO;
    private DataTableColumnDAO dataTableColumnDAO;
    private DataTableCellDAO dataTableCellDAO;

    public DataTableResource(DataTableDAO dataTableDAO, DataTableColumnDAO dataTableColumnDAO,
                             DataTableCellDAO dataTableCellDAO) {
        this.dataTableDAO = dataTableDAO;
        this.dataTableColumnDAO = dataTableColumnDAO;
        this.dataTableCellDAO = dataTableCellDAO;
    }

    @GET
    @Path("testcases/{testcaseId}/datatable")
    @JsonView(ResourceJsonViews.DataTableUIGrid.class)
    public DataTable findByTestcaseId(@PathParam("testcaseId") long testcaseId) {
        return dataTableDAO.getTestcaseDataTable(testcaseId, false);
    }

    @POST @PermitAll
    @Path("testcases/{testcaseId}/datatable/addColumn")
    @JsonView(ResourceJsonViews.DataTableUIGrid.class)
    public DataTable addColumn(@PathParam("testcaseId") long testcaseId, @QueryParam("columnType") String columnType) {
        dataTableColumnDAO.insert(testcaseId, columnType);
        return dataTableDAO.getTestcaseDataTable(testcaseId, false);
    }

    @POST @PermitAll
    @Path("testcases/{testcaseId}/datatable/deleteColumn")
    @JsonView(ResourceJsonViews.DataTableUIGrid.class)
    public DataTable deleteColumn(@PathParam("testcaseId") long testcaseId, @QueryParam("columnId") long columnId) {
        dataTableColumnDAO.delete(columnId);
        return dataTableDAO.getTestcaseDataTable(testcaseId, false);
    }

    @POST @PermitAll
    @Path("testcases/{testcaseId}/datatable/renameColumn")
    public DataTable renameColumn(@PathParam("testcaseId") long testcaseId, @QueryParam("columnId") long columnId,
                                  @QueryParam("newName") String newName) {
        dataTableColumnDAO.rename(columnId, newName);
        return dataTableDAO.getTestcaseDataTable(testcaseId, false);
    }

    @POST @PermitAll
    @Path("testcases/{testcaseId}/datatable/moveColumn")
    public DataTable moveColumn(@PathParam("testcaseId") long testcaseId,
                                @QueryParam("fromSequence") short fromSequence, @QueryParam("toSequence") short toSequence) {
        dataTableColumnDAO.moveInTestcase(testcaseId, fromSequence, toSequence);
        return dataTableDAO.getTestcaseDataTable(testcaseId, false);
    }

    @POST @PermitAll
    @Path("testcases/{testcaseId}/datatable/addRow")
    @JsonView(ResourceJsonViews.DataTableUIGrid.class)
    public DataTable addRow(@PathParam("testcaseId") long testcaseId) {
        dataTableCellDAO.addRow(testcaseId);
        return dataTableDAO.getTestcaseDataTable(testcaseId, false);
    }

    @POST @PermitAll
    @Path("testcases/{testcaseId}/datatable/deleteRow")
    @JsonView(ResourceJsonViews.DataTableUIGrid.class)
    public DataTable deleteRow(@PathParam("testcaseId") long testcaseId, @QueryParam("rowSequence") short rowSequence) {
        dataTableCellDAO.deleteRow(testcaseId, rowSequence);
        return dataTableDAO.getTestcaseDataTable(testcaseId, false);
    }

    @POST @PermitAll
    @Path("testcases/{testcaseId}/datatable/updateCell")
    public void updateCell(DataTableCell dataTableCell) {
        dataTableCellDAO.update(dataTableCell,
                dataTableCell.getEndpoint() == null ? null : dataTableCell.getEndpoint().getId());
    }
}
