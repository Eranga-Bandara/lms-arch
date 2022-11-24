package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.custom.BookDAO;
import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.entity.Book;
import lk.ijse.dep9.service.custom.BookService;
import lk.ijse.dep9.service.exception.DuplicateException;
import lk.ijse.dep9.service.exception.NotFoundException;
import lk.ijse.dep9.service.util.Converter;
import lk.ijse.dep9.util.ConnectionUtil;

import java.util.List;
import java.util.stream.Collectors;

public class BookServiceImpl implements BookService {

    private final BookDAO bookDAO;
    private final Converter converter;

    public BookServiceImpl() {
        this.bookDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.BOOK);
        converter = new Converter();
    }

    @Override
    public void addNewBook(BookDTO dto) throws DuplicateException {

        if(bookDAO.existsById(dto.getIsbn())){
            throw new DuplicateException("Book with this isbn aleady exixts");
        }

//        Book bookEntity = new Book(dto.getIsbn(), dto.getTitle(), dto.getAuthor(), dto.getCopies());
        bookDAO.save(converter.toBook(dto));

    }

    @Override
    public void updateBookDetails(BookDTO dto) throws NotFoundException {
        if(!bookDAO.existsById(dto.getIsbn())){
            throw new NotFoundException("Book does not exist");
        }

//        Book bookEntity = new Book(dto.getIsbn(), dto.getTitle(), dto.getAuthor(), dto.getCopies());
        bookDAO.update(converter.toBook(dto));
    }

    @Override
    public BookDTO getBookDetails(String isbn) throws NotFoundException {
//        return bookDAO.findById(isbn).map(e -> new BookDTO(e.getIsbn(), e.getTitle(), e.getAuthor(), e.getCopies()))
//                .orElseThrow(() -> new NotFoundException("Book does not exist"));
        return bookDAO.findById(isbn).map(converter::fromBook)
                .orElseThrow(() -> new NotFoundException("Book does not exist"));
    }

    @Override
    public List<BookDTO> findBooks(String query, int size, int page) {

        List<Book> bookEntityList = bookDAO.findBooksByQuery(query, size, page);
//        return bookEntityList.stream()
//                .map(e -> new BookDTO(e.getIsbn(), e.getTitle(), e.getAuthor(), e.getCopies()))
//                .collect(Collectors.toList());
        return bookEntityList.stream()
                .map(converter::fromBook)
                .collect(Collectors.toList());

    }
}
