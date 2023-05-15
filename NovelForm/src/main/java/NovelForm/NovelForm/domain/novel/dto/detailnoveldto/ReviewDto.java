package NovelForm.NovelForm.domain.novel.dto.detailnoveldto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ReviewDto{

    @Schema(description = "작성한 사람의 nickname", defaultValue = "XXXX")
    String nickname; // 작성한 멤버 nickname;


    @Schema(description = "작성한 리뷰 내용", defaultValue = "리뷰 내용이 없다면 null값")
    String content; //리뷰내용

    @Schema(description = "부여한 평점", defaultValue = "0.0~5.0 0.5단위")
    double rating; //별점

    @Schema(description = "작성 시간")
    LocalDateTime writeAt; //작성 시간

    @Schema(description = "해당 리뷰에 좋아요를 누른 수")
    int like_cnt; // 좋아요 수

}
