package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.novel.CustomNovelRepository;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.domain.novel.RecommendNovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendNovelRepository extends JpaRepository<RecommendNovel, Long> {


    @Query("select rn from RecommendNovel rn where rn.originNovel =:novel")
    List<RecommendNovel> findRecommendNovelByNovel(@Param("novel")Novel novel);


}
