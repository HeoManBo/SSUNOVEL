package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.community.CommunityPost;
import NovelForm.NovelForm.domain.community.dto.PostDto;
import NovelForm.NovelForm.domain.member.domain.Member;
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
    @Query(value = "select c from CommunityPost c join fetch c.member join fetch c.comments",
            countQuery = "select c from CommunityPost c inner join c.member inner join c.comments")
    Page<CommunityPost> findPostListWithPaging(Pageable pageable);


    /**
     * 게시글 상세 조회 기능입니다.
     */
    @Query("select c from CommunityPost c join fetch c.member where c.id = :post_id")
    Optional<CommunityPost> findDetailPost(@Param("post_id") Long post_id);

    /**
     * keyword가 포함되는 게시글 가져온다 -> 오래전 것부터 가져옴
     */
    @Query(value = "select c from CommunityPost c join fetch c.member where c.title like %:keyword% order by c.create_at ASC",
    countQuery = "select count(c) from CommunityPost c inner join c.member where c.title like %:keyword% order by c.create_at ASC")
    Page<CommunityPost> findCommunityPostWithKeywordASC(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "select c from CommunityPost c join fetch c.member where c.title like %:keyword% order by c.create_at DESC",
    countQuery = "select count(c) from CommunityPost c inner join c.member where c.title like %:keyword% order by c.create_at DESC")
    Page<CommunityPost> findCommunityPostWithKeywordDESC(@Param("keyword") String keyword, Pageable pageable);


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
    @Query("select new NovelForm.NovelForm.domain.community.dto.PostDto( " +
            " cp.id, " +
            " cp.title, " +
            " cp.content " +
            " ) from CommunityPost cp where cp.member = :member")
    List<PostDto> findPostByMember(@Param("member") Member member);

}
