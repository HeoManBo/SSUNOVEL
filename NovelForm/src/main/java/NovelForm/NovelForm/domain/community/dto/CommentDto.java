package NovelForm.NovelForm.domain.community.dto;


import NovelForm.NovelForm.domain.comment.Comment;
import NovelForm.NovelForm.domain.member.domain.Member;
import lombok.Data;

@Data
public class CommentDto {
    private String nickname;
    private String content;


    public CommentDto(Comment comment) {
        this.nickname = comment.getMember().getNickname();
        this.content = comment.getContent();
    }
}
