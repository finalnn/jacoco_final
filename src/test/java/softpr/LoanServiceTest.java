package softpr;

import model.Book;
import model.CD;
import model.Loan;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.LoanService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {

    private LoanService loanService;
    private User user;

    @BeforeEach
    void setup() {
        loanService = new LoanService();
        user = new User("Noor", "noorfayek321@gmail.com");
    }

    @Test
    void loanCreateAddsLoanBook() {
        Book book = new Book("Algorithms", "Robert Sedgewick", "001") {
            @Override
            public double getFinePerDay() { return 2.0; }
        };
        Loan loan = loanService.createLoan(book, user);
        List<Loan> loans = loanService.getAllLoans();
        assertEquals(1, loans.size());
        assertEquals(loan, loans.get(0));
        assertTrue(book.isBorrowed());
        assertFalse(loan.isReturned());
    }

    @Test
    void loanCreateAddsLoanCD() {
        CD cd = new CD("Rock Classics", "Queen", "CD001") {
            @Override
            public double getFinePerDay() { return 1.0; }
        };
        Loan loan = loanService.createLoan(cd, user);
        List<Loan> loans = loanService.getAllLoans();
        assertEquals(1, loans.size());
        assertEquals(loan, loans.get(0));
        assertTrue(cd.isBorrowed());
        assertFalse(loan.isReturned());
    }

    @Test
    void returnLoanMarksReturnedAndCalculatesFine() {
        Book book = new Book("Data Structures", "Mark Allen", "002") {
            @Override
            public double getFinePerDay() { return 10.0; }
        };

        Loan loan = loanService.createLoan(book, user);

       loan.setDueDate(LocalDate.now().minusDays(2));

        loanService.returnLoan(loan);

        assertTrue(loan.isReturned());
        assertFalse(book.isBorrowed());

         double expectedFine = loan.getDaysOverdue() * book.getFinePerDay();
        user.addFine(expectedFine);

        // التحقق من رصيد الغرامة
        assertEquals(20.0, user.getFineBalance());
    }


    @Test
    void getUserLoansReturnsOnlyActiveLoans() {
        Book book1 = new Book("C++ Fundamentals", "Bjarne Stroustrup", "003");
        Book book2 = new Book("Python Intro", "Guido Rossum", "004");

        Loan loan1 = loanService.createLoan(book1, user);
        loan1.markReturned(); // إرجاع القرض
        Loan loan2 = loanService.createLoan(book2, user);

        List<Loan> userLoans = loanService.getUserLoans(user);
        assertEquals(1, userLoans.size());
        assertEquals("Python Intro", userLoans.get(0).getMedia().getTitle());
    }

    @Test
    void canBorrowAfterReturningOverdueLoanWithNoFine() {
        Book oldBook = new Book("Old Book", "Author", "005") {
            @Override
            public double getFinePerDay() { return 0.0; }
        };
        Loan oldLoan = loanService.createLoan(oldBook, user);
        oldLoan.setDueDate(LocalDate.now().minusDays(3));
        loanService.returnLoan(oldLoan);

        CD newCD = new CD("New CD", "Artist", "CD002");
        Loan newLoan = loanService.createLoan(newCD, user);

        assertFalse(newLoan.isReturned());
        assertTrue(newCD.isBorrowed());
    }
    @Test
    void testGetUserLoansFullCoverage() {
        User u1 = new User("Alice","nnn@gmail.com");
        User u2 = new User("Bob","nn@gmail.com");

        Book b1 = new Book("Book1", "Author1", "001");
        Book b2 = new Book("Book2", "Author2", "002");

        Loan loan1 = loanService.createLoan(b1, u1); 
        Loan loan2 = loanService.createLoan(b2, u1); 
        loan2.markReturned();
        Loan loan3 = loanService.createLoan(b1, u2); 

        List<Loan> loans = loanService.getUserLoans(u1);

       assertEquals(1, loans.size());
        assertEquals(loan1, loans.get(0));
    }

    @Test
    void testGetBorrowAndDueDate() {
        Book book = new Book("Java", "Author", "006");
        Loan loan = loanService.createLoan(book, user);

        assertNotNull(loan.getDueDate());
        assertNotNull(loan.getBorrowDate());
        assertEquals(LocalDate.now(), loan.getBorrowDate());
    }

   @Test
    void cannotBorrowIfOnlyFineExists() {
        Book book = new Book("Book A", "Author", "007");
        user.addFine(5.0);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            loanService.createLoan(book, user);
        });
        assertTrue(ex.getMessage().contains("You cannot borrow"));
    }

    @Test
    void cannotBorrowIfOnlyFineExistsMultipleAmounts() {
        Book book = new Book("Book B", "Author", "008");
        user.addFine(10.0);
        user.addFine(5.0);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            loanService.createLoan(book, user);
        });
        assertTrue(ex.getMessage().contains("You cannot borrow"));
    }

    @Test
    void testBookGetFinePerDayAndSetDueDate() {
        Book book = new Book("Test Book", "Author", "999");
        assertEquals(10.0, book.getFinePerDay());  
        LocalDate due = LocalDate.now().plusDays(5);
        book.setDueDate(due);
        assertEquals(due, book.getDueDate());      
    }

    @Test
    void testCDSetDueDateAndGetFinePerDay() {
        CD cd = new CD("Test CD", "Artist", "CD999");
        assertEquals(20.0, cd.getFinePerDay());   
        LocalDate due = LocalDate.now().plusDays(3);
        cd.setDueDate(due);
        assertEquals(due, cd.getDueDate());       
    }

    @Test
    void testLoanGetDaysOverdue() {
        Book book = new Book("Late Book", "Author", "998");
        Loan loan = loanService.createLoan(book, user);

        assertEquals(0, loan.getDaysOverdue());

        loan.setDueDate(LocalDate.now().minusDays(2));
        assertEquals(2, loan.getDaysOverdue());
    }

    @Test
    void testLoanIsOverdueFlag() {
        Book book = new Book("Overdue Book", "Author", "997");
        Loan loan = loanService.createLoan(book, user);

      assertFalse(loan.isOverdue());

        loan.setDueDate(LocalDate.now().minusDays(1));
        assertTrue(loan.isOverdue());

        loan.markReturned();
        assertFalse(loan.isOverdue());
    }

    @Test
    void cannotBorrowIfOnlyOverdueLoanExists() {
        Book oldBook = new Book("Old Book", "Author", "009");
        Loan oldLoan = loanService.createLoan(oldBook, user);
        oldLoan.setDueDate(LocalDate.now().minusDays(1));

        CD newCD = new CD("New CD", "Artist", "CD003");
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            loanService.createLoan(newCD, user);
        });
        assertTrue(ex.getMessage().contains("You cannot borrow"));
    }

    @Test
    void cannotBorrowIfUserHasMultipleOverdueLoans() {
        Book book1 = new Book("Book1", "Author", "010");
        CD cd1 = new CD("CD1", "Artist", "CD004");

        Loan loan1 = loanService.createLoan(book1, user);
        Loan loan2 = loanService.createLoan(cd1, user);

        loan1.setDueDate(LocalDate.now().minusDays(2));
        loan2.setDueDate(LocalDate.now().minusDays(1));

        Book newBook = new Book("Book3", "Author", "011");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            loanService.createLoan(newBook, user);
        });
        assertTrue(ex.getMessage().contains("You cannot borrow"));
    }
}
