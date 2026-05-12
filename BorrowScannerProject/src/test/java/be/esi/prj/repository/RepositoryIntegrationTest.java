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
import java.util.Optional;

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
    void borrowerRepositorySupportsCrudWithAvailableMethods() {
        Borrower ada = new Borrower("Ada", "Lovelace", "ada@example.com");
        Borrower grace = new Borrower("Grace", "Hopper", "grace@example.com");

        int adaId = borrowerRepository.insert(ada);
        int graceId = borrowerRepository.insert(grace);
        ada.setId(adaId);
        grace.setId(graceId);

        assertTrue(adaId > 0);
        assertEquals(2, borrowerRepository.findAll().size());
        assertEquals("Ada", findBorrower(adaId).orElseThrow().getFirstName());
        assertTrue(findBorrower(999_999).isEmpty());
        assertTrue(borrowerRepository.findAll().stream()
                .anyMatch(b -> b.getLastName().contains("Hopp") && b.getId() == graceId));

        ada.setEmail("ada@esi.be");
        borrowerRepository.updateEmail(ada);
        assertEquals("ada@esi.be", findBorrower(adaId).orElseThrow().getEmail());

        borrowerRepository.delete(graceId);
        assertTrue(findBorrower(graceId).isEmpty());
        assertEquals(1, borrowerRepository.findAll().size());
    }

    @Test
    void itemRepositorySupportsCrudWithAvailableMethods() {
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
        assertEquals("Routeur", findItem("RES-900").orElseThrow().getName());
        assertTrue(findItem("ABSENT").isEmpty());
        assertEquals(1, itemRepository.findAll().stream()
                .filter(i -> i.getName().contains("out"))
                .count());
        assertEquals(1, itemRepository.findAll().stream()
                .filter(i -> i.getCategory() == ItemCategory.RESEAU)
                .count());
        assertEquals(2, itemRepository.findAll().stream()
                .filter(i -> i.getStatus() == ItemStatus.AVAILABLE)
                .count());
        assertEquals(1, itemRepository.findAll().stream()
                .filter(i -> i.getCategory() == ItemCategory.CABLAGE
                        && i.getStatus() == ItemStatus.AVAILABLE)
                .count());

        router.setCategory(ItemCategory.INDUSTRIEL);
        router.setStatus(ItemStatus.UNAVAILABLE);
        itemRepository.update(router);

        Item updated = findItem("RES-900").orElseThrow();
        assertEquals(ItemCategory.INDUSTRIEL, updated.getCategory());
        assertEquals(ItemStatus.UNAVAILABLE, updated.getStatus());

        itemRepository.delete("PER-900");
        assertTrue(findItem("PER-900").isEmpty());
        assertEquals(2, itemRepository.findAll().size());
    }

    @Test
    void loanRepositorySupportsAvailableFinderVariantsAndUpdate() {
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
        assertEquals("MES-100", findLoan(101).orElseThrow().getItemId());
        assertTrue(findLoan(999_999).isEmpty());
        assertEquals(2, loanRepository.findAll().stream()
                .filter(l -> l.getBorrowerId() == borrowerOne)
                .count());
        assertEquals(2, loanRepository.findAll().stream()
                .filter(l -> l.getItemId().equals("MES-100"))
                .count());
        assertEquals(2, loanRepository.findActiveLoans().size());
        assertEquals(2, loanRepository.findActiveLoans().stream()
                .filter(l -> l.getBorrowerId() == borrowerOne)
                .count());
        assertEquals(102, loanRepository.findOverdueLoans().getFirst().getId());

        LocalDate today = now.toLocalDate();
        assertEquals(2, loanRepository.findLoansByDate(today.minusDays(10), today).size());

        active.extendDueDate();
        active.setReturnDate(now);
        loanRepository.update(active);

        Loan updated = findLoan(101).orElseThrow();
        assertEquals(active.getDueDate(), updated.getDueDate());
        assertEquals(now, updated.getReturnDate());
    }

    private Optional<Borrower> findBorrower(int id) {
        return borrowerRepository.findAll().stream()
                .filter(b -> b.getId() == id)
                .findFirst();
    }

    private Optional<Item> findItem(String id) {
        return itemRepository.findAll().stream()
                .filter(i -> i.getId().equals(id))
                .findFirst();
    }

    private Optional<Loan> findLoan(int id) {
        return loanRepository.findAll().stream()
                .filter(l -> l.getId() == id)
                .findFirst();
    }
}
