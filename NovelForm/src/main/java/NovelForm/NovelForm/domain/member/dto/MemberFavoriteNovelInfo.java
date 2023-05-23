package NovelForm.NovelForm.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberFavoriteNovelInfo {

    @Schema(description = "해당 소설의 데이터베이스 번호")
    private Long novelId;

    @Schema(description = "소설 제목", defaultValue = "test title")
    private String title;

    @Schema(description = "이미지 링크", defaultValue = "https://img.xxx.net/cover")
    private String img_link;

    @Schema(description = "작가 명")
    private String authorName;

    @Schema(description = "해당 웹 소설 사이트 리뷰 수")
    private int review_cnt;

    @Schema(description = "해당 웹 소설 사이트 평점")
    private double reivew_rating;

    @Schema(description = "소설 장르")
    private String category;
}
