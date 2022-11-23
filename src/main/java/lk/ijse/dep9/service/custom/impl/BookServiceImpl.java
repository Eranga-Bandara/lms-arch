package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.custom.BookDAO;
import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.service.custom.BookService;
import lk.ijse.dep9.util.ConnectionUtil;

import java.util.List;

public class BookServiceImpl implements BookService {

    private final BookDAO bookDAO;

    public BookServiceImpl(BookDAO bookDAO) {
        this.bookDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.BOOK);
    }

    @Override
    public void addNewBook(BookDTO dto) {

    }

    @Override
    public void updateBookDetails(BookDTO dto) {

    }

    @Override
    public void getBookDetails(String isbn) {

    }

    @Override
    public List<BookDTO> findBooks(String query, int size, int page) {
        return null;
    }
}
