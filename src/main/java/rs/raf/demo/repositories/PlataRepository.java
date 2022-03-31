package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.raf.demo.model.Plata;

@Repository
public interface PlataRepository extends JpaRepository<Plata, Long> {
}
