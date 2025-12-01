package softpr;

import model.User;
import service.FineSummaryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class FineSummaryServiceTest {

    private FineSummaryService fineService;
    private User user;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        fineService = new FineSummaryService();
        user = new User("Noor", "noorfayek321@gmail.com");
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testGetTotalFineInitiallyZero() {
        assertEquals(0.0, fineService.getTotalFine(user));
    }

    @Test
    void testGetTotalFineAfterAddingFine() {
        user.addFine(15.0);
        assertEquals(15.0, fineService.getTotalFine(user));
    }

    @Test
    void testPrintReportOutput() {
        user.addFine(20.0);
        fineService.printReport(user);

        String output = outContent.toString();
        assertTrue(output.contains("Noor"));
        assertTrue(output.contains("20.0"));
    }
}
