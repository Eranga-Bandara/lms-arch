package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.util.HttpServlet2;
import lk.ijse.dep9.dto.ReturnDTO;
import lk.ijse.dep9.dto.ReturnItemDTO;
import lk.ijse.dep9.service.ServiceFactory;
import lk.ijse.dep9.service.ServiceTypes;
import lk.ijse.dep9.service.SuperService;
import lk.ijse.dep9.service.custom.ReturnService;
import lk.ijse.dep9.util.ConnectionUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "ReturnServlet", value = "/returns/*")
public class ReturnServlet extends HttpServlet2 {

    @Resource(lookup = "java:comp/env/jdbc/lms")
    private DataSource pool;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getPathInfo() != null && !request.getPathInfo().equals("/")){
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
            return;
        }
       try{
           if(request.getContentType() == null || request.getContentType().startsWith("application/json")){
               response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON");
               return;
           }

           ReturnDTO returnDTO = JsonbBuilder.create().fromJson(request.getReader(), ReturnDTO.class);

           addReturnItems(returnDTO, response);
       }catch (JsonbException e){
           response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
       }
    }

    private void addReturnItems(ReturnDTO returnDTO, HttpServletResponse response) throws IOException {
        /* Data Validation */
        if (returnDTO.getMemberId() == null || !returnDTO.getMemberId().matches("^([A-Fa-f0-9]{8}(-[A-Fa-f0-9]{4}){3}-[A-Fa-f0-9]{12})$")){
            throw new JsonbException("Member id is empty or invalid");
        } else if (returnDTO.getReturnItems().isEmpty()) {
            throw new JsonbException("No return items have been found");
        } else if (returnDTO.getReturnItems().stream().anyMatch(Objects::isNull)) {  // dto -> dto == null
            throw new JsonbException("Null items have been found in the list");
        } else if (returnDTO.getReturnItems().stream().anyMatch(item ->
            item.getIssueNoteId() == null || item.getIsbn() == null || !item.getIsbn().matches("([0-9][0-9\\\\-]*[0-9])"))) {
            throw new JsonbException("Some items are invalid");
        }
        Set<ReturnItemDTO> returnItems = returnDTO.getReturnItems().stream().collect(Collectors.toSet());

        /*  Business Validation  */
        try(Connection connection = pool.getConnection()) {
            ConnectionUtil.setConnection(connection);
            ReturnService returnService = ServiceFactory.getInstance().getService(ServiceTypes.RETURN);
            returnService.updateReturnStatus(returnDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(returnDTO, response.getWriter());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
