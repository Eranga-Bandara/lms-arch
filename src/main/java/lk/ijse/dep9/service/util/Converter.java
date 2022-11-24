package lk.ijse.dep9.service.util;

import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.entity.Book;
import org.modelmapper.ModelMapper;

public class Converter {

    private ModelMapper mapper = new ModelMapper();

    public BookDTO fromBookEntity(Book bookEntity){
//        return new BookDTO(bookEntity.getIsbn(),
//                bookEntity.getTitle(),
//                bookEntity.getAuthor(),
//                bookEntity.getCopies());
        return mapper.map(bookEntity, BookDTO.class);
    }

    public Book toBookEntity(BookDTO bookDTO){
//        return new Book(bookDTO.getIsbn(),
//                bookDTO.getTitle(),
//                bookDTO.getAuthor(),
//                bookDTO.getCopies());
        return mapper.map(bookDTO, Book.class);
    }

}
