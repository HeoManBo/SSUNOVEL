package NovelForm.NovelForm.repository;


import NovelForm.NovelForm.domain.member.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
