package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.util.HttpServlet2;
import lk.ijse.dep9.dto.IssueNoteDTO;
import lombok.Data;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@WebServlet(name = "IssueNoteServlet", value = "/issue-notes/*")
public class IssueNoteServlet extends HttpServlet2 {

    @Resource(lookup = "java:comp/env/jdbc/lms")
    private DataSource pool;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getPathInfo() != null && !request.getPathInfo().equals("/")){
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
            return;
        }

        try{
            if(request.getContentType() == null || !request.getContentType().startsWith("application/json")){
                throw new JsonbException("Invalid JSON");
            }

            IssueNoteDTO issueNoteDTO = JsonbBuilder.create().fromJson(request.getReader(), IssueNoteDTO.class);
            createIssueNote(issueNoteDTO, response);
        }catch (JsonbException e){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void createIssueNote(IssueNoteDTO issueNoteDTO, HttpServletResponse response) throws IOException {

        /*  Data Validation  */

        if(issueNoteDTO.getMemberId() == null || !issueNoteDTO.getMemberId().matches("^([A-Fa-f0-9]{8}(-[A-Fa-f0-9]{4}){3}-[A-Fa-f0-9]{12})$")){
            throw new JsonbException("Member id is empty or invalid");
        } else if (issueNoteDTO.getBooks().isEmpty()) {
            throw new JsonbException("Cannot place an issue note without books");  // handle ERD total participation relation between issueNoteDTO
        } else if (issueNoteDTO.getBooks().size() > 3) {
            throw new JsonbException("Cannot issue more than 3 books");
        } else if (issueNoteDTO.getBooks().stream().anyMatch(isbn -> isbn == null || !isbn.matches("^(\\d[\\d\\\\-]*\\d)$"))) {
            throw new JsonbException("Invalid ISBN in the books list");
        }
        /*  Duplicates finding in issue note  */
        else if(issueNoteDTO.getBooks().stream().collect(Collectors.toSet()).size() != issueNoteDTO.getBooks().size()){
            throw new JsonbException("Duplicate isbn has been found");
        }

    }
}
