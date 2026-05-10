package be.esi.prj.repository;

import be.esi.prj.model.Borrower;
import be.esi.prj.model.Item;
import be.esi.prj.model.ItemCategory;
import be.esi.prj.model.ItemStatus;
import be.esi.prj.model.Loan;
import be.esi.prj.testsupport.TestDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryIntegrationTest {

    private BorrowerRepositoryImpl borrowerRepository;
    private ItemRepositoryImpl itemRepository;
    private LoanRepositoryImpl loanRepository;

    @BeforeEach
    void setUp() {
        TestDatabase.reset();
        borrowerRepository = new BorrowerRepositoryImpl();
        itemRepository = new ItemRepositoryImpl();
        loanRepository = new LoanRepositoryImpl();
    }

    @Test
    void borrowerRepositorySupportsCrudAndSearch() {
        Borrower ada = new Borrower("Ada", "Lovelace", "ada@example.com");
        Borrower grace = new Borrower("Grace", "Hopper", "grace@example.com");

        int adaId = borrowerRepository.insert(ada);
        int graceId = borrowerRepository.insert(grace);
        ada.setId(adaId);
        grace.setId(graceId);

        assertTrue(adaId > 0);
        assertEquals(2, borrowerRepository.findAll().size());
        assertEquals("Ada", borrowerRepository.findById(adaId).getFirstName());
        assertNull(borrowerRepository.findById(999_999));

        List<Borrower> byName = borrowerRepository.findByNameOrSurname("Hopp");
        assertEquals(1, byName.size());
        assertEquals(graceId, byName.getFirst().getId());

        ada.setEmail("ada@esi.be");
        borrowerRepository.updateEmail(ada);
        assertEquals("ada@esi.be", borrowerRepository.findById(adaId).getEmail());

        borrowerRepository.delete(graceId);
        assertNull(borrowerRepository.findById(graceId));
        assertEquals(1, borrowerRepository.findAll().size());
    }

    @Test
    void itemRepositorySupportsLookupFilteringUpdateAndDelete() {
        Item router = new Item("RES-900", "Routeur", ItemCategory.RESEAU,
                ItemStatus.AVAILABLE, "external-data/qrcodes/RES-900.png");
        Item keyboard = new Item("PER-900", "Clavier", ItemCategory.PERIPHERIQUE,
                ItemStatus.MAINTENANCE, "external-data/qrcodes/PER-900.png");
        Item cable = new Item("CAB-900", "Cable HDMI", ItemCategory.CABLAGE,
                ItemStatus.AVAILABLE, "external-data/qrcodes/CAB-900.png");

        itemRepository.insert(router);
        itemRepository.insert(keyboard);
        itemRepository.insert(cable);

        assertEquals(3, itemRepository.findAll().size());
        assertEquals("Routeur", itemRepository.findById("RES-900").getName());
        assertNull(itemRepository.findById("ABSENT"));
        assertEquals(List.of(router.getId()), itemRepository.searchByName("out").stream().map(Item::getId).toList());
        assertEquals(1, itemRepository.findByCategory(ItemCategory.RESEAU).size());
        assertEquals(2, itemRepository.findByStatus(ItemStatus.AVAILABLE).size());
        assertEquals(1, itemRepository.findByCategoryAndStatus(ItemCategory.CABLAGE, ItemStatus.AVAILABLE).size());

        router.setCategory(ItemCategory.INDUSTRIEL);
        router.setStatus(ItemStatus.UNAVAILABLE);
        itemRepository.update(router);

        Item updated = itemRepository.findById("RES-900");
        assertEquals(ItemCategory.INDUSTRIEL, updated.getCategory());
        assertEquals(ItemStatus.UNAVAILABLE, updated.getStatus());

        itemRepository.delete("PER-900");
        assertNull(itemRepository.findById("PER-900"));
        assertEquals(2, itemRepository.findAll().size());
    }

    @Test
    void loanRepositorySupportsAllFinderVariantsAndUpdate() {
        int borrowerOne = borrowerRepository.insert(new Borrower("Nadia", "Comaneci", "nadia@example.com"));
        int borrowerTwo = borrowerRepository.insert(new Borrower("Simone", "Biles", "simone@example.com"));

        itemRepository.insert(new Item("MES-100", "Multimetre", ItemCategory.MESURE,
                ItemStatus.AVAILABLE, "external-data/qrcodes/MES-100.png"));
        itemRepository.insert(new Item("ELE-100", "Arduino", ItemCategory.ELECTRONIQUE,
                ItemStatus.AVAILABLE, "external-data/qrcodes/ELE-100.png"));

        LocalDateTime now = LocalDateTime.now().withNano(0);
        Loan active = new Loan(101, "MES-100", borrowerOne,
                now.minusDays(1), now.plusDays(20), null);
        Loan overdue = new Loan(102, "ELE-100", borrowerOne,
                now.minusDays(10), now.minusDays(1), null);
        Loan returned = new Loan(103, "MES-100", borrowerTwo,
                now.minusDays(40), now.minusDays(10), now.minusDays(9));

        loanRepository.insert(active);
        loanRepository.insert(overdue);
        loanRepository.insert(returned);

        assertEquals(3, loanRepository.findAll().size());
        assertEquals("MES-100", loanRepository.findById(101).getItemId());
        assertNull(loanRepository.findById(999_999));
        assertEquals(2, loanRepository.findByBorrowerId(borrowerOne).size());
        assertEquals(2, loanRepository.findRecentLoansByBorrower(borrowerOne).size());
        assertEquals(2, loanRepository.findByItemId("MES-100").size());
        assertEquals(2, loanRepository.findActiveLoans().size());
        assertEquals(2, loanRepository.findActiveLoansByBorrower(borrowerOne).size());
        assertEquals(List.of(102), loanRepository.findOverdueLoans().stream().map(Loan::getId).toList());

        LocalDate today = now.toLocalDate();
        assertEquals(2, loanRepository.findLoansByDate(today.minusDays(10), today).size());

        active.extendDueDate();
        active.setReturnDate(now);
        loanRepository.update(active);

        Loan updated = loanRepository.findById(101);
        assertEquals(active.getDueDate(), updated.getDueDate());
        assertEquals(now, updated.getReturnDate());
    }
}
