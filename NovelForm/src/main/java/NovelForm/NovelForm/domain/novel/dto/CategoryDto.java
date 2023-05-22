package NovelForm.NovelForm.domain.novel.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CategoryDto {
    @Schema(description = "완결 여부, 값이 1이라면 완결인 소설만 보여줍니다.")
    @Min(0)
    @Max(1)
    private int isFinished;
    @Schema(description = "플랫폼 이름, 미 클릭시 None 값, 영어(is_kakao,is_naver,is_munpia,is_ridi)로 전달")
    private String platform;
    @Schema(description = "장르 이름, 미 클릭시 None 값, 한국어(현판,로판,드라마 등)으로 전달")
    private String genre;

    @Schema(description = "정렬 기준, 기본 값 : 인기순 (download_cnt : 웹 사이트 리뷰 개수순) ")
    private String orderBy;

    @Schema(description = "페이지 번호입니다 0번부터 시작")
    @Min(0)
    private int pageNum;
}
