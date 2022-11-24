package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.custom.MemberDAO;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.entity.Member;
import lk.ijse.dep9.service.custom.MemberService;
import lk.ijse.dep9.service.exception.DuplicateException;
import lk.ijse.dep9.service.exception.NotFoundException;
import lk.ijse.dep9.service.util.Converter;
import lk.ijse.dep9.util.ConnectionUtil;

import java.util.List;
import java.util.stream.Collectors;

public class MemberServiceImpl implements MemberService {

    private final MemberDAO memberDAO;
    private final Converter converter;

    public MemberServiceImpl() {
        this.memberDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.MEMBER);
        this.converter = new Converter();
    }

    @Override
    public void signupMember(MemberDTO memberDTO) throws DuplicateException {
        if (memberDAO.existsById(memberDTO.getId())){
            throw new DuplicateException("Member with this id already exists");
        }

        memberDAO.save(converter.toMember(memberDTO));
    }

    @Override
    public void updateMemberDetails(MemberDTO memberDTO) throws NotFoundException {

        if(!memberDAO.existsById(memberDTO.getId())){
            throw new NotFoundException("Member does not exists");
        }

        memberDAO.update(converter.toMember(memberDTO));

    }

    @Override
    public void removeMemberAccount(String memberId) {

    }

    @Override
    public MemberDTO getMemberDetails(String memberId) throws NotFoundException{

        return memberDAO.findById(memberId).map(converter::fromMember)
                .orElseThrow(() -> new NotFoundException("Member does not exists"));

    }

    @Override
    public List<MemberDTO> findMembers(String query, int size, int page) {
        List<Member> memberEntityList = memberDAO.findMembersByQuery(query, size, page);

        return memberEntityList.stream().map(converter::fromMember).collect(Collectors.toList());
    }
}
