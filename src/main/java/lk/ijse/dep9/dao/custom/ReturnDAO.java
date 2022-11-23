package lk.ijse.dep9.dao;

import lk.ijse.dep9.entity.Return;
import lk.ijse.dep9.entity.ReturnPK;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ReturnDAO {

    public long countReturns() ;

    public void deleteReturnByPK(ReturnPK returnPK) ;

    public boolean existsReturnByPK(ReturnPK returnPK);

    public List<Return> findAllReturns();

    public Optional<Return> findReturnByPK(ReturnPK issueItemPK);

    public Return saveReturn(Return returnItem);

}
