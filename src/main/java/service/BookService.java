package service;

import model.Book;

import java.util.ArrayList;
import java.util.List;

public class BookService {

    private final List<Book> books = new ArrayList<>();

    public void addBook(String title, String author, String isbn) {
        // تحقق من وجود ISBN مسبقًا
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

    public List<Book> search(String query) {
        String q = query.toLowerCase();
        List<Book> result = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(q)
                    || b.getAuthor().toLowerCase().contains(q)
                    || b.getIsbn().toLowerCase().contains(q)) {
                result.add(b);
            }
        }
        return result;
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
                user.addFine(5.0); // مثال: غرامة ثابتة
            }
            book.returnBook();
        }
    }
}
