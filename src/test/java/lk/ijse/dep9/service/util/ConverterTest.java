package lk.ijse.dep9.service.util;

import com.github.javafaker.Faker;
import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.entity.Book;
import lk.ijse.dep9.entity.Member;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConverterTest {

    private Converter converter = new Converter();

    @Test
    void fromBook() {
        Faker faker = new Faker();
        Book bookEntity = new Book(faker.code().isbn10(), faker.book().title(), faker.book().author(), faker.number().numberBetween(1,3));
        BookDTO bookDTO = converter.fromBook(bookEntity);
        assertEquals(bookEntity.getIsbn(), bookDTO.getIsbn());
        assertEquals(bookEntity.getTitle(), bookDTO.getTitle());
        assertEquals(bookEntity.getAuthor(), bookDTO.getAuthor());
        assertEquals(bookEntity.getCopies(), bookDTO.getCopies());
    }

    @Test
    void toBook() {
        Faker faker = new Faker();
        BookDTO bookDTO = new BookDTO(faker.code().isbn10(), faker.book().title(), faker.book().author(), faker.number().numberBetween(1,3));
        Book book = converter.toBook(bookDTO);
        assertEquals(bookDTO.getIsbn(), bookDTO.getIsbn());
        assertEquals(bookDTO.getTitle(), bookDTO.getTitle());
        assertEquals(bookDTO.getAuthor(), bookDTO.getAuthor());
        assertEquals(bookDTO.getCopies(), bookDTO.getCopies());
    }

    @Test
    void fromMember() {
        Faker faker = new Faker();
        Member member = new Member(UUID.randomUUID().toString(), faker.name().fullName(), faker.address().fullAddress(), faker.regexify("0\\d{2}-\\d{7}"));
        MemberDTO memberDTO = converter.fromMember(member);
        assertEquals(memberDTO.getId(), member.getId());
        assertEquals(memberDTO.getName(), member.getName());
        assertEquals(memberDTO.getAddress(), member.getAddress());
        assertEquals(memberDTO.getContact(), member.getContact());

    }

    @Test
    void toMember() {
        Faker faker = new Faker();
        MemberDTO memberDTO = new MemberDTO(UUID.randomUUID().toString(), faker.name().fullName(), faker.address().fullAddress(), faker.regexify("0\\d{2}-\\d{7}"));
        Member member = converter.toMember(memberDTO);
        assertEquals(memberDTO.getId(), member.getId());
        assertEquals(memberDTO.getName(), member.getName());
        assertEquals(memberDTO.getAddress(), member.getAddress());
        assertEquals(memberDTO.getContact(), member.getContact());
    }
}