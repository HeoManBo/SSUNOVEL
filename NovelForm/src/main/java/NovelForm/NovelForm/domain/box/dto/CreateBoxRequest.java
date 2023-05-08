package NovelForm.NovelForm.domain.box.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBoxRequest {


    @Schema(description = "보관함 제목", defaultValue = "test 보관함")
    @NotEmpty
    private String title;       // 보관함 제목

    @Schema(description = "보관함 설명", defaultValue = "test 보관함 설명")
    @NotEmpty
    private String content;     // 보관함 설명

    @Schema(description = "개인용인지 공유용인지 체크 (1: 개인, 0: 공유)", defaultValue = "1")
    @NotNull
    private Integer is_private;     // 개인 저장 용인지 체크 (1: 개인, 0: 공유)

    @Schema(description = "보관함에 들어갈 작품들의 번호(novelId)", defaultValue = "[1, 2, 3, 4, 5]")
    @NotEmpty
    private List<Long> boxItems = new ArrayList<>();      // 보관함에 들어갈 작품들 리스트로 전달 받음 (Novel id값)

    @Schema(description = "보관함의 대표 작품의 번호(novelId)", defaultValue = "1")
    @NotNull
    private Long leadItemId;     // 대표 작품 번호

}
