package NovelForm.NovelForm.domain.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 게시글 조회시 반환할 DTO
 */
@Data
public class PostDto {

    @Schema(description = "게시글 번호")
    private Long postId; //게시글 번호
    @Schema(description = "게시글 제목")
    private String title; //제목
    @Schema(description = "작성자 이름")
    private String nickName; //작성한 사람 이름
    @Schema(description = "작성 시간")
    private LocalDateTime writeAt;

    public PostDto(Long postId, String title, String nickName, LocalDateTime write_at) {
        this.postId = postId;
        this.title = title;
        this.nickName = nickName;
        this.writeAt = write_at;
    }
}
