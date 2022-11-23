package lk.ijse.dep9.service.custom;

import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.service.SuperService;

import java.util.List;

public interface BookService extends SuperService {
    void addNewBook(BookDTO dto);
    void updateBookDetails(BookDTO dto);
    void getBookDetails(String isbn);
    List<BookDTO> findBooks(String query, int size, int page);
}
