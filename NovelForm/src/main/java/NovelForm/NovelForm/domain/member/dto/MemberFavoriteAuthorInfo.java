package NovelForm.NovelForm.domain.member.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberFavoriteAuthorInfo {

    @Schema(description = "작가 번호")
    private Long id;

    @Schema(description = "작가 이름")
    private String name;
    
    @Schema(description = "작가의 가장 인기 많은 작품의 이미지")
    private String imgSrc;
    
    @Schema(description = "작가의 가장 인기 많은 작품의 이름")
    private String title;

}
