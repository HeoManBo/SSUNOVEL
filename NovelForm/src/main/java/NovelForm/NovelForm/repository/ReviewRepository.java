package NovelForm.NovelForm.repository;


import NovelForm.NovelForm.domain.member.domain.Review;
import NovelForm.NovelForm.domain.novel.Novel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(value = "select r from Review r join fetch r.member where r.novel = :novel",
            countQuery = "select count(r) from Review r inner join  r.member where r.novel = :novel")
    Page<Review> findByReview(@Param("novel") Novel novel, Pageable pageable);
}
