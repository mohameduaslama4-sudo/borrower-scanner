package be.esi.prj.repository;

import be.esi.prj.model.Borrower;
import java.util.List;

public interface BorrowerRepository {
    List<Borrower> findAll();
    Borrower findById(int id);
    List<Borrower> findByNameOrSurname(String keyword);
    int insert(Borrower borrower);
    void updateEmail(Borrower borrower);
    void delete(int id);
}