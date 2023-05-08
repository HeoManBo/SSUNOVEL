package NovelForm.NovelForm.domain.novel;


import NovelForm.NovelForm.domain.novel.dto.SearchDto;
import NovelForm.NovelForm.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@Tag(name = "소설 검색", description = "소설 검색 컨트롤러입니다")
@RequiredArgsConstructor
@RequestMapping("/novel")
public class NovelSearchController {
    private final NovelService novelService;

    /**
     *
     * keyword : 검색어
     * paging : 페이지 번호 -> 페이징을 위한 번호, 입력하지 않으면 기본적으로 맨 첫번째 페이지를 검색하도록 설정
     * 정렬 순,
     * 반드시 요청할 때 헤더에 accept 타입의 application/json 이여야함. 그외엔 오류.
     * 검색시 정렬 기준의 대한 고려사항이 필요함.
     */
    @Operation(summary = "소설/작가 검색", description = "입력에 맞게 페이징된, 소설 검색 결과와 작가의 소설 검색 결과를 보여줍니다.")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<SearchDto> searchNovelAndAuthor(@RequestParam String keyword,
                                             @RequestParam(required = false, defaultValue = "0") int novelPageNum,
                                             @RequestParam(required = false, defaultValue = "0") int authorPageNum,
                                             @RequestParam(required = false, defaultValue = "None") String orderBy){
        //검색어가 빈문자 or 공백으로만 차있는 경우 : bad request;
        if(!StringUtils.hasText(keyword)){
            return new BaseResponse(HttpStatus.BAD_REQUEST, null, "잘못된 검색어입니다!");
        }
        SearchDto result = novelService.searchInfo(keyword, novelPageNum, authorPageNum);
        return new BaseResponse<SearchDto>(HttpStatus.OK, result);
    }

    /**
     * 상세 조회 매핑 메소드 --> 기본적인 TEST 는 완료함.
     */
/*    @RequestMapping(value = "/search/{novel_id}")
    public BaseResponse<detailNovelDto>*/

}
