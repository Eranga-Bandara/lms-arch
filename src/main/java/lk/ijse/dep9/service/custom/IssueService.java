package lk.ijse.dep9.service.custom;

import lk.ijse.dep9.dto.IssueNoteDTO;
import lk.ijse.dep9.service.SuperService;

public interface IssueService extends SuperService {

    void placeNewIssueNote(IssueNoteDTO issueNoteDTO);

}
