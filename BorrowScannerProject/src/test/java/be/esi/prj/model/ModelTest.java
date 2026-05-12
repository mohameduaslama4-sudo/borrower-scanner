package be.esi.prj.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void borrowerConstructorsAndMutatorsExposeExpectedState() {
        Borrower persisted = new Borrower(7, "Ada", "Lovelace", "ada@example.com");
        assertEquals(7, persisted.getId());
        assertEquals("Ada", persisted.getFirstName());
        assertEquals("Lovelace", persisted.getLastName());
        assertEquals("ada@example.com", persisted.getEmail());

        Borrower newBorrower = new Borrower("Grace", "Hopper", "grace@example.com");
        newBorrower.setId(42);
        newBorrower.setEmail("hopper@example.com");

        assertEquals(42, newBorrower.getId());
        assertEquals("Grace", newBorrower.getFirstName());
        assertEquals("Hopper", newBorrower.getLastName());
        assertEquals("hopper@example.com", newBorrower.getEmail());
    }

    @Test
    void itemGeneratesReadableIdAndQrPathFromCategory() {
        Item item = new Item("Oscilloscope", ItemCategory.MESURE, ItemStatus.AVAILABLE);

        assertTrue(item.getId().matches("MES-\\d{3}"));
        assertEquals("Oscilloscope", item.getName());
        assertEquals(ItemCategory.MESURE, item.getCategory());
        assertEquals(ItemStatus.AVAILABLE, item.getStatus());
        assertEquals("external-data/qrcodes/" + item.getId() + ".png", item.getQrPath());

        item.setCategory(ItemCategory.ELECTRONIQUE);
        item.setStatus(ItemStatus.MAINTENANCE);

        assertEquals(ItemCategory.ELECTRONIQUE, item.getCategory());
        assertEquals(ItemStatus.MAINTENANCE, item.getStatus());
    }

    @Test
    void itemPersistedConstructorKeepsDatabaseValues() {
        Item item = new Item("PER-777", "Clavier", ItemCategory.PERIPHERIQUE,
                ItemStatus.UNAVAILABLE, "/tmp/PER-777.png");

        assertEquals("PER-777", item.getId());
        assertEquals("Clavier", item.getName());
        assertEquals(ItemCategory.PERIPHERIQUE, item.getCategory());
        assertEquals(ItemStatus.UNAVAILABLE, item.getStatus());
        assertEquals("/tmp/PER-777.png", item.getQrPath());
    }

    @Test
    void itemCounterResumesAfterPersistedIds() {
        Item.synchronizeNextNumber(List.of(
                new Item("CAB-003", "Cable HDMI", ItemCategory.CABLAGE,
                        ItemStatus.AVAILABLE, "external-data/qrcodes/CAB-003.png")));

        Item cable = new Item("USB", ItemCategory.CABLAGE, ItemStatus.AVAILABLE);

        assertEquals("CAB-004", cable.getId());
    }

    @Test
    void loanCreationDefaultsBorrowAndDueDates() {
        Loan loan = new Loan("RES-001", 5);

        assertTrue(loan.getId() > 0);
        assertEquals("RES-001", loan.getItemId());
        assertEquals(5, loan.getBorrowerId());
        assertNull(loan.getReturnDate());
        assertEquals(loan.getBorrowedDate().plusDays(30), loan.getDueDate());
        assertEquals(0, loan.getBorrowedDate().getSecond());
        assertEquals(0, loan.getBorrowedDate().getNano());
    }

    @Test
    void loanPersistedConstructorAndReturnDateAreControlled() {
        LocalDateTime borrowed = LocalDateTime.of(2026, 5, 1, 10, 15);
        LocalDateTime due = borrowed.plusDays(30);
        LocalDateTime returned = borrowed.plusDays(2);

        Loan loan = new Loan(99, "ELE-001", 12, borrowed, due, returned);

        assertEquals(99, loan.getId());
        assertEquals("ELE-001", loan.getItemId());
        assertEquals(12, loan.getBorrowerId());
        assertEquals(borrowed, loan.getBorrowedDate());
        assertEquals(due, loan.getDueDate());
        assertEquals(returned, loan.getReturnDate());
    }

    @Test
    void loanCanBeExtendedUntilSixtyDaysAndNotAfterReturn() {
        LocalDateTime borrowed = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Loan loan = new Loan(1, "STO-001", 3, borrowed, borrowed.plusDays(30), null);

        loan.extendDueDate();
        assertEquals(borrowed.plusDays(45), loan.getDueDate());

        loan.extendDueDate();
        assertEquals(borrowed.plusDays(60), loan.getDueDate());

        assertThrows(IllegalStateException.class, loan::extendDueDate);

        Loan returned = new Loan(2, "STO-002", 4, borrowed, borrowed.plusDays(30), borrowed.plusDays(1));
        assertThrows(IllegalStateException.class, returned::extendDueDate);
    }

    @Test
    void loanReturnDateCanOnlyBeSetOnce() {
        Loan loan = new Loan("CAB-001", 8);
        LocalDateTime returned = LocalDateTime.of(2026, 5, 10, 14, 30);

        loan.setReturnDate(returned);

        assertEquals(returned, loan.getReturnDate());
        assertThrows(IllegalStateException.class, () -> loan.setReturnDate(returned.plusDays(1)));
    }
}
