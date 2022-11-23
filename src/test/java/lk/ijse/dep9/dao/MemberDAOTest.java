package lk.ijse.dep9.dao;

import com.github.javafaker.Faker;
import lk.ijse.dep9.dao.exception.ConstraintViolationException;
import lk.ijse.dep9.dao.custom.impl.MemberDAOImpl;
import lk.ijse.dep9.entity.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MemberDAOTest {

    private MemberDAOImpl memberDAO;
    private Connection connection;

    @BeforeAll
    static void beforeAll() throws ClassNotFoundException, SQLException, URISyntaxException, IOException {
//        System.out.println("Before All test cases");
        Class.forName("com.mysql.cj.jdbc.Driver");
//        Connection connection = DriverManager.getConnection("jdbc:h2:mem:dep9_lms");
//        List<String> lines = Files.readAllLines(Paths.get(MemberDAOTest.class.getResource("/db.script.sql").toURI()));
//        String dbScriptContent = lines.stream().reduce((previous, current) -> previous + "\n" + current).get();
////        System.out.println(dbScriptContent);
//        Statement stm = connection.createStatement();
//        stm.execute(dbScriptContent);
    }

    //
//    @AfterAll
//    static void afterAll() {
//        System.out.println("After All test cases");
//    }
//
    @BeforeEach
    void setUp() throws SQLException, URISyntaxException, IOException {
//        System.out.println("Before each test case");
        connection = DriverManager.getConnection("jdbc:h2:mem:");  // empty database
        List<String> lines = Files.readAllLines(Paths.get(this.getClass().getResource("/db.script.sql").toURI()));
        String dbScriptContent = lines.stream().reduce((previous, current) -> previous + "\n" + current).get();
        Statement stm = connection.createStatement();
        stm.execute(dbScriptContent);
        this.memberDAO = new MemberDAOImpl(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
//        System.out.println("After each test case");
        connection.close();
    }

    //    @Order(1)
    @Test
    void countMembers() {
        long actualMemberCount = memberDAO.countMembers();
        assertEquals(5, actualMemberCount);
//        System.out.println(this);
    }

    //    @Order(1)
    @Test
    void findAllMembers() {
        List<Member> members = memberDAO.findAllMembers();
        assertEquals(5, members.size());
        members.forEach(member -> {
            assertNotNull(member);
            assertNotNull(member.getId());
            assertNotNull(member.getName());
            assertNotNull(member.getAddress());
            assertNotNull(member.getContact());
            System.out.println(member);
//        System.out.println(this);
        });
    }

    //    @Order(2)
    @Test
    void saveMember() {

        Faker faker = new Faker();

        Member expectedMember = new Member(UUID.randomUUID().toString(), faker.name().fullName(), faker.address().fullAddress(), faker.regexify("0\\d{2}-\\d{7}"));
        System.out.println(expectedMember);
        long expectedCount = memberDAO.countMembers() + 1;
        Member actualMember = memberDAO.saveMember(expectedMember);
        assertEquals(expectedMember, actualMember);
        assertEquals(expectedCount, memberDAO.countMembers());
//        System.out.println(this);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/member-test-data.csv")
    void deleteMemberById(String memberId, boolean expectedResult) {
        try {
            memberDAO.deleteMemberById(memberId);
        } catch (ConstraintViolationException e) {
            System.out.println("Failed to delete " + memberId);
        }
    }

    @ParameterizedTest
//    @ValueSource(strings = {"86f4c85d-5803-11ed-be5d-d89d67cd0f57",
//    "86f4cbd7-5803-11ed-be5d-d89d67cd0f57", "86f4cd56-5803-11ed-be5d-d89d67cd0f57"})
//    @Test
    @CsvFileSource(resources = "/member-test-data.csv")
    void existsMemberById(String memberId, boolean expectedResult) {
//        String memberId = "86f4c85d-5803-11ed-be5d-d89d67cd0f57";
        boolean actualValue = memberDAO.existsMemberById(memberId);
        assertTrue(actualValue == expectedResult);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/member-test-data.csv")
    void findMemberById(String memberId, boolean expectedResult) {
        Optional<Member> optMember = memberDAO.findMemberById(memberId);
        assertTrue(optMember.isPresent() == expectedResult);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/member-test-data.csv")
    void updateMember(String memberId, boolean exist) {
        Optional<Member> optMember = memberDAO.findMemberById(memberId);
        Faker faker = new Faker();

        optMember.ifPresent(member -> {
            member.setName(faker.name().fullName());
            member.setAddress(faker.address().fullAddress());
            member.setContact(faker.regexify("0\\d{2}-\\d{7}"));
            Member updatedMember = memberDAO.updateMember(member);
            assertEquals(member, updatedMember);
        });
    }

    @Test
    void findMembersByQuery() {
    }

    @Test
    void testFindMembersByQuery() {
    }

    @Test
    void testFindAllMembers() {
    }
}
