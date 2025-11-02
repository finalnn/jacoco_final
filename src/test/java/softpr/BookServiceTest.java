package softpr;

import model.Book;
import model.User;
import service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookServiceTest {

    private BookService bookService;

    @BeforeEach
    public void setup() {
        bookService = new BookService();
    }

    @Test
    public void addBookShouldIncreaseSize() {
        bookService.addBook("Clean Code", "Robert Martin", "1234567890");
        List<Book> books = bookService.getAllBooks();
        assertEquals(1, books.size());
        assertEquals("Clean Code", books.get(0).getTitle());
    }
    @Test
    public void addBookShouldNotAllowDuplicateISBN() {
        bookService.addBook("Clean Code", "Robert Martin", "1234567890");
        int sizeBefore = bookService.getAllBooks().size();

        
        bookService.addBook("Another Book", "Another Author", "1234567890");
        int sizeAfter = bookService.getAllBooks().size();

        
        assertEquals(sizeBefore, sizeAfter);

        
        assertEquals("Clean Code", bookService.getAllBooks().get(0).getTitle());
    }
    @Test
    public void borrowShouldSetBorrowedTrue() {
        Book book = new Book("Test", "Author", "123");
        book.borrow();
        assertTrue(book.isBorrowed());
    }

    @Test
    public void bookToStringShouldNotBeNull() {
        Book book = new Book("Clean Code", "Robert Martin", "1234567890");
        String str = book.toString();
        assertNotNull(str);
        assertTrue(str.contains("Clean Code"));
        assertTrue(str.contains("Robert Martin"));
        assertTrue(str.contains("1234567890"));
    }

    @Test
    public void returnBookShouldSetBorrowedFalse() {
        Book book = new Book("Test", "Author", "123");
        book.borrow();
        book.returnBook();
        assertFalse(book.isBorrowed());
    }

    @Test
    public void isOverdueShouldReturnCorrectValue() {
        Book book = new Book("Test", "Author", "123");
        // مثال: إذا عندك منطق لحساب التأخير
        assertFalse(book.isOverdue());
    }


    @Test
    public void getAllBooksShouldReturnAllAddedBooks() {
        bookService.addBook("Book1", "Author1", "ISBN1");
        bookService.addBook("Book2", "Author2", "ISBN2");
        List<Book> books = bookService.getAllBooks();
        assertEquals(2, books.size());
    }

    @Test
    public void searchShouldFindBookByTitle() {
        bookService.addBook("Clean Code", "Robert Martin", "1234567890");
        List<Book> results = bookService.search("Clean Code");
        assertEquals(1, results.size());
        assertEquals("Clean Code", results.get(0).getTitle());
    }

    @Test
    public void searchShouldFindBookByAuthor() {
        bookService.addBook("Clean Code", "Robert Martin", "1234567890");
        List<Book> results = bookService.search("robert");
        assertEquals(1, results.size());
    }

    @Test
    public void searchShouldFindBookByISBN() {
        bookService.addBook("Clean Code", "Robert Martin", "1234567890");
        List<Book> results = bookService.search("1234567890");
        assertEquals(1, results.size());
    }

    @Test
    public void searchShouldReturnEmptyListIfNoMatch() {
        bookService.addBook("Clean Code", "Robert Martin", "1234567890");
        List<Book> results = bookService.search("nonexistent");
        assertTrue(results.isEmpty());
    }

    @Test
    public void searchShouldBeCaseInsensitive() {
        bookService.addBook("Clean Code", "Robert Martin", "1234567890");
        List<Book> results = bookService.search("cLeAn");
        assertEquals(1, results.size());
    }
    
    
    @Test
    public void borrowBookShouldSucceedWhenAvailableAndNoFine() {
        Book book = new Book("Test Book", "Author", "ISBN1");
        User user = new User("Noor");
        boolean borrowed = bookService.borrowBook(book, user);
        assertTrue(borrowed);
        assertTrue(book.isBorrowed());
    }

    @Test
    public void borrowBookShouldFailIfAlreadyBorrowed() {
        Book book = new Book("Test Book", "Author", "ISBN1");
        User user1 = new User("Noor");
        User user2 = new User("Ali");
        bookService.borrowBook(book, user1);
        boolean borrowed = bookService.borrowBook(book, user2);
        assertFalse(borrowed);
    }

    @Test
    public void borrowBookShouldFailIfUserHasFine() {
        Book book = new Book("Test Book", "Author", "ISBN1");
        User user = new User("Noor");
        user.addFine(10);
        boolean borrowed = bookService.borrowBook(book, user);
        assertFalse(borrowed);
        assertFalse(book.isBorrowed());
    }

    @Test
    public void returnBookShouldResetBorrowed() {
        Book book = new Book("Test Book", "Author", "ISBN1");
        User user = new User("Noor");
        bookService.borrowBook(book, user);
        bookService.returnBook(book, user);
        assertFalse(book.isBorrowed());
    }

    @Test
    public void returnBookShouldAddFineIfOverdue() {
        Book book = new Book("Test Book", "Author", "ISBN1") {
            @Override
            public boolean isOverdue() {
                return true; // محاكاة كتاب متأخر
            }
        };
        User user = new User("Noor");
        bookService.borrowBook(book, user);
        bookService.returnBook(book, user);
        assertEquals(5.0, user.getFineBalance());
    }

}
