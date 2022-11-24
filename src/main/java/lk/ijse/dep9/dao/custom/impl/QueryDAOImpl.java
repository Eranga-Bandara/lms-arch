package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.custom.QueryDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class QueryDAOImpl implements QueryDAO {

    private final Connection connection;

    public QueryDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Integer> getAvailableCopies(String isbn) {
        try{
            PreparedStatement stm = connection.prepareStatement(
                    "SELECT b.copies - COUNT(ii.isbn) + COUNT(r.isbn) AS available_copies\n" +
                    "FROM issue_item ii\n" +
                    "LEFT OUTER JOIN `return` r on ii.issue_id = r.issue_id and ii.isbn = r.isbn\n" +
                    "LEFT OUTER JOIN book b on ii.isbn = b.isbn WHERE b.isbn = ?\n" +
                    "GROUP BY b.isbn;");
            stm.setString(1, isbn);
            ResultSet rst = stm.executeQuery();
            if (!rst.next()) return Optional.empty();
            return Optional.of(rst.getInt(1));
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
