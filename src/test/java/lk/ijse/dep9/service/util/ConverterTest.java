package lk.ijse.dep9.service.util;

import com.github.javafaker.Faker;
import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.entity.Book;
import org.junit.jupiter.api.Test;

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
}