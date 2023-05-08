package NovelForm.NovelForm.domain.box.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AllBoxResponse {

    @Schema(description = "이미지 링크", defaultValue = "https://img.xxx.net/cover")
    private String imgSrc;      // 대표 이미지 링크

    @Schema(description = "보관함 이름", defaultValue = "test title")
    private String title;       // 보관함 이름

    @Schema(description = "보관함 생성자", defaultValue = "test creater")
    private String memberName;  // 보관함 생성자 이름

    @Schema(description = "보관함 번호(id)", defaultValue = "1")
    private Long boxId;         // 보관함 번호

}
