package lk.ijse.dep9.api;

import ch.qos.logback.classic.spi.IThrowableProxy;
import jakarta.annotation.Resource;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lk.ijse.dep9.api.exception.ValidationException;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.api.util.HttpServlet2;
import lk.ijse.dep9.dto.util.Groups;
import lk.ijse.dep9.entity.Member;
import lk.ijse.dep9.exception.ResponseStatusException;
import lk.ijse.dep9.service.BOLogics;
import lk.ijse.dep9.service.ServiceFactory;
import lk.ijse.dep9.service.ServiceTypes;
import lk.ijse.dep9.service.SuperService;
import lk.ijse.dep9.service.custom.MemberService;
import lk.ijse.dep9.util.ConnectionUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "MemberServlet", value = "/members/*", loadOnStartup = 0)
public class MemberServlet extends HttpServlet2 {

    @Resource(lookup = "java:comp/env/jdbc/lms")
    private DataSource pool;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            String query = request.getParameter("q");
            String size = request.getParameter("size");
            String page = request.getParameter("page");

            if (query != null && size != null && page != null) {
                if (!size.matches("\\d+") || !page.matches("\\d+")) {
                    throw new ResponseStatusException(400, "Invalid page or size");
                } else {
                    searchPaginatedMembers(query, Integer.parseInt(size), Integer.parseInt(page), response);
                }
            } else if (query != null) {
//                searchMembers(query, response);
            } else if (size != null && page != null) {
                if (!size.matches("\\d+") || !page.matches("\\d+")) {
                    throw new ResponseStatusException(400, "Invalid page or size");
                } else {
//                    loadAllPaginatedMembers(Integer.parseInt(size), Integer.parseInt(page), response);
                }
            } else {
//                loadAllMembers(response);
            }
        } else {
            Matcher matcher = Pattern.compile("^/([A-Fa-f0-9]{8}(-[A-Fa-f0-9]{4}){3}-[A-Fa-f0-9]{12})/?$").matcher(request.getPathInfo());
            if (matcher.matches()) {
                getMemberDetails(matcher.group(1), response);
            } else {
                throw new ResponseStatusException(501);
            }
        }
    }

    private void searchPaginatedMembers(String query, int size, int page, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            ConnectionUtil.setConnection(connection);
            MemberService memberService = ServiceFactory.getInstance().getService(ServiceTypes.MEMBER);
            List<MemberDTO> members = memberService.findMembers(query, size, page);
            response.setIntHeader("X-Total-Count", members.size());
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(members, response.getWriter());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void getMemberDetails(String memberId, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
           ConnectionUtil.setConnection(connection);
            MemberService memberService = ServiceFactory.getInstance().getService(ServiceTypes.MEMBER);
            MemberDTO memberDetails = memberService.getMemberDetails(memberId);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(memberDetails);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            try{
                if (request.getContentType() == null || !request.getContentType().startsWith("application/json")) {
                    throw new JsonbException("Invalid JSON");
                }

                MemberDTO member = JsonbBuilder.create().fromJson(request.getReader(), MemberDTO.class);

                Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
                Set<ConstraintViolation<MemberDTO>> violations = validator.validate(member);
                if (!violations.isEmpty()){
                    throw new ValidationException(violations.stream().findAny().get().getMessage());
                }

                try(Connection connection = pool.getConnection()) {
                    MemberService memberService = ServiceFactory.getInstance().getService(ServiceTypes.MEMBER);
                    memberService.signupMember(member);
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    response.setContentType("application/json");
                    JsonbBuilder.create().toJson(member, response.getWriter());
                    ConnectionUtil.setConnection(connection);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }catch (JsonbException e){
//                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                throw new ResponseStatusException(400, e.getMessage(), e);
            }
        } else {
//            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
            throw new ResponseStatusException(501);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() == null || request.getPathInfo().startsWith("/")){
//            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
//            return;
            throw new ResponseStatusException(501);
        }

        Matcher matcher = Pattern.compile("^/([A-Fa-f0-9]{8}(-[A-Fa-f0-9]{4}){3}-[A-Fa-f0-9]{12})/?$").matcher(request.getPathInfo());
        if (matcher.matches()){
            deleteMember(matcher.group(1), response);
        }else {
//            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
            throw new ResponseStatusException(501);
        }
    }

    private void deleteMember(String memberId, HttpServletResponse response){
        try(Connection connection = pool.getConnection()) {
            ConnectionUtil.setConnection(connection);
            MemberService memberService = ServiceFactory.getInstance().getService(ServiceTypes.MEMBER);
            memberService.removeMemberAccount(memberId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")){
//            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
//            return;
            throw new ResponseStatusException(501);
        }

        Matcher matcher = Pattern.compile("^/([A-Fa-f0-9]{8}(-[A-Fa-f0-9]{4}){3}-[A-Fa-f0-9]{12})/?$").matcher(request.getPathInfo());
        if (matcher.matches()){
            updateMember(matcher.group(1), request, response);
        }else {
//            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
            throw new ResponseStatusException(501);
        }
    }

    private void updateMember(String memberId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try{
            if(request.getContentType() == null || !request.getContentType().startsWith("application/json")){
                throw new JsonbException("Invalid JSON");
            }

            MemberDTO member = JsonbBuilder.create().fromJson(request.getReader(), MemberDTO.class);
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            Set<ConstraintViolation<MemberDTO>> violations = validator.validate(member, Groups.Update.class);
            violations.stream().findAny().ifPresent(
                    violate -> {
                        throw new ValidationException(violate.getMessage());
                    });

            if(!memberId.equals(member.getId()))throw new ValidationException("Member ids are mismatched");

            try(Connection connection = pool.getConnection()) {
               ConnectionUtil.setConnection(connection);
                MemberService memberService = ServiceFactory.getInstance().getService(ServiceTypes.MEMBER);
                memberService.updateMemberDetails(member);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }catch(JsonbException e){
            throw new ResponseStatusException(400, e.getMessage(), e);
        }
    }
}
