package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.novel.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
