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
                    "RIGHT OUTER JOIN book b on ii.isbn = b.isbn WHERE b.isbn = ?\n" +
                    "GROUP BY b.isbn;");
            stm.setString(1, isbn);
            ResultSet rst = stm.executeQuery();
            if (!rst.next()) return Optional.empty();
            return Optional.of(rst.getInt(1));
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean alreadyIssued(String isbn, String memberId) {
        try{
            PreparedStatement stm = connection.prepareStatement("SELECT *, b.title FROM issue_item ii\n" +
            "         INNER JOIN `return` r ON NOT (ii.issue_id = r.issue_id and ii.isbn = r.isbn)\n" +
                    "         INNER JOIN book b on ii.isbn = b.isbn\n" +
                    "         INNER JOIN issue_note `in` on ii.issue_id = `in`.id\n" +
                    "WHERE `in`.member_id = ? AND b.isbn = ?");
            stm.setString(1, memberId);
            stm.setString(2, isbn);
            return stm.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Integer> availableBookLimit(String memberId) {
        try{
            PreparedStatement stm = connection.prepareStatement("SELECT m.name, 3 - COUNT(r.issue_id) AS available FROM issue_note\n" +
                    "    INNER JOIN issue_item ii on issue_note.id = ii.issue_id\n" +
                    "    INNER JOIN `return` r on not(ii.issue_id = r.issue_id and ii.isbn = r.isbn)\n" +
                    "    RIGHT OUTER JOIN member m on issue_note.member_id = m.id\n" +
                    "    WHERE m.id = ? \n" +
                    "    GROUP BY m.id");
            stm.setString(1, memberId);
            ResultSet rst = stm.executeQuery();
            if(!rst.next()) return Optional.empty();
            return Optional.of(rst.getInt("available"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
