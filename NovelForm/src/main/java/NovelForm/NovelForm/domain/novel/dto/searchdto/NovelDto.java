package NovelForm.NovelForm.domain.novel.dto.searchdto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 소설 검색시 반환받을 NovelDto 입니다. (작가 포함)
 */
@Getter
public class NovelDto {


    @Schema(description = "이미지 링크", defaultValue = "https://img.xxx.net/cover")
    private String img_link;
    @Schema(description = "소설 제목", defaultValue = "test title")
    private String title;
    @Schema(description = "작가 명")
    private String authorName;
    @Schema(description = "해당 웹 소설 사이트 리뷰 수")
    private int review_cnt;
    @Schema(description = "해당 웹 소설 사이트 평점")
    private double reivew_rating;
    @Schema(description = "소설 장르")
    private String category;
    @Schema(description = "해당 소설의 데이터베이스 번호")
    private Long novelId;

    @Builder
    public NovelDto(String title, String authorName, String img_link, double reivew_rating, int review_cnt, String category,
                    Long novelId) {
        this.title = title;
        this.authorName = authorName;
        this.img_link = img_link;
        this.reivew_rating = reivew_rating;
        this.review_cnt = review_cnt;
        this.category = category;
        this.novelId = novelId;
    }

}
