package NovelForm.NovelForm.domain.novel;

import NovelForm.NovelForm.domain.novel.dto.reivewdto.ReviewBodyDto;
import NovelForm.NovelForm.domain.novel.exception.NotReviewOwner;
import NovelForm.NovelForm.global.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static NovelForm.NovelForm.global.SessionConst.LOGIN_MEMBER_ID;

/**
 * 리뷰 작성 관련 컨트롤러 입니다.
 * 로그인 하지 않은 유저의 경우 interceptor 단에서 걸러지므로 별다른 에러처리는 하지 않습니다.
 */
@RestController
@Slf4j
@Tag(name = "리뷰 작성", description = "리뷰 작성 관련 api입니다")
@RequiredArgsConstructor
@RequestMapping("/novel/review")
public class ReivewController {

    private final ReviewService reviewService;

    //리뷰 등록 메소드입니다.
    @PostMapping("/{novel_id}")
    public BaseResponse writeReview(@RequestBody ReviewBodyDto reviewBodyDto, BindingResult bindingResult,
                                    @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
                                    @PathVariable Long novel_id) throws Exception {

        //Binding시 문제가 있을 경우 -> 에러 (제한 범위를 벗어난 별점 부여 )
        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException("잘못된 인자 값입니다.");
        }

        Long result = reviewService.writeReview(reviewBodyDto, memberId, novel_id);

        return new BaseResponse<Long>(HttpStatus.OK, result);
    }

    //리뷰 삭제 메소드입니다.
    @DeleteMapping("/{novel_id}/{review_id}")
    public BaseResponse deleteReview(@SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
                                     @PathVariable("novel_id") Long novel_id,
                                     @PathVariable("review_id") Long review_id) throws Exception{

        String result = reviewService.deleteReview(memberId, novel_id, review_id);

        return new BaseResponse(HttpStatus.OK, result);
    }

    //리뷰 수정 메소드입니다.
    @PatchMapping("/{novel_id}/{review_id}")
    public BaseResponse modifyReview(@RequestBody ReviewBodyDto reviewBodyDto, BindingResult bindingResult,
                                     @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
                                     @PathVariable("novel_id") Long novel_id,
                                     @PathVariable("review_id") Long review_id) throws Exception {

        String result = reviewService.modifyReview(reviewBodyDto, memberId, novel_id, review_id);


        return new BaseResponse(HttpStatus.OK, result);

    }
}
