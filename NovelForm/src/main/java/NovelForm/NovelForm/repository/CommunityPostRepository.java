package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.community.CommunityPost;
import NovelForm.NovelForm.domain.community.dto.PostDto;
import NovelForm.NovelForm.domain.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {


    /**
     * 커뮤니티에 작성한 글 모두 삭제하기
     */
    @Modifying(clearAutomatically = true)
    //@Query("delete from CommunityPost cp where cp.member = :member")
    void deleteAllByMember(Member member);

    /**
     * 페이징 번호에 맞는 게시글 조회 최신순 내림차순으로 조회한다.
     */
    @Query(value = "select c from CommunityPost c join fetch c.member ",
            countQuery = "select c from CommunityPost c inner join c.member")
    Page<CommunityPost> findPostListWithPaging(Pageable pageable);


    /**
     * 게시글 상세 조회 기능입니다.
     */
    @Query("select c from CommunityPost c join fetch c.member where c.id = :post_id")
    Optional<CommunityPost> findDetailPost(@Param("post_id") Long post_id);

    /**
     * keyword가 포함되는 게시글 가져온다 -> 오래전 것부터 가져옴
     */
    @Query("select c from CommunityPost c join fetch c.member where c.title like %:keyword% order by c.create_at ASC")
    List<CommunityPost> findCommunityPostWithKeywordASC(@Param("keyword") String keyword);

    @Query("select c from CommunityPost c join fetch c.member where c.title like %:keyword% order by c.create_at DESC")
    List<CommunityPost> findCommunityPostWithKeywordDESC(@Param("keyword") String keyword);


    /**
     * 해당 멤버가 작성한 게시글을 가져옵니다.
     */
    @Query("select c from CommunityPost c where c.member = :member")
    List<CommunityPost> findMemberPost(@Param("member") Member member);

    /**
     * 해당 멤버가 작성한 모든 게시글을 삭제합니다.
     *
     */
    @Modifying
    @Query("delete from CommunityPost c where c.member = :member")
    void deleteAllPostByMember(@Param("member") Member member);


    /**
     * 마이페이지 작성글 조회시 가져올 내 작성글
     */
    @Query(value = "select new NovelForm.NovelForm.domain.community.dto.PostDto( " +
            " cp.id, " +
            " cp.title, " +
            " cp.content," +
            " cp.create_at " +
            " ) from CommunityPost cp where cp.member = :member",

            countQuery = "select count(cp) from CommunityPost cp where cp.member = :member"
    )
    Page<PostDto> findPostByMember(@Param("member") Member member, PageRequest pageRequest);


    /**
     * 특정 회원이 작성한 작성 글 개수 가져오기
     */
    @Query("select count(cp) from CommunityPost cp where cp.member = :member")
    Integer findPostCountByMember(@Param("member") Member member);
}
