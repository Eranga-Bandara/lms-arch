package lk.ijse.dep9.dao;

import java.sql.Connection;

public class IssueNoteDAO {

    private Connection connection;

    public IssueNoteDAO(Connection connection) {
        this.connection = connection;
    }
}
