package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {


}
