package NovelForm.NovelForm.domain.community.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.SessionAttribute;

@Getter
@Setter
@NoArgsConstructor
public class WriteDto {

    @Schema(description = "게시글 제목")
    @NotBlank(message = "제목은 빈칸일 수 없습니다.")
    private String title;

    @Schema(description = "게시글 내용")
    @NotBlank(message = "내용은 빈칸일 수 없습니다.")
    private String content;

    public WriteDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
