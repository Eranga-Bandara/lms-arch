package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.custom.*;
import lk.ijse.dep9.dto.IssueNoteDTO;
import lk.ijse.dep9.entity.IssueItem;
import lk.ijse.dep9.entity.IssueNote;
import lk.ijse.dep9.service.custom.IssueService;
import lk.ijse.dep9.service.exception.AlreadyIssuedException;
import lk.ijse.dep9.service.exception.LimitExceedException;
import lk.ijse.dep9.service.exception.NotAvailableException;
import lk.ijse.dep9.service.exception.NotFoundException;
import lk.ijse.dep9.service.util.Converter;
import lk.ijse.dep9.service.util.Executor;
import lk.ijse.dep9.util.ConnectionUtil;

import java.util.List;

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
//        check member existence
        if(!memberDAO.existsById(issueNoteDTO.getMemberId())) throw new NotFoundException("Member does not exists");
//        check book existence and availability
        for(String isbn : issueNoteDTO.getBooks()){
            int availableCopies = queryDAO.getAvailableCopies(isbn).orElseThrow(() -> new NotFoundException("Book " + isbn + " does not exist"));
            if(availableCopies == 0) throw new NotAvailableException("Book " + isbn + " not available at the moment");

//        check whether a book in issue note has been already issued to this member
            if(queryDAO.alreadyIssued(isbn, issueNoteDTO.getMemberId())){
                throw new AlreadyIssuedException("Book " + isbn + " has been already issued to the same member");
            }
        }

//        check how many books can be issued to this member maximum 3
        Integer availableBookLimit = queryDAO.availableBookLimit(issueNoteDTO.getMemberId()).get();
        if(availableBookLimit < issueNoteDTO.getBooks().size()) throw new LimitExceedException("Member's book limit has been exceeded");


//        Transaction in Try catch hell
        try{
            ConnectionUtil.getConnection().setAutoCommit(false);

            IssueNote issueNote = converter.toIssueNote(issueNoteDTO);
            List<IssueItem> issueItemList = converter.toIssueItemList(issueNoteDTO);

            issueNoteDAO.save(issueNote);
            issueItemList.forEach(issueItemDAO::save);

            ConnectionUtil.getConnection().commit();
        }catch (Throwable t){
//            try {
//                ConnectionUtil.getConnection().rollback();
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
            Executor.execute(ConnectionUtil.getConnection()::rollback);
            throw new RuntimeException(t);
        }finally {
//            try {
//                ConnectionUtil.getConnection().setAutoCommit(true);
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
            Executor.execute(() -> ConnectionUtil.getConnection().setAutoCommit(true));
        }
    }
}
