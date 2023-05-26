package NovelForm.NovelForm.domain.novel.dto.reivewdto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 리뷰 페이지
 * 장르별 베스트 리뷰를 반환할 Dto 입니다.
 */
@Data
@NoArgsConstructor
public class BestReviewDto {

    @Schema(description = "소설 ID")
    private Long id;

    @Schema(description = "소설 제목")
    private String title;

    @Schema(description = "작가 명")
    private String name;

    @Schema(description = "평점")
    private Double rating;

    @Schema(description = "소설 이미지 url")
    private String image_url;

    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "리뷰 작성자 닉네임")
    private String nickName;

    @Schema(description = "리뷰 좋아요 수")
    private int like_count;

    @Builder
    public BestReviewDto(Long id, String title, String name, Double rating, String image_url, String content, String nickName, Long like_count) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.rating = rating;
        this.image_url = image_url;
        this.content = content;
        this.nickName = nickName;
        this.like_count = like_count.intValue();
    }


}
