package lk.ijse.dep9.service.custom;

import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.service.SuperService;

import java.util.List;

public interface MemberService extends SuperService {

    void signupMember(MemberDTO member);
    void updateMemberDetails(MemberDTO member);
    void removeMemberAccount(String memberId);
    MemberDTO getMemberDetails(String memberId);
    List<MemberDTO> findMembers(String query, int size, int page);

}
