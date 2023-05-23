package NovelForm.NovelForm.domain.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PostDto {

    @Schema(description = "게시글 번호")
    private Long postId; //게시글 번호
    @Schema(description = "게시글 제목")
    private String title; //제목
    @Schema(description = "작성자 이름")
    private String nickName; //작성한 사람 이름

    public PostDto(Long postId, String title, String nickName) {
        this.postId = postId;
        this.title = title;
        this.nickName = nickName;
    }
}