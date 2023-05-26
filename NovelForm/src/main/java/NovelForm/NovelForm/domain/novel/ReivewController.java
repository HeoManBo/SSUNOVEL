package NovelForm.NovelForm.domain.novel;

import NovelForm.NovelForm.domain.novel.dto.reivewdto.BestReviewDto;
import NovelForm.NovelForm.domain.novel.dto.reivewdto.ReviewBodyDto;
import NovelForm.NovelForm.domain.novel.exception.NoMatchingGenre;
import NovelForm.NovelForm.domain.novel.exception.NotReviewOwner;
import NovelForm.NovelForm.global.BaseResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @ApiResponse(description = "리뷰 등록 API 메소드입니다. 성공시 해당 리뷰 ID를 반환합니다.")
    @PostMapping("/{novel_id}")
    public BaseResponse writeReview(@RequestBody ReviewBodyDto reviewBodyDto, BindingResult bindingResult,
                                    @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
                                    @PathVariable Long novel_id) throws Exception {

        //Binding시 문제가 있을 경우 -> 에러 (제한 범위를 벗어난 별점 부여 )
        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException("잘못된 인자 값입니다.");
        }

        log.info("review add = member_id = {}, novel = {}", memberId, novel_id);

        Long result = reviewService.writeReview(reviewBodyDto, memberId, novel_id);

        return new BaseResponse<Long>(HttpStatus.OK, result);
    }

    //리뷰 삭제 메소드입니다.
    @ApiResponse(description = "리뷰 삭제 API 메소드입니다. 성공시 result에 success 문자열을 출력합니다.")
    @DeleteMapping("/{novel_id}/{review_id}")
    public BaseResponse deleteReview(@Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
                                     @PathVariable("novel_id") Long novel_id,
                                     @PathVariable("review_id") Long review_id) throws Exception{

        String result = reviewService.deleteReview(memberId, novel_id, review_id);

        return new BaseResponse(HttpStatus.OK, result);
    }

    //리뷰 수정 메소드입니다.
    @ApiResponse(description = "리뷰 수정 API 메소드입니다. 성공시 result에 success 문자열을 출력합니다.")
    @PatchMapping("/{novel_id}/{review_id}")
    public BaseResponse modifyReview(@RequestBody ReviewBodyDto reviewBodyDto, BindingResult bindingResult,
                                     @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
                                     @PathVariable("novel_id") Long novel_id,
                                     @PathVariable("review_id") Long review_id) throws Exception {

        String result = reviewService.modifyReview(reviewBodyDto, memberId, novel_id, review_id);


        return new BaseResponse(HttpStatus.OK, result);

    }

    /**
     * 장르별 베스트 리뷰 조회
     */
    @GetMapping("")
    public BaseResponse bestReview(    @Parameter(description = "장르 선택 파라미터입니다. 미선택시 default로 로맨스 베스트 리뷰를 조회합니다.")
                                       @RequestParam(value = "genre", required = false, defaultValue = "로맨스") String genre,
                                       @Parameter(description = "페이지 번호입니다 미선택시 0번으로 지정됩니다.")
                                       @RequestParam(value = "page", required = false, defaultValue = "0") int page) throws Exception {


        List<BestReviewDto> bestReview = reviewService.findBestReview(page, genre);


        return new BaseResponse(HttpStatus.OK, bestReview);
    }

}
