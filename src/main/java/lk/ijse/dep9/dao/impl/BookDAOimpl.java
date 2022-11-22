package lk.ijse.dep9.dao;

import lk.ijse.dep9.dao.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class BookDAO {

    private Connection connection;

    public BookDAO(Connection connection) {
        this.connection = connection;
    }

    public long countBooks(){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT COUNT(isbn) FROM book");
            ResultSet rst = stm.executeQuery();
            rst.next();
            return rst.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteBookByISBN(String isbn) throws ConstraintViolationException {
        try {
            PreparedStatement stm = connection.prepareStatement("DELETE FROM book WHERE isbn = ?");
            stm.setString(1,isbn);
            stm.executeUpdate();
        } catch (SQLException e) {
            if(existsBookByISBN(isbn)) throw new ConstraintViolationException("Book still exists within other tables", e);
            throw new RuntimeException(e);
        }
    }

    public boolean existsBookByISBN(String isbn){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT isbn FROM book WHERE isbn = ?");
            stm.setString(1, isbn);
            return stm.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Book> findAllBooks(){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM book");
            ResultSet rst = stm.executeQuery();
            List<Book> bookList = new ArrayList<>();
            while(rst.next()){
                String isbn = rst.getString("isbn");
                String title = rst.getString("title");
                String author = rst.getString("author");
                int copies = rst.getInt("copies");
                bookList.add(new Book(isbn, title, author, copies));
            }
            return bookList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Book> findBookByISBN(String isbn){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM book WHERE isbn = ?");
            stm.setString(1, "isbn");
            ResultSet rst = stm.executeQuery();
            if(rst.next()){
                String title = rst.getString("title");
                String author = rst.getString("author");
                int copies = rst.getInt("copies");
                return Optional.of(new Book(isbn, title, author, copies));
            }else{
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Book saveBook(Book book){
        try {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO book (isbn, title, author, copies) VALUES (?, ?, ?, ?)");
            stm.setString(1, book.getIsbn());
            stm.setString(2, book.getTitle());
            stm.setString(3, book.getAuthor());
            stm.setInt(4, book.getCopies());
            if(stm.executeUpdate() == 1){
                return book;
            }else{
                throw new SQLException("Failed to save the book");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Book updateBook(Book book){
        try {
            PreparedStatement stm = connection.prepareStatement("UPDATE book SET title = ?, author = ?, copies = ? WHERE isbn = ?");
            stm.setString(1, book.getTitle());
            stm.setString(2, book.getAuthor());
            stm.setInt(3, book.getCopies());
            stm.setString(4, book.getIsbn());
            if(stm.executeUpdate() == 1){
                return book;
            }else{
                throw new SQLException("Failed to update the book");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Book> findBooksByQuery(String query){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM book WHERE isbn LIKE ? OR title LIKE ? OR author LIKE ? OR copies LIKE ?");
            query = "%" + query + "%";
            stm.setString(1, query);
            stm.setString(2, query);
            stm.setString(3, query);
            stm.setString(4, query);
            ResultSet rst = stm.executeQuery();
            List<Book> bookList  = new ArrayList<>();
            while(rst.next()){
                String isbn = rst.getString("isbn");
                String title = rst.getString("title");
                String author = rst.getString("author");
                int copies = rst.getInt("copies");
                bookList.add(new Book(isbn, title, author, copies));
            }
            return bookList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Book> findBooksByQuery(String query, int page, int size){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM book WHERE isbn LIKE ? OR title LIKE ? OR author LIKE ? OR copies LIKE ? LIMIT ? OFFSET ?");
            query = "%" + query + "%";
            stm.setString(1, query);
            stm.setString(2, query);
            stm.setString(3, query);
            stm.setString(4, query);
            stm.setInt(5, page);
            stm.setInt(6, (page - 1) * size);
            ResultSet rst = stm.executeQuery();
            List<Book> bookList  = new ArrayList<>();
            while(rst.next()){
                String isbn = rst.getString("isbn");
                String title = rst.getString("title");
                String author = rst.getString("author");
                int copies = rst.getInt("copies");
                bookList.add(new Book(isbn, title, author, copies));
            }
            return bookList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Book> findAllBooks(int page, int size){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM book LIMIT ? OFFSET ?");
            stm.setInt(1, page);
            stm.setInt(2, (page - 1) * size);
            ResultSet rst = stm.executeQuery();
            List<Book> bookList = new ArrayList<>();
            while(rst.next()){
                String isbn = rst.getString("isbn");
                String title = rst.getString("title");
                String author = rst.getString("author");
                int copies = rst.getInt("copies");
                bookList.add(new Book(isbn, title, author, copies));
            }
            return bookList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
