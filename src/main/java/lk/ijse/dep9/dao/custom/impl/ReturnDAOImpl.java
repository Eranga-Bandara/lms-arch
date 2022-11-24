package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.custom.ReturnDAO;
import lk.ijse.dep9.dao.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.Return;
import lk.ijse.dep9.entity.ReturnPK;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReturnDAOImpl implements ReturnDAO {

    private final Connection connection;

    public ReturnDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long count() {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT COUNT(isbn) FROM `return`");
            ResultSet rst = stm.executeQuery();
            rst.next();
            return rst.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(ReturnPK returnPK) throws ConstraintViolationException{
        try {
            PreparedStatement stm = connection.prepareStatement("DELETE FROM `return` WHERE isbn = ? AND issue_id = ?");
            stm.setString(1, returnPK.getIsbn());
            stm.setInt(2, returnPK.getIssueId());
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(ReturnPK returnPK){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM `return` WHERE isbn = ? AND issue_id = ?");
            stm.setString(1, returnPK.getIsbn());
            stm.setInt(2, returnPK.getIssueId());
            return stm.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Return> findAll(){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM `return`");
            ResultSet rst = stm.executeQuery();
            List<Return> returnList = new ArrayList<>();
            while(rst.next()){
                Date date = rst.getDate("date");
                String isbn = rst.getString("isbn");
                int issueId = rst.getInt("issue_id");
                returnList.add(new Return(date, issueId, isbn));
            }
            return returnList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Return> findById(ReturnPK returnPK){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM `return` WHERE isbn = ? AND issue_id = ?");
            stm.setString(1, returnPK.getIsbn());
            stm.setInt(2, returnPK.getIssueId());
            ResultSet rst = stm.executeQuery();
            List<Return> returnList = new ArrayList<>();
            if(rst.next()){
                Date date = rst.getDate("date");
                String isbn = rst.getString("isbn");
                int issueId = rst.getInt("issue_id");
                return Optional.of(new Return(date, issueId, isbn));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Return save(Return returnItem){
        try {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO `return` (date, issue_id, isbn) VALUES (?,?,?)");
            stm.setDate(1, returnItem.getDate());
            stm.setInt(2, returnItem.getReturnPK().getIssueId());
            stm.setString(3, returnItem.getReturnPK().getIsbn());
            if(stm.executeUpdate() == 1){
                return returnItem;
            }else{
                throw new SQLException("Failed to save the return item");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Return update(Return returnItem) throws ConstraintViolationException {
        try {
            PreparedStatement stm = connection.prepareStatement("UPDATE `return` SET date = ? WHERE issue_id = ? AND isbn = ?");
            stm.setDate(1, returnItem.getDate());
            stm.setInt(2, returnItem.getReturnPK().getIssueId());
            stm.setString(3, returnItem.getReturnPK().getIsbn());
            if(stm.executeUpdate() == 1){
                return returnItem;
            }else{
                throw new SQLException("Failed to update the return item");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
