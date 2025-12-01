package softpr;

import model.Book;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookServiceTest {

    private BookService bookService;

    @BeforeEach
    public void setup() {
        bookService = new BookService();
    }

    @Test
    public void addBookIncreasesList() {
        bookService.addBook("Java Basics", "James Gosling", "001");
        List<Book> books = bookService.getAllBooks();
        assertEquals(1, books.size());
    }

    @Test
    public void duplicateISBNShouldNotAddBook() {
        bookService.addBook("Java", "A", "001");
        int before = bookService.getAllBooks().size();
        bookService.addBook("Python", "B", "001");
        assertEquals(before, bookService.getAllBooks().size());
    }

    @Test
    void bookGetFinePerDayShouldReturnCorrectValue() {
        Book book = new Book("Data Structures", "Mark Allen", "002");
        assertEquals(10.0, book.getFinePerDay());
    }

    @Test
    void bookSetDueDateShouldChangeDueDate() {
        Book book = new Book("Data Structures", "Mark Allen", "002");
        LocalDate newDate = LocalDate.now().plusDays(5);
        book.setDueDate(newDate);
        assertEquals(newDate, book.getDueDate());
    }
    
    @Test
    void searchWithNoStrategyReturnsEmpty() {
        BookService service = new BookService();
        List<Book> result = service.search("Java");
        assertTrue(result.isEmpty());
    }

    @Test
    void searchWithStrategyFindsBooks() {
        BookService service = new BookService();
        service.addBook("Java Basics", "Author A", "001");
        service.setSearchStrategy((books, query) -> {
            List<Book> result = new ArrayList<>();
            for (Book b : books) {
                if (b.getTitle().contains(query)) result.add(b);
            }
            return result;
        });

        List<Book> result = service.search("Java");
        assertEquals(1, result.size());
    }

    @Test
    void searchWithStrategyNoMatches() {
        BookService service = new BookService();
        service.addBook("Python Basics", "Author B", "002");
        service.setSearchStrategy((books, query) -> {
            List<Book> result = new ArrayList<>();
            for (Book b : books) {
                if (b.getTitle().contains(query)) result.add(b);
            }
            return result;
        });

        List<Book> result = service.search("Java");
        assertTrue(result.isEmpty());
    }


    @Test
    public void borrowAvailableBookSucceeds() {
        Book book = new Book("Algorithms", "Sedgewick", "002");
        User user = new User("Noor", "noorfayek321@gmail.com");
        boolean result = bookService.borrowBook(book, user);
        assertTrue(result);
        assertTrue(book.isBorrowed());
    }
    
    @Test
    void testGetDaysOverdueDirectly() throws Exception {
        User user = new User("Noor", "noor@gmail.com");
        Book book = new Book("Java", "Author", "006");
        book.borrow(user);

       
        var field = Book.class.getDeclaredField("dueDate");
        field.setAccessible(true);
        field.set(book, java.time.LocalDate.now().minusDays(3));

        assertEquals(3, book.getDaysOverdue());
    }


    @Test
    public void borrowFailsIfBookAlreadyBorrowed() {
        Book book = new Book("Algorithms", "Sedgewick", "002");
        User u1 = new User("A", "a@a.com");
        User u2 = new User("B", "b@b.com");
        bookService.borrowBook(book, u1);
        assertFalse(bookService.borrowBook(book, u2));
    }

    @Test
    public void borrowBookFailsIfUserCannotBorrow() {
        Book book = new Book("Java", "Author", "003");
        User user = new User("Noor", "noor@gmail.com") {
            @Override
            public boolean canBorrow() {
                return false;
            }
        };
        assertFalse(bookService.borrowBook(book, user));
    }

    @Test
    public void returnBookResetsBorrowedFlag() {
        Book book = new Book("Data", "Mark", "003");
        User u = new User("Noor", "noor@gmail.com");
        bookService.borrowBook(book, u);
        bookService.returnBook(book, u);
        assertFalse(book.isBorrowed());
    }

    @Test
    public void returnBookAddsFineIfOverdue() {
        User user = new User("Noor", "noor@gmail.com");
        Book book = new Book("Java", "Author", "004") {
            @Override
            public boolean isOverdue() { return true; }
        };
        book.borrow(user);

        bookService.returnBook(book, user);

        assertEquals(5.0, user.getFineBalance());
        assertFalse(book.isBorrowed());
    }

    @Test
    public void searchByTitleShouldWork() {
        bookService.setSearchStrategy(new SearchByTitle());
        bookService.addBook("Java", "A", "001");
        List<Book> res = bookService.search("java");
        assertEquals(1, res.size());
    }

    @Test
    public void searchByISBNShouldWork() {
        bookService.setSearchStrategy(new SearchByISBN());
        bookService.addBook("Java", "A", "001");
        List<Book> res = bookService.search("001");
        assertEquals(1, res.size());
    }

    @Test
    public void searchByAuthorShouldWork() {
        bookService.setSearchStrategy(new SearchByAuthor());
        bookService.addBook("Java", "James Gosling", "001");
        List<Book> res = bookService.search("james");
        assertEquals(1, res.size());
    }
    @Test
    void testGetBorrowerAndDueDate() {
        User user = new User("Noor", "noor@gmail.com");
        Book book = new Book("Java", "Author", "001");
        book.borrow(user);

        assertEquals(user, book.getBorrower());
        assertNotNull(book.getDueDate());
    }

   

    @Test
    void testToString() {
        Book book = new Book("Java", "Author", "003");
        String str = book.toString();
        assertTrue(str.contains("Java"));
        assertTrue(str.contains("Author"));
        assertTrue(str.contains("003"));
    }
    @Test
    public void searchShouldReturnEmptyIfNotFound() {
        bookService.setSearchStrategy(new SearchByTitle());
        bookService.addBook("Java", "A", "001");
        assertTrue(bookService.search("C++").isEmpty());
    }
    
    @Test
    void testGetDaysOverdue() {
        User user = new User("Noor", "noor@gmail.com");

        Book book = new Book("Java", "Author", "001") {
            @Override
            public boolean isOverdue() {
                return true; }

            @Override
            public long getDaysOverdue() {
                
            	return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now().minusDays(5), LocalDate.now());
            }
        };

        book.borrow(user);

        long days = book.getDaysOverdue();
        assertEquals(5, days);
    }


    @Test
    public void checkOverdueBooksNotifiesObserver() {
        BookService service = new BookService();
        User user = new User("Noor", "noorfayek321@gmail.com");

        Book book = new Book("Java", "Author", "005") {
            @Override
            public boolean isOverdue() { return true; }
        };
        book.borrow(user);

        service.addBook(book); 
        
        Observer observer = mock(Observer.class);
        service.addObserver(observer);

        service.checkOverdueBooks();

        verify(observer, times(1)).update(service, book);
    }
}
