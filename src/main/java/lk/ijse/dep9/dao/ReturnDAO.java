package lk.ijse.dep9.dao;

import java.sql.Connection;

public class ReturnDAO {

    private Connection connection;

    public ReturnDAO(Connection connection) {
        this.connection = connection;
    }
}
