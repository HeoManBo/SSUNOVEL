package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.Ridi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RidiRepository extends JpaRepository<Ridi, Long> {

}
