package lk.ijse.dep9.dao;

import lk.ijse.dep9.dao.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.IssueItem;
import lk.ijse.dep9.entity.IssueItemPK;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface IssueItemDAO {

    public long countIssueItems();

    public void deleteIssueItemByPK(IssueItemPK issueItemPK) throws ConstraintViolationException ;

    public boolean existsIssueItemByPK(IssueItemPK issueItemPK);

    public List<IssueItem> findAllIssueItems();

    public Optional<IssueItem> findIssueItemByPK(IssueItemPK issueItemPK);

    public IssueItem saveIssueItem(IssueItem issueItem);

}
