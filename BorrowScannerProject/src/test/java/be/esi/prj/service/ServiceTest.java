package be.esi.prj.service;

import be.esi.prj.model.*;
import be.esi.prj.repository.*;
import be.esi.prj.testsupport.TestDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    // =====================================================
    // BORROWER SERVICE
    // =====================================================

    @Test
    void borrowerServiceLoadsAddsFindsUpdatesAndDeletesBorrowers() {

        assertTrue(borrowerService.load().isEmpty());

        Borrower borrower = borrowerService.addBorrower("Curie", "Marie", "marie@example.com");

        assertTrue(borrower.getId() > 0);
        assertEquals(1, borrowerService.load().size());
        assertEquals(borrower.getId(), borrowerService.getBorrowerById(borrower.getId()).getId());

        borrowerService.updateBorrowerEmail(borrower.getId(), "new@example.com");
        assertEquals("NEW@EXAMPLE.COM",
                borrowerService.getBorrowerById(borrower.getId()).getEmail());

        borrowerService.deleteBorrower(borrower.getId());
        assertTrue(borrowerService.load().isEmpty());
    }

    @Test
    void borrowerServiceRejectsInvalidData() {

        assertThrows(IllegalArgumentException.class,
                () -> borrowerService.addBorrower("", "Marie", "marie@example.com"));

        assertThrows(IllegalArgumentException.class,
                () -> borrowerService.addBorrower("Curie", "", "marie@example.com"));

        assertThrows(IllegalArgumentException.class,
                () -> borrowerService.addBorrower("Curie", "Marie", "bad-email"));
    }

    // =====================================================
    // ITEM SERVICE
    // =====================================================

    @Test
    void itemServiceAddsUpdatesLoadsFindsAndDeletesItems() {

        Item toCreate = new Item(
                null,
                "Oscilloscope",
                ItemCategory.MESURE,
                ItemStatus.AVAILABLE,
                null
        );

        Item added = itemService.addItem(toCreate);

        assertNotNull(added.getId());
        assertTrue(added.getId().startsWith("MES-"));

        assertEquals("Oscilloscope", added.getName());
        assertEquals(ItemCategory.MESURE, added.getCategory());
        assertEquals(ItemStatus.AVAILABLE, added.getStatus());

        assertEquals(1, itemService.getItemsObservable().size());
        assertEquals(added.getId(), itemService.getItemById(added.getId()).getId());

        // update
        added.setCategory(ItemCategory.ELECTRONIQUE);
        added.setStatus(ItemStatus.MAINTENANCE);

        itemService.updateItem(added);

        Item persisted = itemService.getAllItems().stream()
                .filter(i -> i.getId().equals(added.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(ItemCategory.ELECTRONIQUE, persisted.getCategory());
        assertEquals(ItemStatus.MAINTENANCE, persisted.getStatus());

        // delete
        itemService.deleteItem(added.getId());

        assertTrue(itemService.getItemsObservable().isEmpty());
    }

    @Test
    void itemServiceRejectsInvalidItems() {

        Item invalidItem1 = new Item(null, "", ItemCategory.AUTRE, ItemStatus.AVAILABLE, null);
        assertThrows(IllegalArgumentException.class, () -> itemService.addItem(invalidItem1));

        Item invalidItem2 = new Item(null, "Nom", null, ItemStatus.AVAILABLE, null);
        assertThrows(IllegalArgumentException.class, () -> itemService.addItem(invalidItem2));
    }

    @Test
    void itemServiceContinuesIdsAfterPersistedItems() {

        ItemRepositoryImpl repo = new ItemRepositoryImpl();

        repo.insert(new Item(
                "CAB-003",
                "Cable HDMI",
                ItemCategory.CABLAGE,
                ItemStatus.AVAILABLE,
                "external-data/qrcodes/CAB-003.png"
        ));

        Item added = itemService.addItem(
                new Item(null, "USB", ItemCategory.CABLAGE, ItemStatus.AVAILABLE, null)
        );

        assertEquals("CAB-004", added.getId());
    }

    // =====================================================
    // LOAN SERVICE
    // =====================================================

    @Test
    void loanServiceCreatesReturnsExtendsAndCountsLoans() {

        int borrowerId = new BorrowerRepositoryImpl().insert(
                new Borrower("Turing", "Alan", "alan@example.com")
        );

        Item item = itemService.addItem(
                new Item(null, "Raspberry", ItemCategory.ELECTRONIQUE, ItemStatus.AVAILABLE, null)
        );

        Loan loan = new Loan(item.getId(), borrowerId);

        loanService.createLoan(loan);

        assertEquals(ItemStatus.UNAVAILABLE,
                itemService.getItemById(item.getId()).getStatus());

        assertEquals(1, loanService.getLoansObservable().size());

        loanService.returnLoan(loan.getId());

        assertEquals(ItemStatus.AVAILABLE,
                itemService.getItemById(item.getId()).getStatus());
    }

    @Test

    void loanServiceRejectsInvalidLoanScenarios() {
        int borrowerId = new BorrowerRepositoryImpl().insert(
                new Borrower("Noether", "Emmy", "emmy@example.com")
        );

        Item unavailable = itemService.addItem(
                new Item(null, "Switch", ItemCategory.RESEAU, ItemStatus.MAINTENANCE, null)
        );
        Loan invalidLoan = new Loan(unavailable.getId(), borrowerId);

        assertThrows(IllegalStateException.class, () -> loanService.createLoan(invalidLoan));
        assertThrows(IllegalArgumentException.class, () -> loanService.extendLoan(999_999));
        assertThrows(IllegalStateException.class, () -> loanService.returnLoan(999_999));
    }

    @Test
    void loanServiceStatistics() {

        int borrowerId = new BorrowerRepositoryImpl().insert(
                new Borrower("Meitner", "Lise", "lise@example.com")
        );

        Item item = itemService.addItem(
                new Item(null, "SSD", ItemCategory.STOCKAGE, ItemStatus.AVAILABLE, null)
        );

        Loan loan = new Loan(item.getId(), borrowerId);
        loanService.createLoan(loan);

        assertEquals(1, loanService.load().size());
        assertEquals(1, loanService.getActiveLoans().size());
    }
}