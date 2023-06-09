package NovelForm.NovelForm.domain.box.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class BoxSearchByCreatorResponse {

    @Schema(description = "보관함 수(생성자 검색)")
    private int boxCntByMember;
    @Schema(description = "보관함 생성자 기반 검색 목록")
    private List<BoxSearchInfo> searchByMember = new ArrayList<>();

}
