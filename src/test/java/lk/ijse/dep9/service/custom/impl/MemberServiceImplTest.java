package lk.ijse.dep9.service.custom.impl;

import com.github.javafaker.Faker;
import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.SuperDAO;
import lk.ijse.dep9.dao.custom.MemberDAO;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.entity.Member;
import lk.ijse.dep9.service.ServiceFactory;
import lk.ijse.dep9.service.ServiceTypes;
import lk.ijse.dep9.service.custom.MemberService;
import lk.ijse.dep9.service.exception.NotFoundException;
import lk.ijse.dep9.service.util.Converter;
import lk.ijse.dep9.util.ConnectionUtil;
import lombok.Cleanup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MemberServiceImplTest {

    private MemberService memberService;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        connection = DriverManager.getConnection("jdbc:h2:mem:");
//     Lombok Cleanup fro try insteadof try with resource
        @Cleanup BufferedReader bfr = new BufferedReader(new InputStreamReader(getClass().
                getResourceAsStream("db.script.sql")));
        String dbScript = bfr.lines().reduce((previous, current) -> previous + current).get();
        connection.createStatement().execute(dbScript);
        ConnectionUtil.setConnection(connection);  // assosiation connection with the thread
        memberService = ServiceFactory.getInstance().getService(ServiceTypes.MEMBER);
    }


    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void signupMember() {
        Faker faker = new Faker();
        MemberDTO member1 = new MemberDTO(UUID.randomUUID().toString(), faker.name().fullName(), faker.address().fullAddress(), "078-1234567");
        MemberDTO member2 = new MemberDTO(UUID.randomUUID().toString(), faker.name().fullName(), faker.address().fullAddress(), faker.regexify("0\\d{2}-\\d{7}"));

//        assertThrows()
    }

    @Test
    void updateMemberDetails() {
        Faker faker = new Faker();
        MemberDAO memberDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.MEMBER);
        MemberDTO member = new Converter().fromMember(memberDAO.findById("104ccff3-c584-4782-a582-8a06479b46f6").get());
        member.setName(faker.name().fullName());
        member.setAddress(faker.address().fullAddress());
        member.setContact(faker.regexify("0\\d{2}-\\d{7}"));

        MemberDTO member2 = new MemberDTO(UUID.randomUUID().toString(), faker.name().fullName(), faker.address().fullAddress(), "078-1234567");
        memberService.updateMemberDetails(member);

        MemberDTO updatedMember = new Converter().fromMember(memberDAO.findById(member.getId()).get());

//        assertEquals(member.getName(), updatedMember.getName());
//        assertEquals(member.getAddress(), updatedMember.getAddress());
//        assertEquals(member.getContact(), updatedMember.getContact());

        assertEquals(member, member2);  // since in same type this way is possible other than above
        assertThrows(NotFoundException.class, () -> memberService.updateMemberDetails(member2));
    }

    @Test
    void removeMemberAccount() {
    }

    @Test
    void getMemberDetails() {
    }

    @Test
    void findMembers() {
    }
}