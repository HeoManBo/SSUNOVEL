package NovelForm.NovelForm.domain.novel;


import NovelForm.NovelForm.domain.favorite.domain.FavoriteNovel;
import NovelForm.NovelForm.domain.member.MemberService;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.dto.CategoryDto;
import NovelForm.NovelForm.domain.novel.dto.detailnoveldto.DetailNovelInfo;
import NovelForm.NovelForm.domain.novel.dto.detailnoveldto.ReviewDto;
import NovelForm.NovelForm.domain.novel.dto.searchdto.MidFormmat;
import NovelForm.NovelForm.domain.novel.dto.searchdto.NovelDto;
import NovelForm.NovelForm.domain.novel.dto.searchdto.SearchDto;
import NovelForm.NovelForm.domain.novel.exception.NoSuchNovelListException;
import NovelForm.NovelForm.global.BaseResponse;
import NovelForm.NovelForm.repository.FavoriteNovelRepository;
import NovelForm.NovelForm.util.NovelCSVParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static NovelForm.NovelForm.global.SessionConst.LOGIN_MEMBER_ID;

@RestController
@Slf4j
@Tag(name = "소설 검색", description = "소설 검색 관련 api입니다")
@RequiredArgsConstructor
@RequestMapping("/novel")
@Validated //RequestParam, PathVariable 검증 추가
public class NovelSearchController {
    private final NovelService novelService;
    private final ReviewService reviewService;
    private final FavoriteNovelRepository favoriteNovelRepository;
    private final MemberService memberService;


    /**
     *
     * search : 검색어
     * paging : 페이지 번호 -> 페이징을 위한 번호, 입력하지 않으면 기본적으로 맨 첫번째 페이지를 검색하도록 설정
     * 정렬 순,
     * 반드시 요청할 때 헤더에 accept 타입의 application/json 이여야함. 그외엔 오류.
     * 검색시 정렬 기준의 대한 고려사항이 필요함.
     * 더 보기 눌렀을 때 경로가 변화되도록?
     * 더 보기를 눌렀을 때 소설 : /search/novel ?= paging_number=.. & orderby="";
     * 더 보기를 눌렀을 때 작가 : /search/author ?= paging_number=.. & orderby="";
     * 테스트 코드랑, main properties 따로 두어서 DB 따로 설정
     * @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId --> 세션 검사
     * //별점순, 최신순, 리뷰 준 사람 숫자,
     */

