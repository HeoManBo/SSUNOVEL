package NovelForm.NovelForm.repository;


import NovelForm.NovelForm.domain.favorite.domain.FavoriteNovel;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Novel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteNovelRepository extends JpaRepository<FavoriteNovel, Long> {

    @Query("select fn from FavoriteNovel fn join fetch fn.novel where fn.member = :member")
    List<FavoriteNovel> findByMember(@Param("member") Member member, PageRequest pageRequest);


    @Query("select fn from FavoriteNovel fn join fetch fn.novel where fn.member = :member and fn.novel = :novel")
    FavoriteNovel findByMemberWithNovel(@Param("member") Member member, @Param("novel") Novel novel);

    @Query("select fn from FavoriteNovel fn inner join fn.novel inner join fn.member where fn.member = :member and fn.novel = :novel")
    List<FavoriteNovel> findByMemberAndNovel(@Param("member") Member member, @Param("novel") Novel novel);

    @Query("select fn from FavoriteNovel fn join fetch fn.novel where fn.member = :member")
    List<FavoriteNovel> findByMemberNoPaging(@Param("member") Member member);
}
