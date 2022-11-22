package lk.ijse.dep9.dao;

import lk.ijse.dep9.entity.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberDAO {

    private Connection connection;

    public MemberDAO(Connection connection) {
        this.connection = connection;
    }

    public long countMembers() {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT COUNT(id) FROM member");
            ResultSet rst = stm.executeQuery();
            rst.next();
            return rst.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMemberById(String id) {
        try {
            PreparedStatement stm = connection.prepareStatement("DELETE FROM member WHERE id = ?");
            stm.setString(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsMemberById(String id) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT id FROM member WHERE id = ?");
            stm.setString(1, id);
            return stm.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Member> findAllMembers() {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member");
            ResultSet rst = stm.executeQuery();
            List<Member> memberList = new ArrayList<>();
            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                memberList.add(new Member(id, name, address, contact));
            }
            return memberList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Member> findMemberById(String id) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member WHERE id = ?");
            stm.setString(1, id);
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                return Optional.of(new Member(id, name, address, contact));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Member saveMember(Member member) {
        try {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO member (id, name, address, contact) VALUES (?, ?, ?, ?)");
            stm.setString(1, member.getId());
            stm.setString(2, member.getName());
            stm.setString(3, member.getAddress());
            stm.setString(3, member.getContact());
            if (stm.executeUpdate() == 1) {
                return member;
            } else {
                throw new SQLException("Failed to save the memner");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Member updateMember(Member member) {
        try {
            PreparedStatement stm = connection.prepareStatement("UPDATE member SET name = ?, address = ?, contact = ? WHERE id = ?");
            stm.setString(1, member.getName());
            stm.setString(2, member.getAddress());
            stm.setString(3, member.getContact());
            stm.setString(4, member.getId());
            if (stm.executeUpdate() == 1) {
                return member;
            } else {
                throw new SQLException("Failed to update the member");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Member> findMembersByQuery(String query) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ?");
            query = "%" + query + "%";
            stm.setString(1, query);
            stm.setString(2, query);
            stm.setString(3, query);
            stm.setString(4, query);
            ResultSet rst = stm.executeQuery();
            List<Member> memberList = new ArrayList<>();
            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                memberList.add(new Member(id, name, address, contact));
            }
            return memberList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Member> findMembersByQuery(String query, int page, int size) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ? LIMIT ? OFFSET ?");
            query = "%" + query + "%";
            stm.setString(1, query);
            stm.setString(2, query);
            stm.setString(3, query);
            stm.setString(4, query);
            stm.setInt(5, page);
            stm.setInt(6, (page - 1) * size);
            ResultSet rst = stm.executeQuery();
            List<Member> memberList = new ArrayList<>();
            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                memberList.add(new Member(id, name, address, contact));
            }
            return memberList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Member> findAllMembers(int page, int size){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member LIMIT ? OFFSET ?");
            stm.setInt(1, page);
            stm.setInt(2, (page - 1) * size);
            ResultSet rst = stm.executeQuery();
            List<Member> memberList = new ArrayList<>();
            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                memberList.add(new Member(id, name, address, contact));
            }
            return memberList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
