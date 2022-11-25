package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.custom.MemberDAO;
import lk.ijse.dep9.dao.custom.QueryDAO;
import lk.ijse.dep9.dao.custom.ReturnDAO;
import lk.ijse.dep9.dto.ReturnDTO;
import lk.ijse.dep9.entity.ReturnPK;
import lk.ijse.dep9.service.custom.ReturnService;
import lk.ijse.dep9.service.exception.AlreadyReturnedException;
import lk.ijse.dep9.service.exception.NotFoundException;
import lk.ijse.dep9.util.ConnectionUtil;

import java.util.HashSet;

public class ReturnServiceImpl implements ReturnService {

    private ReturnDAO returnDAO;
    private MemberDAO memberDAO;
    private QueryDAO queryDAO;

    public ReturnServiceImpl() {
        queryDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.QUERY);
    }

    @Override
    public void updateReturnStatus(ReturnDTO returnDTO) throws NotFoundException {

        var returnItems = new HashSet<>(returnDTO.getReturnItems());
        returnItems.forEach(
                returnItem -> {
                    if (!queryDAO.isValidIssueItem(returnDTO.getMemberId(),
                            returnItem.getIssueNoteId(), returnItem.getIsbn()))
                        throw new NotFoundException("Invalid return");
                    if(returnDAO
                            .existsById(new ReturnPK(returnItem.getIssueNoteId(), returnItem.getIsbn()))){
                        throw new AlreadyReturnedException("Item has been already returned");
                    }
                });
        ConnectionUtil.
    }
}
