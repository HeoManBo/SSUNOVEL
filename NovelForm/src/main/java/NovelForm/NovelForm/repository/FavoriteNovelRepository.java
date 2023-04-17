package NovelForm.NovelForm.repository;


import NovelForm.NovelForm.domain.favorite.FavoriteNovel;
import NovelForm.NovelForm.domain.member.Member;
import NovelForm.NovelForm.domain.novel.Novel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteNovelRepository extends JpaRepository<FavoriteNovel, Long> {

    @Query("select fn from FavoriteNovel fn join fetch fn.novel where fn.member = :member")
    List<FavoriteNovel> findByMember(@Param("member") Member member);



}
