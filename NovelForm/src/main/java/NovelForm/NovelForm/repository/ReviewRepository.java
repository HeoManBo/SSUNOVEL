package NovelForm.NovelForm.repository;


import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.review.domain.Review;
import NovelForm.NovelForm.domain.novel.Novel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    //리뷰 페이징이 필요한 경우
    @Query(value = "select r from Review r join fetch r.member where r.novel = :novel",
            countQuery = "select count(r) from Review r inner join  r.member where r.novel = :novel")
    Page<Review> findByReviewWithPaging(@Param("novel") Novel novel, Pageable pageable);

    //리뷰 전체를 가져오는 경우
    @Query(value = "select r from Review r join fetch r.member where r.novel = :novel")
    List<Review> findByReview(@Param("novel") Novel novel);

    //파라미터로 넘어오는 소설의 멤버의 리뷰를 조회함
    @Query(value = "select r from Review r join fetch r.member where r.novel = :novel and r.member = :member ")
    Optional<Review> findSingleReivew(@Param("member")Member member, @Param("novel")Novel novel);

    //리뷰 수정을 위한 쿼리 -> 해당 리뷰의 멤버와, 소설까지 가져옴
    @Query(value = "select r from Review r join fetch r.novel inner join r.member where r.novel = :novel and r.member = :member ")
    Optional<Review> modifyReview(@Param("member")Member member, @Param("novel")Novel novel);

    //리뷰 삭제
    @Modifying
    @Query(value = "delete from Review where review_id = :review_id", nativeQuery = true)
    void deleteReviewById(@Param("review_id") Long review_id);

}
