package NovelForm.NovelForm.domain.box.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoxItemInfo {

    @Schema(description = "소설 번호(id)", defaultValue = "1")
    private Long novelId;           // 소설 id

    @Schema(description = "소설 표지 이미지", defaultValue = "https://img.xxx.net/cover")
    private String novelImage;      // 소설 표지 이미지

    @Schema(description = "소설 장르", defaultValue = "판타지")
    private String genre;           // 소설 장르

    @Schema(description = "작가 이름", defaultValue = "test 작가")
    private String authorName;      // 작가 이름

    @Schema(description = "소설 이름", defaultValue = "test 소설")
    private String novelName;       // 소설 이름

    @Schema(description = "소설 별점", defaultValue = "9.8")
    private Double rating;          // 소설의 별점 (평균)

    @Schema(description = "리뷰 수", defaultValue = "38")
    private Integer reviewCnt;     // 리뷰를 남긴 사람의 수


    @Schema(description = "대표 작품 여부", defaultValue = "0")
    private Integer isLeadItem;


}
