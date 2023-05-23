package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.comment.Comment;
import NovelForm.NovelForm.domain.community.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    //해당 댓글 작성자의 멤버와 작성된 게시글을 join fetch로 가져온다.
    @Query("select c from Comment c join fetch c.member join fetch c.communityPost where c.id = :comment_id")
    Optional<Comment> findCommentByIdWithMemberAndPost(@Param("comment_id")Long comment_id);


    @Modifying
    @Query("delete from Comment c where c.communityPost = :communityPost")
    void deleteWithPost(@Param("communityPost") CommunityPost communityPost);
}
