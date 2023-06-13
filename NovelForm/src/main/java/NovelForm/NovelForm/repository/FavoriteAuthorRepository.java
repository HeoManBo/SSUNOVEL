package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.favorite.domain.FavoriteAuthor;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.member.dto.MemberFavoriteAuthorInfo;
import NovelForm.NovelForm.domain.novel.Author;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FavoriteAuthorRepository extends JpaRepository<FavoriteAuthor, Long> {


    /**
     *  회원 객체와 page 정보를 가지고 즐겨찾기 목록을 가져오기
     * 
     * @param member
     * @param pageRequest
     * @return
     */
    @Query("select fa from FavoriteAuthor fa join fetch fa.author where fa.member =:member")
    List<FavoriteAuthor> findByMember(@Param("member") Member member, PageRequest pageRequest);


    /**
     * 회원 객체와 작가 객체를 가지고 특정 즐겨찾기 하나를 가져오기
     * 
     * @param member
     * @param author
     * @return
     */
    @Query("select fa from FavoriteAuthor fa join fetch fa.author where fa.member =:member and fa.author =:author")
    FavoriteAuthor findByMemberWithAuthor(@Param("member") Member member, @Param("author") Author author);


    /**
     * memberId로 해당 회원이 작성한 모든 즐겨찾기 목록을 가져오기
     *
     */
    List<FavoriteAuthor> findFavoriteAuthorsByMemberId(Long memberId);

    /**
     *  해당 작가를 좋아요를 누른 멤버의 리스트를 가져옴
     */
    @Query("select fa from FavoriteAuthor fa join fetch fa.member m where fa.author = :author")
    List<FavoriteAuthor> findByMemberWithLikeAuthor(@Param("author") Author author);

    /**
     * 상세 조회 페이지에서 해당 작가 좋아요를 로그인한 유저가 좋아요를 눌렀는지 확인함
     */
    @Query("select fa from FavoriteAuthor fa where fa.member = :member and fa.author = :author")
    FavoriteAuthor IsLikeAlreadyAuthor(@Param("member") Member member, @Param("author") Author author);

    /**
     * 본인이 즐겨찾기한 작가의 개수 구하기
     */
    Integer countFavoriteAuthorByMember(Member member);
}
