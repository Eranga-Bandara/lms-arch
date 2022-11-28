package lk.ijse.dep9.api;

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
import lk.ijse.dep9.api.util.HttpServlet2;
import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.exception.ResponseStatusException;
import lk.ijse.dep9.service.BOLogics;
import lk.ijse.dep9.service.ServiceFactory;
import lk.ijse.dep9.service.ServiceTypes;
import lk.ijse.dep9.service.SuperService;
import lk.ijse.dep9.service.custom.BookService;
import lk.ijse.dep9.util.ConnectionUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "BookServlet", value = "/books/*", loadOnStartup = 0)
public class BookServlet extends HttpServlet2 {

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
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid page or size");
                } else {
                    searchPaginatedBooks(query, Integer.parseInt(size), Integer.parseInt(page), response);
                }
            } else if (query != null) {
//                searchBooks(query, response);
            } else if (size != null && page != null) {
                if (!size.matches("\\d+") || !page.matches("\\d+")) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid page or size");
                } else {
//                    loadAllPaginatedBooks(Integer.parseInt(size), Integer.parseInt(page), response);
                }
            } else {
//                loadAllBooks(response);
            }
        } else {
            Matcher matcher = Pattern.compile("^/([0-9][0-9\\\\-]*[0-9])/?$").matcher(request.getPathInfo());
            if (matcher.matches()) {
                getBookDetails(matcher.group(1), response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
            }
        }
    }

    private void searchPaginatedBooks(String query, int size, int page, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {

            ConnectionUtil.setConnection(connection);
            BookService bookService = ServiceFactory.getInstance().getService(ServiceTypes.BOOK);
            List<BookDTO> books = bookService.findBooks(query, size, page);
            response.setIntHeader("X-Total-Count", books.size());
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(books, response.getWriter());
        } catch (SQLException e) {
            throw new RuntimeException(e);
          }
    }

    private void getBookDetails(String isbn, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            ConnectionUtil.setConnection(connection);
            BookService bookService = ServiceFactory.getInstance().getService(ServiceTypes.BOOK);
            BookDTO bookDetails = bookService.getBookDetails(isbn);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(bookDetails);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            saveBook(request, response);
        } else {
            throw new ResponseStatusException(501);
        }
    }

    private void saveBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (request.getContentType() == null || !request.getContentType().startsWith("application/json")) {
                throw new JsonbException("Invalid JSON");
            }

            BookDTO book = JsonbBuilder.create().fromJson(request.getReader(), BookDTO.class);

            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            Set<ConstraintViolation<BookDTO>> violations = validator.validate(book);
            if(!violations.isEmpty()){
                throw new ValidationException(violations.stream().findAny().get().getMessage());
            }

            try (Connection connection = pool.getConnection()) {
                ConnectionUtil.setConnection(connection);
                if (BOLogics.saveBook(book)) {
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    response.setContentType("application/json");
                    JsonbBuilder.create().toJson(book, response.getWriter());
                } else {
                    throw new SQLException("Something went wrong");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (JsonbException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")){
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
            return;
        }

        Matcher matcher = Pattern.compile("^/([0-9][0-9\\\\-]*[0-9])/?$").matcher(request.getPathInfo());
        if (matcher.matches()){
            updateBookDetails(matcher.group(1), request, response);
        }else {
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }

    private void updateBookDetails(String isbn, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try{
            if(request.getContentType() == null || !request.getContentType().startsWith("application/json")){
                throw new JsonbException("Invalid JSON");
            }

            BookDTO book = JsonbBuilder.create().fromJson(request.getReader(), BookDTO.class);

            if (book.getIsbn() == null || !book.getIsbn().equalsIgnoreCase(isbn)) {
                throw new JsonbException("Book isbn is empty or invalid");
            } else if (book.getTitle() == null || !book.getTitle().matches(".+")) {
                throw new JsonbException("Book title is empty or invalid");
            } else if (book.getAuthor() == null || !book.getAuthor().matches("[A-Za-z ]+")) {
                throw new JsonbException("Author is empty or invalid");
            } else if (book.getCopies() == null || book.getCopies() < 1) {
                throw new JsonbException("Copies is empty or invalid");
            }

            try (Connection connection = pool.getConnection()) {
                ConnectionUtil.setConnection(connection);
                if (BOLogics.updateBook(book)) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                   response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book does not exist");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update the book");
            }
        } catch (JsonbException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
