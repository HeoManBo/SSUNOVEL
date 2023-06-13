package NovelForm.NovelForm.repository;


import NovelForm.NovelForm.domain.favorite.domain.FavoriteNovel;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.member.dto.MemberFavoriteNovelInfo;
import NovelForm.NovelForm.domain.novel.Novel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteNovelRepository extends JpaRepository<FavoriteNovel, Long> {

    // 회원으로 즐겨찾기 정보 가져오기
    @Query("select fn from FavoriteNovel fn join fetch fn.novel where fn.member = :member")
    List<FavoriteNovel> findByMember(@Param("member") Member member, PageRequest pageRequest);


    // 회원과 소설 번호로 즐겨찾기 정보 가져오기
    @Query("select fn from FavoriteNovel fn join fetch fn.novel where fn.member = :member and fn.novel = :novel")
    FavoriteNovel findByMemberWithNovel(@Param("member") Member member, @Param("novel") Novel novel);


    @Query("select fn from FavoriteNovel fn inner join fn.novel inner join fn.member where fn.member = :member and fn.novel = :novel")
    List<FavoriteNovel> findByMemberAndNovel(@Param("member") Member member, @Param("novel") Novel novel);

    @Query("select fn from FavoriteNovel fn join fetch fn.novel where fn.member = :member")
    List<FavoriteNovel> findByMemberNoPaging(@Param("member") Member member);


    /**
     * 즐겨찾기 한 소설에 대한 정보를 가져온다.
     *
     * @param member
     * @param pageRequest
     * @return
     */
    @Query(value = "select new NovelForm.NovelForm.domain.member.dto.MemberFavoriteNovelInfo(" +
            " n.id, " +
            " n.title, " +
            " n.cover_image, " +
            " a.name, " +
            " cast(count(r) as int), " +
            " avg(r.rating), " +
            " n.category " +
            " ) " +
            " from FavoriteNovel fn " +
            " inner join fn.novel n " +
            " inner join n.author a " +
            " inner join n.reviews r " +
            " where fn.member = :member" +
            " group by fn ",

            countQuery = "select count(fn) " +
                    "       from FavoriteNovel fn " +
                    "       inner join fn.novel n " +
                    "       inner join n.author a " +
                    "       inner join n.reviews r " +
                    "       where fn.member = :member " +
                    "       group by fn"
    )
    Page<MemberFavoriteNovelInfo> findFavoriteNovelInfoByMember(@Param("member") Member member, PageRequest pageRequest);

    /**
     * 즐겨찾기한 소설의 개수를 가져온다.
     */
    Integer countFavoriteNovelByMember(Member member);
}
