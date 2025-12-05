package softpr;

import model.CD;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.CDService;

import java.time.LocalDate;
import java.util.List;
import java.util.Observer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CDServiceTest {

    private CDService cdService;
    private User user;

    @BeforeEach
    void setUp() {
        cdService = new CDService();
        user = new User("Hala", "hala@example.com");
    }

    @Test
    void testAddCDByDetails() {
        cdService.addCD("Rock", "Queen", "CD001");
        List<CD> cds = cdService.getAllCDs();
        assertEquals(1, cds.size());
        assertEquals("Rock", cds.get(0).getTitle());
        assertEquals("Queen", cds.get(0).getArtist());
        assertEquals("CD001", cds.get(0).getId());
    }

    @Test
    void testAddCDObject() {
        CD cd = new CD("Jazz", "Miles", "CD002");
        cdService.addCD(cd);
        List<CD> cds = cdService.getAllCDs();
        assertTrue(cds.contains(cd));
    }

    @Test
    void testAddDuplicateCD() {
        cdService.addCD("Rock", "Queen", "CD001");
        cdService.addCD("Rock Duplicate", "Queen", "CD001");
        List<CD> cds = cdService.getAllCDs();
        assertEquals(1, cds.size());
    }

    @Test
    void testSearchCD() {
        cdService.addCD("Rock", "Queen", "CD001");
        cdService.addCD("Jazz Hits", "Miles", "CD002");
        List<CD> results = cdService.search("rock");
        assertEquals(1, results.size());
        assertEquals("Rock", results.get(0).getTitle());
        results = cdService.search("Miles");
        assertEquals(1, results.size());
        assertEquals("Jazz Hits", results.get(0).getTitle());
        results = cdService.search("CD002");
        assertEquals(1, results.size());
        assertEquals("Jazz Hits", results.get(0).getTitle());
        results = cdService.search("none");
        assertTrue(results.isEmpty());
    }

    @Test
    void testBorrowCDSuccessful() {
        CD cd = new CD("Rock", "Queen", "CD001");
        cdService.addCD(cd);
        assertTrue(cdService.borrowCD(cd, user));
        assertTrue(cd.isBorrowed());
        assertEquals(user, cd.getBorrower());
    }

    @Test
    void testBorrowCDWhenAlreadyBorrowed() {
        CD cd = new CD("Rock", "Queen", "CD001");
        cdService.addCD(cd);
        cd.borrow(user);
        User anotherUser = new User("Noor", "noor@example.com");
        assertFalse(cdService.borrowCD(cd, anotherUser));
    }

    @Test
    void testBorrowCDWhenUserCannotBorrow() {
        CD cd = new CD("Rock", "Queen", "CD001");
        cdService.addCD(cd);
        User mockUser = mock(User.class);
        when(mockUser.canBorrow()).thenReturn(false);
        assertFalse(cdService.borrowCD(cd, mockUser));
    }

    @Test
    void testReturnCDNotOverdue() {
        CD cd = new CD("Rock", "Queen", "CD001");
        cdService.addCD(cd);
        cd.borrow(user);
        double fineBefore = user.getFineBalance();
        cdService.returnCD(cd, user);
        assertFalse(cd.isBorrowed());
        assertEquals(fineBefore, user.getFineBalance());
    }

    @Test
    void testReturnCDOverdueAddsFine() {
        CD cd = new CD("Rock", "Queen", "CD001");
        cdService.addCD(cd);
        cd.borrow(user);
        cd.setDueDate(LocalDate.now().minusDays(2));
        double fineBefore = user.getFineBalance();
        cdService.returnCD(cd, user);
        assertFalse(cd.isBorrowed());
        assertTrue(user.getFineBalance() > fineBefore);
    }

    @Test
    void testCheckOverdueCDsNotifiesObservers() {
        Observer mockObserver = mock(Observer.class);
        cdService.addObserver(mockObserver);
        CD cd = new CD("Rock", "Queen", "CD001");
        cdService.addCD(cd);
        cd.borrow(user);
        cd.setDueDate(LocalDate.now().minusDays(1));
        cdService.checkOverdueCDs();
        verify(mockObserver, times(1)).update(eq(cdService), eq(cd));
    }

    @Test
    void testCheckOverdueCDsNoNotificationIfNotOverdue() {
        Observer mockObserver = mock(Observer.class);
        cdService.addObserver(mockObserver);
        CD cd = new CD("Rock", "Queen", "CD001");
        cdService.addCD(cd);
        cd.borrow(user);
        cdService.checkOverdueCDs();
        verify(mockObserver, never()).update(any(), any());
    }
}