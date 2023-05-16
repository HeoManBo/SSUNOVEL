package NovelForm.NovelForm.domain.like;


import NovelForm.NovelForm.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static NovelForm.NovelForm.global.SessionConst.LOGIN_MEMBER_ID;

@Slf4j
@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {


    private final LikeService likeService;


    @Operation(summary = "보관함 좋아요 등록", description = "해당 보관함에 좋아요 등록")
    @PostMapping("/box/{boxId}")
    public BaseResponse<Long> createBoxLike(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @Parameter(description = "보관함 번호(id)", in = ParameterIn.PATH) @PathVariable Long boxId) throws Exception {

        Long resultId = likeService.createBoxLike(memberId, boxId);

        return new BaseResponse<>(resultId);
    }


    @Operation(summary = "리뷰 좋아요 등록", description = "해당 리뷰에 좋아요 등록")
    @PostMapping("/review/{reviewId}")
    public BaseResponse<Long> createReviewLike(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @Parameter(description = "리뷰 번호(id)", in = ParameterIn.PATH) @PathVariable Long reviewId) throws Exception {

        Long resultId = likeService.createReviewLike(memberId, reviewId);

        return new BaseResponse<>(resultId);
    }


}
