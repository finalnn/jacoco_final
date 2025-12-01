package softpr;

import model.Book;
import model.Loan;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.LoanService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoanServiceTest {

    private LoanService loanService;
    private User user;

    @BeforeEach
    public void setup() {
        loanService = new LoanService();
        user = new User("Noor","noorfayek321@gmail.com");
    }

    @Test
    public void loanCreateAddsLoan() {
        Book book = new Book("Algorithms", "Robert Sedgewick", "789");
        Loan loan = loanService.createLoan(book, user);
        List<Loan> loans = loanService.getAllLoans();
        assertEquals(1, loans.size());
        assertEquals(loan, loans.get(0));
        assertTrue(book.isBorrowed());
        assertFalse(loan.isReturned());
    }

    @Test
    public void returnLoanMarksReturnedAndCalculatesFine() {
        Book book = new Book("Data Structures", "Mark Allen", "321");
        Loan loan = loanService.createLoan(book, user);
        loan.setDueDate(loan.getBorrowDate().minusDays(2));
        loanService.returnLoan(loan);
        assertTrue(loan.isReturned());
        assertFalse(book.isBorrowed());
        assertEquals(2.0, user.getFineBalance());
    }

    @Test
    public void getUserLoansReturnsOnlyActiveLoans() {
        Book book1 = new Book("C++ Fundamentals", "Bjarne Stroustrup", "654");
        Book book2 = new Book("Python Intro", "Guido Rossum", "987");
        Loan loan1 = loanService.createLoan(book1, user);
        loan1.markReturned();
        Loan loan2 = loanService.createLoan(book2, user);
        List<Loan> userLoans = loanService.getUserLoans(user);
        assertEquals(1, userLoans.size());
        assertEquals("Python Intro", userLoans.get(0).getBook().getTitle());
    }

    @Test
    public void isOverdueDetectsPastDueDate() {
        Book book = new Book("Java Basics", "James Gosling", "852");
        Loan loan = new Loan(book, user);
        loan.setDueDate(LocalDate.now().minusDays(1));
        assertTrue(loan.isOverdue());
    }

    @Test
    public void cannotBorrowIfOnlyFineExists() {
        Book book = new Book("Book A", "Author", "101");
        user.addFine(5.0);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            loanService.createLoan(book, user);
        });
        assertTrue(ex.getMessage().contains("You cannot borrow"));
    }

    @Test
    public void cannotBorrowIfOnlyOverdueLoanExists() {
        Book oldBook = new Book("Old Book", "Author", "102");
        Loan oldLoan = loanService.createLoan(oldBook, user);
        oldLoan.setDueDate(oldLoan.getBorrowDate().minusDays(1));

        Book newBook = new Book("New Book", "Author", "103");
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            loanService.createLoan(newBook, user);
        });
        assertTrue(ex.getMessage().contains("You cannot borrow"));
    }

    @Test
    public void canBorrowIfNoFinesAndNoOverdueLoans() {
        Book book = new Book("Clean Book", "Author", "104");
        Loan loan = loanService.createLoan(book, user);
        assertFalse(loan.isReturned());
        assertTrue(book.isBorrowed());
    }
}
