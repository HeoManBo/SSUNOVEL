package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.novel.Novel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NovelRepository extends JpaRepository<Novel, Long> {


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


}
