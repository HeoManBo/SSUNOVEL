package NovelForm.NovelForm.domain.box.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AllBoxAndCntResponse {

    @Schema(description = "보관함 개수")
    Integer boxCnt;

    @Schema(description = "보관함 목록")
    List<AllBoxResponse> allBoxResponses;
}
