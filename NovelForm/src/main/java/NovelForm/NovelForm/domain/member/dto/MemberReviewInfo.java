package NovelForm.NovelForm.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberReviewInfo {


    @Schema(description = "작성한 리뷰 내용", defaultValue = "리뷰 내용이 없다면 null값")
    private String content; // 리뷰 내용

    @Schema(description = "부여한 평점", defaultValue = "0.0~5.0 0.5단위")
    private double rating; // 별점

    @Schema(description = "작성 시간")
    private LocalDateTime writeAt; // 작성 시간

    @Schema(description = "해당 리뷰에 좋아요를 누른 수")
    private int like_cnt; // 좋아요 수

    @Schema(description = "소설 제목")
    private String novelTitle;

    @Schema(description = "소설 이미지")
    private String novelSrc;

    @Schema(description = "작가 이름")
    private String authorName;

    @Schema(description = "소설 번호")
    private Long novelId;

    @Schema(description = "리뷰 번호")
    private Long reviewId;
}
