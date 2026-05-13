package be.esi.prj.repository;

import be.esi.prj.model.*;
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

    // =====================================================
    // BORROWER
    // =====================================================
    @Test
    void borrowerRepositorySupportsCrud() {
        Borrower ada = new Borrower("Ada", "Lovelace", "ada@example.com");

        int id = borrowerRepository.insert(ada);
        ada.setId(id);

        assertTrue(id > 0);
        assertEquals(1, borrowerRepository.findAll().size());
        assertEquals("Ada", findBorrower(id).orElseThrow().getFirstName());

        ada.setEmail("ada@esi.be");
        borrowerRepository.updateEmail(ada);
        assertEquals("ada@esi.be", findBorrower(id).orElseThrow().getEmail());

        borrowerRepository.delete(id);
        assertTrue(findBorrower(id).isEmpty());
    }

    // =====================================================
    // ITEM
    // =====================================================
    @Test
    void itemRepositorySupportsCrud() {

        Item router = new Item("RES-900", "Routeur", ItemCategory.RESEAU,
                ItemStatus.AVAILABLE, "external-data/qrcodes/RES-900.png");

        Item keyboard = new Item("PER-900", "Clavier", ItemCategory.PERIPHERIQUE,
                ItemStatus.MAINTENANCE, "external-data/qrcodes/PER-900.png");

        itemRepository.insert(router);
        itemRepository.insert(keyboard);

        assertEquals(2, itemRepository.findAll().size());
        assertEquals("Routeur", findItem("RES-900").orElseThrow().getName());

        router.setStatus(ItemStatus.UNAVAILABLE);
        itemRepository.update(router);

        assertEquals(ItemStatus.UNAVAILABLE,
                findItem("RES-900").orElseThrow().getStatus());

        itemRepository.delete("PER-900");
        assertTrue(findItem("PER-900").isEmpty());
        assertEquals(1, itemRepository.findAll().size());
    }

    // =====================================================
    // LOAN
    // =====================================================
    @Test
    void loanRepositorySupportsFindersAndUpdate() {

        int b1 = borrowerRepository.insert(new Borrower("Nadia", "Coman", "nadia@example.com"));
        int b2 = borrowerRepository.insert(new Borrower("Simone", "Biles", "simone@example.com"));

        itemRepository.insert(new Item("MES-100", "Multimètre", ItemCategory.MESURE,
                ItemStatus.AVAILABLE, "external-data/qrcodes/MES-100.png"));

        LocalDateTime now = LocalDateTime.now().withNano(0);

        Loan active = new Loan(101, "MES-100", b1,
                now.minusDays(1), now.plusDays(20), null);

        Loan overdue = new Loan(102, "MES-100", b1,
                now.minusDays(10), now.minusDays(1), null);

        Loan returned = new Loan(103, "MES-100", b2,
                now.minusDays(40), now.minusDays(10), now.minusDays(9));

        loanRepository.insert(active);
        loanRepository.insert(overdue);
        loanRepository.insert(returned);

        assertEquals(3, loanRepository.findAll().size());

        assertEquals(2,
                loanRepository.findAll().stream()
                        .filter(l -> l.getBorrowerId() == b1)
                        .count());

        assertEquals(2, loanRepository.findActiveLoans().size());

        assertEquals(102,
                loanRepository.findOverdueLoans().stream()
                        .findFirst()
                        .orElseThrow()
                        .getId());

        LocalDate today = now.toLocalDate();
        assertEquals(3,
                loanRepository.findLoansByDate(today.minusDays(50), today).size());

        active.extendDueDate();
        active.setReturnDate(now);
        loanRepository.update(active);

        Loan updated = findLoan(101).orElseThrow();
        assertEquals(active.getDueDate(), updated.getDueDate());
        assertEquals(now, updated.getReturnDate());
    }

    // =====================================================
    // HELPERS
    // =====================================================
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