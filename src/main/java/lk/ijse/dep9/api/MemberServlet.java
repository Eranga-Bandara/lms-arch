package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.api.util.HttpServlet2;
import lk.ijse.dep9.exception.ResponseStatusException;
import lk.ijse.dep9.service.BOLogics;
import lk.ijse.dep9.util.ConnectionUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "MemberServlet", value = "/members/*", loadOnStartup = 0)
public class MemberServlet extends HttpServlet2 {

    @Resource(lookup = "java:comp/env/jdbc/lms")
    private DataSource pool;

    //    @Override
//    public void init() throws ServletException {
//        try {
//            InitialContext ctx = new InitialContext();
//            pool = (DataSource) ctx.lookup("jdbc/dep9-lms");
//        } catch (NamingException e) {
//            throw new RuntimeException(e);
//        }
//    }
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
                searchMembers(query, response);
            } else if (size != null && page != null) {
                if (!size.matches("\\d+") || !page.matches("\\d+")) {
                    throw new ResponseStatusException(400, "Invalid page or size");
                } else {
                    loadAllPaginatedMembers(Integer.parseInt(size), Integer.parseInt(page), response);
                }
            } else {
                loadAllMembers(response);
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

    private void loadAllMembers(HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            Statement stm = connection.createStatement();
            String sql = "SELECT * FROM member";
            ResultSet rst = stm.executeQuery(sql);
            ArrayList<MemberDTO> members = new ArrayList<>();

            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                MemberDTO memberDTO = new MemberDTO(id, name, address, contact);
                members.add(memberDTO);
            }

            response.setContentType("application/json");
            JsonbBuilder.create().toJson(members, response.getWriter());
        } catch (SQLException e) {
//            e.printStackTrace();
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch members");
            throw new RuntimeException(e);
        }
    }

    private void loadAllPaginatedMembers(int size, int page, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            Statement stmCount = connection.createStatement();
            String sql = "SELECT COUNT(id) FROM member";
            ResultSet rst1 = stmCount.executeQuery(sql);
            rst1.next();
            int totalMembers = rst1.getInt(1);
            response.addIntHeader("X-Total-Count", totalMembers);

            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member LIMIT ? OFFSET ?");
            stm.setInt(1, size);
            stm.setInt(2, (page - 1) * size);
            ResultSet rst2 = stm.executeQuery();

            ArrayList<MemberDTO> members = new ArrayList<>();

            while (rst2.next()) {
                String id = rst2.getString("id");
                String name = rst2.getString("name");
                String address = rst2.getString("address");
                String contact = rst2.getString("contact");
                MemberDTO memberDTO = new MemberDTO(id, name, address, contact);
                members.add(memberDTO);
            }

            response.setContentType("application/json");
            JsonbBuilder.create().toJson(members, response.getWriter());
        } catch (SQLException e) {
//            e.printStackTrace();
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch members");
            throw new RuntimeException(e);
        }
    }

    private void searchMembers(String query, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ?");
            query = "%" + query + "%";
            stm.setString(1, query);
            stm.setString(2, query);
            stm.setString(3, query);
            stm.setString(4, query);
            ResultSet rst = stm.executeQuery();
            ArrayList<MemberDTO> members = new ArrayList<>();

            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                MemberDTO memberDTO = new MemberDTO(id, name, address, contact);
                members.add(memberDTO);
            }

            response.setContentType("application/json");
            JsonbBuilder.create().toJson(members, response.getWriter());
        } catch (SQLException e) {
//            e.printStackTrace();
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch members");
            throw new RuntimeException(e);
        }

    }

    private void searchPaginatedMembers(String query, int size, int page, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stmCount = connection.prepareStatement("SELECT COUNT(id) FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ?");
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ? LIMIT ? OFFSET ?");

            query = "%" + query + "%";
            stmCount.setString(1, query);
            stmCount.setString(2, query);
            stmCount.setString(3, query);
            stmCount.setString(4, query);
            ResultSet rst1 = stmCount.executeQuery();
            rst1.next();
            int totalMembers = rst1.getInt(1);
            response.addIntHeader("X-Total-Count", totalMembers);

            stm.setString(1, query);
            stm.setString(2, query);
            stm.setString(3, query);
            stm.setString(4, query);
            stm.setInt(5, size);
            stm.setInt(6, (page - 1) * size);
            ResultSet rst2 = stm.executeQuery();

            ArrayList<MemberDTO> members = new ArrayList<>();

            while (rst2.next()) {
                String id = rst2.getString("id");
                String name = rst2.getString("name");
                String address = rst2.getString("address");
                String contact = rst2.getString("contact");
                MemberDTO memberDTO = new MemberDTO(id, name, address, contact);
                members.add(memberDTO);
            }

            response.setContentType("application/json");
            JsonbBuilder.create().toJson(members, response.getWriter());
        } catch (SQLException e) {
//            e.printStackTrace();
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch members");
            throw new RuntimeException(e);
        }
    }

    private void getMemberDetails(String memberId, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member WHERE id = ?");
            stm.setString(1, memberId);
            ResultSet rst = stm.executeQuery();

            if (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                MemberDTO memberDTO = new MemberDTO(id, name, address, contact);
                response.setContentType("application/json");
                JsonbBuilder.create().toJson(memberDTO, response.getWriter());
            } else {
                throw new ResponseStatusException(404, "Invalid member id");
            }

        } catch (SQLException e) {
//            e.printStackTrace();
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch the member details");
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

                if (member.getName() == null || !member.getName().matches("[A-Za-z ]+")){
                    throw new JsonbException("Name is empty or invalid");
                } else if (member.getAddress() == null || !member.getAddress().matches("^[A-Za-z0-9| ,.:;#\\/\\\\-]+$")) {
                    throw new JsonbException("Address is empty or invalid");
                } else if (member.getContact() == null || !member.getContact().matches("\\d{3}-\\d{7}")) {
                    throw new JsonbException("Contact number is empty or invalid");
                }

                try(Connection connection = pool.getConnection()) {
                    ConnectionUtil.setConnection(connection);
                    if (BOLogics.createMember(member)){
                        response.setStatus(HttpServletResponse.SC_CREATED);
                        response.setContentType("application/json");
                        JsonbBuilder.create().toJson(member, response.getWriter());
                    }else {
                        throw new SQLException("Something went wrong");
                    }
                } catch (SQLException e) {
//                    e.printStackTrace();
//                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
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
            if (BOLogics.deleteMember(memberId)){
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else{
                throw new ResponseStatusException(400, "Something went wrong");
            }
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

            if (member.getId() == null || !memberId.equalsIgnoreCase(member.getId())){
                throw new JsonbException("Id is empty or invalid");
            }else if (member.getName() == null || !member.getName().matches("[A-Za-z ]+")){
                throw new JsonbException("Name is empty or invalid");
            } else if (member.getAddress() == null || !member.getAddress().matches("^[A-Za-z0-9| ,.:;#\\/\\\\-]+$")) {
                throw new JsonbException("Address is empty or invalid");
            } else if (member.getContact() == null || !member.getContact().matches("\\d{3}-\\d{7}")) {
                throw new JsonbException("Contact number is empty or invalid");
            }

            try(Connection connection = pool.getConnection()) {
               ConnectionUtil.setConnection(connection);
                if(BOLogics.updateMember(member)){
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }else{
//                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Member does not exist");
                    throw new ResponseStatusException(404, "Member does not exist");
                }
            } catch (SQLException e) {
//                e.printStackTrace();
//                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update the member");
                throw new RuntimeException(e);
            }
        }catch(JsonbException e){
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            throw new ResponseStatusException(400, e.getMessage(), e);
        }
    }
}