    @Operation(summary = "소설/작가 검색", description = "자세히 보기 클릭전, 기본적인 정보를 보여줍니다.",
            responses = @ApiResponse(responseCode = "200", description = "상세 검색 성공", content = @Content(schema = @Schema(implementation = SearchDto.class))))
    @GetMapping(value = "/search" , produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<SearchDto> searchNovelAndAuthor(@RequestParam String search,
                                             @RequestParam(required = false, defaultValue = "None") String orderBy){

        //검색어가 빈문자 or 공백으로만 차있는 경우 : bad request;
        if(!StringUtils.hasText(search)){
            throw new IllegalArgumentException("검색어의 입력 값이 없습니다.");
        }

        MidFormmat first = novelService.findNovels(search, 0, orderBy);
        MidFormmat second = novelService.findNovelWithAuthor(search, 0, orderBy);
        SearchDto result = new SearchDto(first, second);

        return new BaseResponse<SearchDto>(HttpStatus.OK, result);
    }

    @Operation(summary = "소설만 상세 검색", description = "페이징이 가능한 소설 검색 결과를 보여줍니다.",
               responses = @ApiResponse(responseCode = "200", description = "소설 상세 검색 성공", content = @Content(schema = @Schema(implementation = MidFormmat.class))))
    @GetMapping(value = "/search/novel", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<MidFormmat> searchNovel(@RequestParam String search,
                                                @RequestParam(required = false, defaultValue = "0") @Min(0) int pageNum,
                                                @RequestParam(required = false, defaultValue = "None") String orderBy){
        //검색어가 빈문자 or 공백으로만 차있는 경우 : bad request;
        if(!StringUtils.hasText(search)){
            throw new IllegalArgumentException("검색어의 입력 값이 없습니다.");
        }

        MidFormmat result = novelService.findNovels(search, pageNum, orderBy);


        return new BaseResponse<MidFormmat>(HttpStatus.OK, result);
    }

    @Operation(summary = "작가만 상세 검색", description = "페이징이 가능한 작가가 가지는 소설 검색 결과를 보여줍니다.",
               responses = @ApiResponse(responseCode = "200", description = "작가 상세 검색 성공", content = @Content(schema = @Schema(implementation = MidFormmat.class))))
    @GetMapping(value = "/search/author", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<MidFormmat> searchAuthor(@RequestParam String search,
                                                 @RequestParam(required = false, defaultValue = "0") @Min(0) int pageNum,
                                                @RequestParam(required = false, defaultValue = "None") String orderBy){
        //검색어가 빈문자 or 공백으로만 차있는 경우 : bad request;
        if(!StringUtils.hasText(search)){
            throw new IllegalArgumentException("검색어의 입력 값이 없습니다.");
        }

        MidFormmat result = novelService.findNovelWithAuthor(search, pageNum, orderBy);

        return new BaseResponse<MidFormmat>(HttpStatus.OK, result);
    }


    /**
     * 상세 조회 메소드
     */
    @Operation(summary = "소설 상세 정보", description = "소설 상세 정보를 보여줍니다. 리뷰와 다른 소설이 없는 경우 공 리스트로 반환됩니다. ",
               responses = @ApiResponse(responseCode = "200", description = "소설 상세 정보 조회 성공", content = @Content(schema = @Schema(implementation = DetailNovelInfo.class))))
    @GetMapping("/{novel_id}")
    public BaseResponse<DetailNovelInfo> detailSearchNovel(@PathVariable("novel_id") @Min(0) Long id,
                                            @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId) throws Exception{
        //소설 정보를 가져온다.
        Novel novel = novelService.findNovel(id);
        if(novel == null){ //novel_id 에 해당하는 id가 없다면
            throw new IllegalArgumentException("해당하는 소설이 존재하지 않습니다.");
        }

        DetailNovelInfo result = DetailNovelInfo.builder()
                .image_url(novel.getCover_image())
                .title(novel.getTitle())
                .content(novel.getSummary())
                .authorName(novel.getAuthor().getName())
                .category(novel.getCategory())
                .is_kakao(novel.getIs_kakao())
                .is_munpia(novel.getIs_munpia())
                .is_naver(novel.getIs_naver())
                .is_ridi(novel.getIs_ridi())
                .review_cnt(novel.getReview_cnt())
                .rating(novel.averageRating())
                .price(novel.getPrice())
                .total_episode(novel.getEpisode()).build();


        //소설 번호에 대응되는 리뷰를 가져온다.
        List<ReviewDto> reviewMatchingNovel = reviewService.findReviewMatchingNovel(novel);
        if(reviewMatchingNovel == null){
            result.setReviewInfos(null);
        }
        else{
            result.setReviewInfos(reviewMatchingNovel);
        }

        //해당 작가의 다른 소설을 가져온다. -> 현재 조회하고 있는 소설은 제외한다.
        List<Novel> authorNovels = novel.getAuthor().getNovels();
        List<NovelDto> an = authorNovels.stream().filter(n -> !n.getTitle().equals(novel.getTitle())).map(n -> new NovelDto(n.getTitle(), novel.getAuthor().getName(), n.getCover_image()
                , n.averageRating(), n.getDownload_cnt(), n.getCategory(), n.getId())).toList();
        result.setAnotherNovels(an);


        //로그인 상태라면 좋아요를 눌렀는지 확인한다.
        if(memberId != null){
            //멤버를 일단 가져옴
            Member member = memberService.isPresentMember(memberId);

            //좋아요를 눌렀는지 확인
            List<FavoriteNovel> findFavorite = favoriteNovelRepository.findByMemberAndNovel(member, novel);
            if(findFavorite.isEmpty()){ //좋아요를 안 눌렀다면
                result.setAlreadyLike(0);
            }
            else{
                result.setAlreadyLike(1);
            }

            //리뷰를 달았는지 확인
            Review myReview = reviewService.findReviewWithMemberAndNovel(novel, member);
            if(myReview != null){
                result.setMy_review(myReview.getContent());
                result.setMy_rating(myReview.getRating());
                result.setMy_review_id(myReview.getId());
            }
            else{
                result.setMy_review(null);
                result.setMy_rating(0.0);
                result.setMy_review_id(0L);
            }
        }
        else{ //로그인 상태가 아니라면 0, 0.0, null 처리
            result.setAlreadyLike(0);
            result.setMy_review(null);
            result.setMy_rating(0.0);
            result.setMy_review_id(0L);
        }

        return new BaseResponse<DetailNovelInfo>(HttpStatus.OK, result);
    }


//    /**
//     *  소설 목록 조회 기능 API 장르에 따른 필터링 조건에 따라 분류함
//     *  default 는 크롤링 당시 웹 소설 사이트의 댓글 수로 하며 높은 평점순, 최신 소설 등록 순으로 분류,
//     */
//    @Operation(summary = "소설 목록 조회", description = "장르별, 리뷰 많은순과 같은 필터링 조건에 따른 리스트 조회 기능입니다.",
//               responses = @ApiResponse(responseCode = "200", description = "소설 목록 조회 성공", content = @Content(schema = @Schema(implementation = MidFormmat.class))))
//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public BaseResponse<MidFormmat> novelGenreListSearch(@RequestParam("genre") String genre, @RequestParam(value = "filtering", required = false, defaultValue = "download_cnt") String filtering,
//                                     @RequestParam(value = "pageNum", required = false, defaultValue = "0") int pageNum) throws NoSuchNovelListException {
//        // binding error -> 페이지 번호가 음수로 들어오거나 문자열로 들어오는 경우.
//        if(pageNum < 0){
//            throw new NumberFormatException("페이지 번호는 양수가 되어야합니다.");
//        }
//
//        //조건에 맞는 소설을 검색해온다.
//        MidFormmat result = novelService.findNovelWithGenre(genre, filtering, pageNum);
//
//        //잘못된 필터링 조건, 장르인경우 에러 발생 --> 소설을 찾을 수 없음
//        if(result == null){
//            throw new NoSuchNovelListException("잘못된 검색어 입니다.");
//        }
//
//        return new BaseResponse<>(HttpStatus.OK, result);
//    }

    /**
     * Category 페이지 API 기능 컨트롤러입니다.
     */
    @Operation(summary = "category 조회", description = "장르별, 플랫폼 별 카테고리 분류 조회 기능입니다.",
            responses = @ApiResponse(responseCode = "200", description = "카테고리별 상세 검색 성공", content = @Content(schema = @Schema(implementation = MidFormmat.class))))
    @PostMapping(value = "/category", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<MidFormmat> novelCategoryList(@RequestBody CategoryDto categoryDto, BindingResult bindingResult) throws JsonMappingException {
        if(bindingResult.hasErrors()){
            throw new JsonMappingException("잘못된 JSON값입니다.");
        }
        MidFormmat novelsWithCategory = novelService.findNovelsWithCategory(categoryDto);

        return new BaseResponse<MidFormmat>(HttpStatus.OK, novelsWithCategory);
    }

}
