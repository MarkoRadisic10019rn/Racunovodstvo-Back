package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.raf.demo.model.Konto;

@Repository
public interface KontoRepository extends JpaRepository<Konto, Long> {
}
