package NovelForm.NovelForm.domain.community.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 댓글 작성시 넘어와야할 Dto
 */
@NoArgsConstructor
@Data
public class CreateCommentDto {

    @Schema(description = "댓글 내용")
    @NotBlank
    private String content;


}
