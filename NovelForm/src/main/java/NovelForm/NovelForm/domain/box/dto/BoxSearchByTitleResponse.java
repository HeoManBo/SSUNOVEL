package NovelForm.NovelForm.domain.box.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
@AllArgsConstructor
public class BoxSearchByTitleResponse {

    @Schema(description = "보관함 수(이름 검색)")
    private int boxCntByTitle;

    @Schema(description = "보관함 이름 기반 검색 목록")
    private List<BoxSearchInfo> searchByTitle = new ArrayList<>();
}
