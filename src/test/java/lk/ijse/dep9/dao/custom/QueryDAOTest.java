package lk.ijse.dep9.dao.custom;

import com.github.javafaker.Faker;
import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.util.ConnectionUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class QueryDAOTest {

    private QueryDAO queryDAO;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, URISyntaxException, IOException {
        connection = DriverManager.getConnection("jdbc:h2:mem:");
        String dbScript = Files.readString(Paths.get(getClass().getResource("/db.script.sql").toURI()));
        connection.createStatement().execute(dbScript);
        ConnectionUtil.setConnection(connection);
        queryDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.QUERY);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void getAvailableCopies() {
        Faker faker = new Faker();
        String isbn1 = faker.code().isbn10();
        String isbn2 = "1234-7891";
        String isbn3 = "4567-1234";

        assertFalse(queryDAO.getAvailableCopies(isbn1).isPresent());
        assertEquals(2, queryDAO.getAvailableCopies(isbn2).get());
        assertEquals(1, queryDAO.getAvailableCopies(isbn3).get());
    }
}