package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.novel.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("select au from Author au where au.name = :authorName")
    public Author findByAuthorName(@Param("authorName") String authorName);
}
