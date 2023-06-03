package NovelForm.NovelForm.domain.box.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BoxInfoResponse {

    @Schema(description = "보관함 번호(id)", defaultValue = "1")
    private Long BoxId;

    @Schema(description = "보관함 이름", defaultValue = "test title")
    private String title;

    @Schema(description = "보관함 설명", defaultValue = "test content")
    private String content;

    @Schema(description = "보관함에 좋아요를 했는지", defaultValue = "false")
    private Boolean isLike;

    @Schema(description = "보관함에 즐겨찾기를 했는지", defaultValue = "false")
    private Boolean isFavorite;

    @Schema(description = "보관함 내 작품들", defaultValue = "[]")
    private List<BoxItemInfo> boxItemInfo;

}
