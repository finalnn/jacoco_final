package softpr;

import model.CD;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CDTest {

    private CD cd;
    private User user;

    @BeforeEach
    void setup() {
        cd = new CD("Rock Classics", "Queen", "CD002");
        user = new User("Noor", "noorfayek321@gmail.com");
    }

    @Test
    void testCDGetters() {
        assertEquals("Rock Classics", cd.getTitle());
        assertEquals("Queen", cd.getArtist());
        assertFalse(cd.isBorrowed());
        assertNull(cd.getDueDate());
        assertNull(cd.getBorrower());
    }
    @Test
    void testIsOverdueWhenNotBorrowed() {
        assertFalse(cd.isOverdue());
    }
    @Test
    void testDaysOverdueWhenNotOverdue() {
        cd.borrow(user);
        cd.setDueDate(LocalDate.now().plusDays(5)); // لسه مش متأخر
        assertEquals(0, cd.getDaysOverdue());
    }

    @Test
    void testBorrowCD() {
        cd.borrow(user);
        assertTrue(cd.isBorrowed());
        assertEquals(user, cd.getBorrower());
        assertEquals(LocalDate.now().plusDays(7), cd.getDueDate());

        // محاولة استعارة CD مستعار
        cd.borrow(user); // حالياً لا ترمي استثناء، يمكن تعديل لوضع check
        assertTrue(cd.isBorrowed());
    }

    @Test
    void testReturnCD() {
        cd.borrow(user);
        cd.returnMedia();
        assertFalse(cd.isBorrowed());
        assertNull(cd.getBorrower());
        assertNull(cd.getDueDate());
    }

    @Test
    void testFinePerDay() {
        assertEquals(20.0, cd.getFinePerDay());
    }
    @Test
    void cdSetDueDateShouldChangeDueDate() {
        CD cd = new CD("Hits", "Artist A", "CD001");
        LocalDate newDate = LocalDate.now().plusDays(3);
        cd.setDueDate(newDate);
        assertEquals(newDate, cd.getDueDate());
    }

    @Test
    void cdGetFinePerDayShouldReturnCorrectValue() {
        CD cd = new CD("Hits", "Artist A", "CD001");
        assertEquals(20.0, cd.getFinePerDay());
    }

    @Test
    void testOverdueAndDaysOverdue() {
        cd.borrow(user);
        cd.returnCD(); 
        cd.borrow(user);

         cd.borrow(user);
        try {
            java.lang.reflect.Field dueField = CD.class.getDeclaredField("dueDate");
            dueField.setAccessible(true);
            dueField.set(cd, LocalDate.now().minusDays(3));
        } catch (Exception ignored) {}

        assertTrue(cd.isOverdue());
        assertEquals(3, cd.getDaysOverdue());
    }

    @Test
    void testToString() {
        String str = cd.toString();
        assertTrue(str.contains("CD: Rock Classics"));
        assertTrue(str.contains("Queen"));
    }
}
