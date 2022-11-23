package lk.ijse.dep9.dao;

import lk.ijse.dep9.dao.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.Member;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface MemberDAO {

    public long countMembers() ;

    public void deleteMemberById(String id) throws ConstraintViolationException ;

    public boolean existsMemberById(String id) ;

    public List<Member> findAllMembers();

    public Optional<Member> findMemberById(String id) ;

    public Member saveMember(Member member) ;

    public Member updateMember(Member member) ;

    public List<Member> findMembersByQuery(String query);

    public List<Member> findMembersByQuery(String query, int page, int size) ;

    public List<Member> findAllMembers(int page, int size);

}
