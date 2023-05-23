package NovelForm.NovelForm.domain.community.dto;


import NovelForm.NovelForm.domain.community.domain.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 상세 조회시 댓글 리스트에 담길 값 Dto
 */
@Data
public class CommentDto {
    @Schema(description = "작성자의 닉네임")
    private String nickname;
    @Schema(description = "댓글 내용")
    private String content;
    @Schema(description = "작성자 id")
    private Long memberId;
    @Schema(description = "댓글 id")
    private Long commentId;


    public CommentDto(Comment comment) {
        this.nickname = comment.getMember().getNickname();
        this.content = comment.getContent();
        this.memberId = comment.getMember().getId();
        this.commentId = comment.getId();
    }
}
