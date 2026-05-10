package be.esi.prj.service;

import be.esi.prj.model.Borrower;
import be.esi.prj.model.Item;
import be.esi.prj.model.ItemCategory;
import be.esi.prj.model.ItemStatus;
import be.esi.prj.model.Loan;
import be.esi.prj.repository.BorrowerRepositoryImpl;
import be.esi.prj.repository.ItemRepositoryImpl;
import be.esi.prj.repository.LoanRepositoryImpl;
import be.esi.prj.testsupport.TestDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    private BorrowerService borrowerService;
    private ItemService itemService;
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        TestDatabase.reset();
        borrowerService = BorrowerService.getInstance();
        itemService = ItemService.getInstance();
        loanService = LoanService.getInstance();
    }

    @Test
    void borrowerServiceLoadsAddsFindsUpdatesAndDeletesBorrowers() {
        assertTrue(borrowerService.load().isEmpty());

        Borrower borrower = borrowerService.addBorrower("Curie", "Marie", "marie@example.com");
        borrowerService.getBorrowersObservable().add(borrower);

        assertTrue(borrower.getId() > 0);
        assertEquals(1, borrowerService.load().size());
        assertEquals(borrower.getId(), borrowerService.getBorrowerById(borrower.getId()).getId());
        assertNull(borrowerService.getBorrowerById(999_999));
        assertEquals(1, borrowerService.getAllBorrowers().size());

        borrowerService.updateBorrowerEmail(borrower.getId(), "new@example.com");
        assertEquals("NEW@EXAMPLE.COM", borrowerService.getBorrowerById(borrower.getId()).getEmail());

        borrowerService.deleteBorrower(borrower.getId());
        assertTrue(borrowerService.load().isEmpty());
    }

    @Test
    void borrowerServiceRejectsIncompleteOrInvalidData() {
        assertThrows(IllegalArgumentException.class,
                () -> borrowerService.addBorrower("", "Marie", "marie@example.com"));
        assertThrows(IllegalArgumentException.class,
                () -> borrowerService.addBorrower("Curie", "", "marie@example.com"));
        assertThrows(IllegalArgumentException.class,
                () -> borrowerService.addBorrower("Curie", "Marie", "bad-email"));

        Borrower borrower = borrowerService.addBorrower("Curie", "Marie", "marie@example.com");
        borrowerService.getBorrowersObservable().add(borrower);
        assertThrows(IllegalArgumentException.class,
                () -> borrowerService.updateBorrowerEmail(borrower.getId(), "bad-email"));
    }

    @Test
    void itemServiceAddsUpdatesLoadsFindsAndDeletesItems() {
        Item item = new Item("Oscilloscope", ItemCategory.MESURE, ItemStatus.AVAILABLE);

        Item added = itemService.addItem(item);

        assertSame(item, added);
        assertEquals(1, itemService.getItemsObservable().size());
        assertTrue(Files.exists(Path.of(item.getQrPath())));
        assertEquals(item.getId(), itemService.getItemById(item.getId()).getId());
        assertNull(itemService.getItemById("ABSENT"));
        assertEquals(1, itemService.getAllItems().size());

        item.setCategory(ItemCategory.ELECTRONIQUE);
        item.setStatus(ItemStatus.MAINTENANCE);
        itemService.updateItem(item);

        Item persisted = new ItemRepositoryImpl().findById(item.getId());
        assertEquals(ItemCategory.ELECTRONIQUE, persisted.getCategory());
        assertEquals(ItemStatus.MAINTENANCE, persisted.getStatus());

        itemService.deleteItem(item.getId());
        assertTrue(itemService.getItemsObservable().isEmpty());
        assertTrue(itemService.load().isEmpty());
    }

    @Test
    void itemServiceRejectsInvalidItems() {
        assertThrows(IllegalArgumentException.class,
                () -> itemService.addItem(new Item("", ItemCategory.AUTRE, ItemStatus.AVAILABLE)));
        assertThrows(IllegalArgumentException.class,
                () -> itemService.addItem(new Item("AUT-999", "Nom", null,
                        ItemStatus.AVAILABLE, "external-data/qrcodes/AUT-999.png")));
    }

    @Test
    void loanServiceCreatesReturnsExtendsAndCountsLoans() {
        int borrowerId = new BorrowerRepositoryImpl().insert(
                new Borrower("Turing", "Alan", "alan@example.com"));
        Item item = itemService.addItem(new Item("Raspberry", ItemCategory.ELECTRONIQUE, ItemStatus.AVAILABLE));
        Loan loan = new Loan(item.getId(), borrowerId);

        loanService.createLoan(loan);

        assertEquals(ItemStatus.UNAVAILABLE, itemService.getItemById(item.getId()).getStatus());
        assertEquals(1, loanService.getLoansObservable().size());
        assertEquals(loan, loanService.getLoanById(loan.getId()));
        assertNull(loanService.getLoanById(999_999));
        assertEquals(1, loanService.getLoansByBorrower(borrowerId).size());
        assertEquals(1, loanService.countActiveLoans());
        assertEquals(0, loanService.countOverdueLoans());
        assertEquals(1, loanService.countLoansToday());

        LocalDateTime firstDueDate = loan.getDueDate();
        loanService.extendLoan(loan.getId());
        assertEquals(firstDueDate.plusDays(15), loanService.getLoanById(loan.getId()).getDueDate());

        Loan returned = loanService.returnLoan(loan.getId());
        assertNotNull(returned.getReturnDate());
        assertEquals(ItemStatus.AVAILABLE, itemService.getItemById(item.getId()).getStatus());
        assertEquals(1, loanService.countReturnsToday());
    }

    @Test
    void loanServiceRejectsInvalidLoanScenarios() {
        int borrowerId = new BorrowerRepositoryImpl().insert(
                new Borrower("Noether", "Emmy", "emmy@example.com"));
        Item unavailable = itemService.addItem(new Item("Switch", ItemCategory.RESEAU, ItemStatus.MAINTENANCE));

        assertThrows(IllegalStateException.class,
                () -> loanService.createLoan(new Loan(unavailable.getId(), borrowerId)));
        assertThrows(IllegalArgumentException.class, () -> loanService.extendLoan(999_999));
        assertThrows(IllegalStateException.class, () -> loanService.returnLoan(999_999));

        Item available = itemService.addItem(new Item("Cable", ItemCategory.CABLAGE, ItemStatus.AVAILABLE));
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan(new Loan(available.getId(), 0)));
        assertThrows(NullPointerException.class,
                () -> loanService.createLoan(new Loan("UNKNOWN", borrowerId)));

        Loan valid = new Loan(available.getId(), borrowerId);
        loanService.createLoan(valid);
        loanService.returnLoan(valid.getId());
        assertThrows(IllegalStateException.class, () -> loanService.returnLoan(valid.getId()));
    }

    @Test
    void loanServiceDelegatesStatisticsToRepository() {
        int borrowerId = new BorrowerRepositoryImpl().insert(
                new Borrower("Meitner", "Lise", "lise@example.com"));
        Item item = new Item("STO-500", "SSD", ItemCategory.STOCKAGE,
                ItemStatus.AVAILABLE, "external-data/qrcodes/STO-500.png");
        new ItemRepositoryImpl().insert(item);

        LocalDateTime now = LocalDateTime.now().withNano(0);
        Loan overdue = new Loan(500, item.getId(), borrowerId,
                now.minusDays(45), now.minusDays(10), null);
        new LoanRepositoryImpl().insert(overdue);

        assertEquals(1, loanService.load().size());
        assertEquals(1, loanService.getActiveLoans().size());
        assertEquals(1, loanService.getOverdueLoans().size());
        assertEquals(1, loanService.getLoansByDateRange(now.toLocalDate().minusDays(50), now.toLocalDate()).size());
    }
}
