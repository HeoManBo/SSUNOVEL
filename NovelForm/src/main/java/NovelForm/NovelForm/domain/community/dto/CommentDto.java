package NovelForm.NovelForm.domain.community.dto;


import NovelForm.NovelForm.domain.comment.Comment;
import NovelForm.NovelForm.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CommentDto {
    @Schema(description = "작성자의 닉네임")
    private String nickname;
    @Schema(description = "댓글 내용")
    private String content;


    public CommentDto(Comment comment) {
        this.nickname = comment.getMember().getNickname();
        this.content = comment.getContent();
    }
}
