package NovelForm.NovelForm.repository;



import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.box.dto.AllBoxResponse;
import NovelForm.NovelForm.domain.like.domain.Like;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("select count(l) from Like l " +
            "inner join l.box b on b.id = :boxId ")
    Long findLikeByBox(@Param("boxId") Long boxId);


    /**
     * 전체 목록 가져오기
     * 좋아요 오름차순을 기준으로 정렬
     *
     * @return
     */
    @Query(value = "select new NovelForm.NovelForm.domain.box.dto.AllBoxResponse( " +
            " b.title, " +
            " m.nickname, " +
            " (select n.cover_image from BoxItem bi2 inner join bi2.novel n where bi2.box = b and bi2.is_lead_item = 1), " +
            " b.id," +
            " b.update_at, " +
            " count(distinct bi), " +
            " count(distinct l)) " +
            " from Like l " +
            " inner join l.box b " +
            " inner join b.member m " +
            " inner join b.boxItems bi " +
            " where b.is_private = 0 " +
            " group by b " +
            " order by count(l) ",

            countQuery = " select count(distinct l) " +
                         " from Like l" +
                         " inner join l.box b " +
                         " inner join b.member m " +
                         " inner join b.boxItems bi " +
                         " where b.is_private = 0 " +
                         " group by b "
    )
    Page<AllBoxResponse> findAllBoxByPublicWithLike(PageRequest pageRequest);


    /**
     * 전체 목록 가져오기
     * 좋아요 오름차순을 기준으로 정렬
     *
     * @param pageRequest
     * @return
     */
    @Query(value = "select new NovelForm.NovelForm.domain.box.dto.AllBoxResponse( " +
            " b.title, " +
            " m.nickname, " +
            " (select n.cover_image from BoxItem bi2 inner join bi2.novel n where bi2.box = b and bi2.is_lead_item = 1), " +
            " b.id," +
            " b.update_at, " +
            " count(distinct bi), " +
            " count(distinct l)) " +
            " from Like l " +
            " inner join  l.box b " +
            " inner join b.member m " +
            " inner join b.boxItems bi " +
            " where b.is_private = 0 " +
            " group by b " +
            " order by count(l) DESC ",

            countQuery = " select count(distinct l) " +
                    " from Like l " +
                    " inner join l.box b " +
                    " inner join b.member m " +
                    " inner join b.boxItems bi " +
                    " where b.is_private = 0 " +
                    " group by b "
    )
    Page<AllBoxResponse> findAllBoxByPublicWithLikeDesc(PageRequest pageRequest);


    /**
     * 사용자가 특정 보관함에 대해 좋아요를 했는지 확인
     */
    List<Like> findLikesByMemberAndBox(Member member, Box box);


    /**
     * 사용자가 특정 리뷰에 대해 좋아요를 했는지 확인
     */
    List<Like> findLikesByMemberAndReview(Member member, Review review);


    /**
     * 특정 리뷰에 대해 좋아요가 몇개인지 확인
     */
    @Query("select count(l) from Like l inner join l.review r on r = :review")
    Integer findLikeCountByReview(Review review);


    /**
     * 특정 박스의 좋아요 삭제
     */
    @Modifying(clearAutomatically = true)
    void deleteAllByBox(Box box);
}
