package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.member.dto.MemberFavoriteNovelInfo;
import NovelForm.NovelForm.domain.novel.CustomNovelRepository;
import NovelForm.NovelForm.domain.novel.Novel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface NovelRepository extends JpaRepository<Novel, Long>, CustomNovelRepository {


    /**
     *
     * fetch join시 alise 를 이용하여 query시 반드시 조회용으로만 사용해야함.
     */
    // 소설명으로 검색
    @Query("select n from Novel n join fetch n.author where n.title like %:search% ")
    List<Novel> findByTitleName(@Param("search") String search);

    // 작가명으로 검색
    @Query("select n from Novel n join fetch n.author au where au.name like %:search%")
    List<Novel> findByAuthorName(@Param("search") String search);

    //장르명으로 조회
    @Query("select n from Novel n join fetch n.author where n.category = :category")
    List<Novel> findByCategory(@Param("category")String category);


    //검색시 페이징을 위한 쿼리
    //해당 쿼리는 파라미터로 넘어오는 검색어가 포함되는 소설을 검색하는 쿼리이다.
    //countQuery에서는 개수만 새면 되므로 작가의 정보는 필요가 없어 fetch join 대신 inner join을 사용한다.
    @Query(value = "select n from Novel n join fetch n.author where n.title like %:search%",
           countQuery = "select count(n) from Novel n inner join n.author where n.title like %:search%")
    Page<Novel> findByTitleWithPagingOrderByDownload_cntOrReview_cnt(@Param("search") String search, Pageable pageable);


    @Query(value = "select n from Novel n join fetch n.author where n.title like %:search% order by n.rating / n.review_cnt desc",
            countQuery = "select count(n) from Novel n inner join n.author where n.title like %:search% order by n.rating / n.review_cnt")
    Page<Novel> findByTitleWithPagingOrderByRating(@Param("search") String search, Pageable pageable);

    //검색시 검색어가 포함되는 소설의 전체 개수를 검색하는 쿼리이다.
    @Query("select count(n) from Novel n where n.title like %:search%")
    int countWithMatchingTitle(@Param("search") String search);

    //검색시 페이징을 위한 쿼리
    //해당 쿼리는 파라미터로 넘어오는 검색어와 일치하는 작가를 검색하는 쿼리이다.
    @Query(value = "select n from Novel n join fetch n.author au where au.name like %:search%",
            countQuery = "select count(n) from Novel n inner join n.author au where au.name like %:search%")
    Page<Novel> findByAuthorWithPagingOrderByDownload_cntOrReview_cnt(@Param("search") String search, Pageable pageable);

    @Query(value = "select n from Novel n join fetch n.author au where au.name like %:search% order by n.rating / n.review_cnt desc",
            countQuery = "select count(n) from Novel n inner join n.author au where au.name like %:search% order by  n.rating/ n.review_cnt")
    Page<Novel> findByAuthorWithPagingOrderByRating(@Param("search") String search, Pageable pageable);

    //검색시 검색어가 포함되는 작가의 전체 소설 개수를 검색하는 쿼리이다.
    //개수만 가져오면 되므로 fetch join으로 작가의 정보까지 가져올 필요는 없으므로 inner join을 사용한다.
    @Query("select count(n) from Novel n inner join n.author au where au.name like %:search%")
    int countWithMatchingAuthorName(@Param("search") String search);

    //소설 상세 조회시 각종 정보를 가져올 쿼리
    @Query(value = "select n from Novel n join fetch n.author au where n.id = :novelId")
    Novel DetailNovelInfo(@Param("novelId") Long id);


    //장르에 따른 필터링 조건에 맞춰 Novel List를 반환함
    @Query(value = "select n from Novel as n join fetch n.author au where n.category = :genre",
           countQuery = "select count(n) from Novel n inner join n.author au where n.category = :genre")
    Page<Novel> findByGenreWithFiltering(@Param("genre") String genre, Pageable pageable);


    //novel_id의 소설과, 해당 소설의 리뷰 List를 가져온다 (제거를 위해)
    @Query("select n from Novel n join fetch n.reviews rs join fetch rs.member where n.id = :novel_id")
    Novel findByNovelIdWithReviews(@Param("novel_id") Long novel_id);

    //Download_cnt 내림차순으로 정렬한 소설 20개를 반환합니다. 정렬 기준은 차후 변경될 수 있습니다.
    @Query("select n from Novel n join fetch n.author order by n.download_cnt desc limit 20")
    List<Novel> findByNovelRanking();



    @Query("select n from Novel n join fetch Author a where n.id = :novel_id")
    Optional<Novel> findNovelByIdWithAuthor(@Param("novel_id")Long novel_id);


    @Query("select n from Novel n join fetch n.author au where n.id in (:recoList)")
    List<Novel> findRecommendNovel(@Param("recoList") List<Long> recoList);


}
