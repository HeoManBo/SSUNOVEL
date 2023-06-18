package NovelForm.NovelForm.domain.novel;

import NovelForm.NovelForm.domain.novel.dto.RecommendNovelList;
import NovelForm.NovelForm.domain.novel.dto.searchdto.MidFormmat;
import NovelForm.NovelForm.global.BaseResponse;
import NovelForm.NovelForm.global.exception.LoginInterceptorException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static NovelForm.NovelForm.global.SessionConst.LOGIN_MEMBER_ID;

/**
 * 소설 추천 서비스 부분 컨트롤러입니다.
 */

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/recommend")
@Tag(name = "추천 시스템", description = "리뷰 기반 추천 소설 컨트롤러입니다. ")
public class RecommendController {

    private final WebClient webClient;
    private final ReviewService reviewService;
    private final NovelService novelService;

    @Operation(summary = "추천 소설", description = "리뷰를 기반으로 추천 소설을 반환합니다. ",
            responses = @ApiResponse(responseCode = "200", description = "추천 소설 제공", content = @Content(schema = @Schema(implementation = MidFormmat.class))))
    @GetMapping()
    private BaseResponse<MidFormmat> recommend(@Parameter(hidden = true, description = "추천대상 멤버 아이디")
                                                   @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId) throws LoginInterceptorException {
        if(memberId == null) { // 비 로그인 대상
            throw new LoginInterceptorException("로그인하여 리뷰를 남겨보세요!");
        }

        if(memberId != 9999){
            throw new IllegalArgumentException("리뷰 데이터를 수집중입니다...");
        }

        boolean canRecommend = reviewService.canRecommend(memberId);
        if(!canRecommend){ //추천 개수가 10개 미만이면
            throw new IllegalArgumentException("최초 10개의 리뷰 작성이 필요합니다.\n리뷰를 더 작성해 소설을 추천받아 보세요!");
        }

        //추천 시스템 호출
        log.info("추천 회원 : {}, 추천 시스템 시작", memberId);
        Mono<RecommendNovelList> recommendNovelMono = webClient.get()
                .uri("http://52.79.165.132/novel/recommend/" + memberId)
                .retrieve()
                .bodyToMono(RecommendNovelList.class);

        RecommendNovelList r = recommendNovelMono.block(); // 현재 쓰레드를 block 시키고 api 호출 완료 대기..

        log.info("recommend Novel = {}", r.getNovel_id().toString());

        MidFormmat recommendNovelList = novelService.findRecommendNovelList(r.getNovel_id()); //추천 번호를 가지고 해당 번호의 소설 정보 가져옴

        return new BaseResponse<MidFormmat>(HttpStatus.OK, recommendNovelList);
    }

}
