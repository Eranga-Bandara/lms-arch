package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.custom.*;
import lk.ijse.dep9.dto.IssueNoteDTO;
import lk.ijse.dep9.service.custom.IssueService;
import lk.ijse.dep9.service.exception.AlreadyIssuedException;
import lk.ijse.dep9.service.exception.LimitExceedException;
import lk.ijse.dep9.service.exception.NotAvailableException;
import lk.ijse.dep9.service.exception.NotFoundException;
import lk.ijse.dep9.service.util.Converter;
import lk.ijse.dep9.util.ConnectionUtil;

public class IssueServiceImpl implements IssueService {

    private final IssueNoteDAO issueNoteDAO;
    private final IssueItemDAO issueItemDAO;
    private final MemberDAO memberDAO;
    private final BookDAO bookDAO;
    private final Converter converter;

    private final QueryDAO queryDAO;

    public IssueServiceImpl() {
        issueNoteDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.ISSUE_NOTE);
        issueItemDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.ISSUE_ITEM);
        memberDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.MEMBER);
        bookDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.BOOK);
        queryDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.QUERY);
        converter = new Converter();
    }


    public void placeNewIssueNote(IssueNoteDTO issueNoteDTO) throws NotFoundException, NotAvailableException, LimitExceedException, AlreadyIssuedException {
//        check member existance
        if(!memberDAO.existsById(issueNoteDTO.getMemberId())) throw new NotFoundException("Member does not exists");
//        check book existance and availability
        for(String isbn : issueNoteDTO.getBooks()){
            int copies = queryDAO.getAvailableCopies(isbn).orElseThrow(() -> new NotFoundException("Book " + isbn + " does not exist"));
            if(copies == 0) throw new NotAvailableException("Book " + isbn + " not available at the moment");

            if(queryDAO.alreadyIssued(isbn, issueNoteDTO.getMemberId())){
                throw new AlreadyIssuedException("Book " + isbn + " has been already issued to the same member");
            }
        }
//        check whether a book in issue note has been already issued to this member

//        check how many books can be issued to this member maximum 3


    }
}
