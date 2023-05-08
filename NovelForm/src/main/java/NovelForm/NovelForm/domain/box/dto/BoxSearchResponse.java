package NovelForm.NovelForm.domain.box.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
@AllArgsConstructor
public class BoxSearchResponse {

    @Schema(description = "보관함 이름 기반 검색 목록")
    private List<BoxSearchInfo> searchByTitle = new ArrayList<>();

    @Schema(description = "보관함 생성자 기반 검색 목록")
    private List<BoxSearchInfo> searchByMember = new ArrayList<>();

}
