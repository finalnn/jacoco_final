package service;

import model.Book;
import java.util.ArrayList;
import java.util.List;

public class BookService {

    private final List<Book> books = new ArrayList<>();
    private SearchStrategy searchStrategy;

    public void addBook(String title, String author, String isbn) {
        boolean exists = books.stream().anyMatch(b -> b.getIsbn().equals(isbn));
        if (exists) {
            System.out.println("Cannot add book: ISBN already exists.");
            return;
        }
        books.add(new Book(title, author, isbn));
        System.out.println("Book added: " + title);
    }

    public List<Book> getAllBooks() {
        return books;
    }

    // لتعيين استراتيجية البحث
    public void setSearchStrategy(SearchStrategy strategy) {
        this.searchStrategy = strategy;
    }

    public List<Book> search(String query) {
        if (searchStrategy == null) {
            System.out.println("No search strategy selected!");
            return new ArrayList<>();
        }
        return searchStrategy.search(books, query);
    }

    public boolean borrowBook(Book book, model.User user) {
        if (!user.canBorrow()) return false;
        if (!book.isBorrowed()) {
            book.borrow();
            return true;
        }
        return false;
    }

    public void returnBook(Book book, model.User user) {
        if (book.isBorrowed()) {
            if (book.isOverdue()) {
                user.addFine(5.0);
            }
            book.returnBook();
        }
    }
}
