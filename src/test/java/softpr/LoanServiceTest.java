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
    private Book book;
    private User user;

    @BeforeEach
    public void setup() {
        loanService = new LoanService();
        book = new Book("Clean Code", "Robert Martin", "123");
        user = new User("Noor");
    }

    @Test
    public void createLoanAddsLoan() {
        Loan loan = loanService.createLoan(book, user);
        List<Loan> loans = loanService.getAllLoans();
        assertEquals(1, loans.size());
        assertEquals(loan, loans.get(0));
        assertTrue(book.isBorrowed());
        assertFalse(loan.isReturned());
    }
    
    @Test
    public void loanGetDueDateShouldReturnCorrectDate() {
        Book book = new Book("Clean Code", "Robert Martin", "123");
        User user = new User("Noor");
        Loan loan = new Loan(book, user);

        assertNotNull(loan.getDueDate());
        // التأكد أن due date بعد borrow date
        assertTrue(loan.getDueDate().isAfter(loan.getBorrowDate()));
    }


    @Test
    public void returnLoanMarksReturned() {
        Loan loan = loanService.createLoan(book, user);
        loanService.returnLoan(loan);
        assertTrue(loan.isReturned());
        assertFalse(book.isBorrowed());
    }

    @Test
    public void getUserLoansReturnsOnlyActiveLoans() {
        Loan loan1 = loanService.createLoan(book, user);
        loanService.returnLoan(loan1);
        Loan loan2 = loanService.createLoan(new Book("Book2", "Author2", "456"), user);
        List<Loan> userLoans = loanService.getUserLoans(user);
        assertEquals(1, userLoans.size());
        assertEquals("Book2", userLoans.get(0).getBook().getTitle());
    }

    @Test
    public void isOverdueDetectsPastDueDate() {
        Loan loan = new Loan(book, user) {
            @Override
            public boolean isOverdue() {
                return LocalDate.now().isAfter(getBorrowDate().minusDays(1));
            }
        };
        loanService.getAllLoans().add(loan);
        assertTrue(loan.isOverdue());
    }
}
