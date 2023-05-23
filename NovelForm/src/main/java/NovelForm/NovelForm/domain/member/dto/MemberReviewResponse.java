package NovelForm.NovelForm.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@Getter
@NoArgsConstructor
public class MemberReviewResponse {

    @Schema(description = "작성한 리뷰 개수")
    int reviewCnt;


    @Schema(description = "작성한 리뷰 목록")
    List<MemberReviewInfo> memberReviewInfoList = new ArrayList<>();
}
