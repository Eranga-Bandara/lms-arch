package lk.ijse.dep9.dao.custom;

import lk.ijse.dep9.dao.CrudDAO;
import lk.ijse.dep9.entity.Member;

import java.util.List;

public interface MemberDAO extends CrudDAO<Member, String> {

//    public long countMembers() ;
//
//    public void deleteMemberById(String id) throws ConstraintViolationException ;
//
//    public boolean existsMemberById(String id) ;
//
//    public List<Member> findAllMembers();
//
//    public Optional<Member> findMemberById(String id) ;
//
//    public Member saveMember(Member member) ;
//
//    public Member updateMember(Member member) ;


//    default boolean existsByContact (String contact){
//        throw new RuntimeException("Not Implemented yet");
//    }

    boolean existsByContact (String contact);

    List<Member> findMembersByQuery(String query);

    List<Member> findMembersByQuery(String query, int size, int page);
    List<Member> findAllMembers(int size, int page);

}
