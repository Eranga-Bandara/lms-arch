package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.custom.BookDAO;
import lk.ijse.dep9.dao.custom.IssueItemDAO;
import lk.ijse.dep9.dao.custom.IssueNoteDAO;
import lk.ijse.dep9.dao.custom.MemberDAO;
import lk.ijse.dep9.dto.IssueNoteDTO;
import lk.ijse.dep9.service.custom.IssueService;
import lk.ijse.dep9.service.exception.AlreadyIssuedException;
import lk.ijse.dep9.service.exception.LimitExceedException;
import lk.ijse.dep9.service.exception.NotAvailableException;
import lk.ijse.dep9.service.exception.NotFoundException;
import lk.ijse.dep9.service.util.Converter;
import lk.ijse.dep9.util.ConnectionUtil;

public class IssueServiceImpl implements IssueService {

    private IssueNoteDAO issueNoteDAO;
    private IssueItemDAO issueItemDAO;
    private MemberDAO memberDAO;
    private BookDAO bookDAO;
    private Converter converter;

    public IssueServiceImpl(IssueNoteDTO issueNoteDTO) {
        issueNoteDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.ISSUE_NOTE);
        issueItemDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.ISSUE_ITEM);
        memberDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.MEMBER);
        bookDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.BOOK);
        converter = new Converter();
    }

    public IssueServiceImpl() {

    }

    public void placeNewIssueNote(IssueNoteDTO issueNoteDTO) throws NotFoundException, NotAvailableException, LimitExceedException, AlreadyIssuedException {
//        check member existance
        if(!memberDAO.existsById(issueNoteDTO.getMemberId())) throw new NotFoundException("Member does not exists");
//        check book existance and availability

//        check how many books can be issued to this member maximum 3
//        check whether a book in issue note has been already issued to this member


    }
}
