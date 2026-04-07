package be.esi.prj.repository;

import be.esi.prj.model.Borrower;
import java.util.List;

public interface BorrowerRepository {
    List<Borrower> findAll();
    Borrower findById(int id);
    List<Borrower> findByNameOrSurname(String keyword);
    void insert(Borrower borrower);
    void update(Borrower borrower);
    void delete(int id);
}