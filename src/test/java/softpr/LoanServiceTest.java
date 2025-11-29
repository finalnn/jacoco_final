package softpr;

import model.Book;
import model.Loan;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.LoanService;
import service.FineService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoanServiceTest {

    private LoanService loanService;
    private Book book;
    private User user;

    @BeforeEach
    public void setup() {
        FineService fineService = new FineService();
        loanService = new LoanService(fineService);
        book = new Book("Clean Code", "Robert Martin", "123");
        user = new User("Noor","noorfayek321@gmail.com");
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
        User user = new User("Noor","noorfayek321@gmail.com");
        Loan loan = new Loan(book, user);

        assertNotNull(loan.getDueDate());
        assertTrue(loan.getDueDate().isAfter(loan.getBorrowDate()));
    }

    @Test
    public void returnLoanMarksReturned() {
        Loan loan = loanService.createLoan(book, user);
        loan.markReturned(); // بدل setReturned
        assertTrue(loan.isReturned());
        assertFalse(book.isBorrowed());
    }

    @Test
    public void getUserLoansReturnsOnlyActiveLoans() {
        Loan loan1 = loanService.createLoan(book, user);
        loan1.markReturned(); // بدل setReturned
        Loan loan2 = loanService.createLoan(new Book("Book2", "Author2", "456"), user);
        List<Loan> userLoans = loanService.getUserLoans(user);
        assertEquals(1, userLoans.size());
        assertEquals("Book2", userLoans.get(0).getBook().getTitle());
    }

    @Test
    public void isOverdueDetectsPastDueDate() {
        Loan loan = new Loan(book, user);
        // نجعل تاريخ الاستحقاق قبل اليوم حتى يصير متأخر
        loan.setDueDate(LocalDate.now().minusDays(2));
        assertTrue(loan.isOverdue());
    }
}
