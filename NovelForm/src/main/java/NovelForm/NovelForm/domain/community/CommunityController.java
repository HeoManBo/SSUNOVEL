package NovelForm.NovelForm.domain.community;


import NovelForm.NovelForm.domain.community.dto.DetailPostDto;
import NovelForm.NovelForm.domain.community.dto.PostDto;
import NovelForm.NovelForm.domain.community.dto.PostListDto;
import NovelForm.NovelForm.domain.community.dto.WriteDto;
import NovelForm.NovelForm.domain.community.exception.NoPostException;
import NovelForm.NovelForm.domain.community.exception.NotFoundSessionException;
import NovelForm.NovelForm.domain.favorite.exception.WrongMemberException;
import NovelForm.NovelForm.global.BaseResponse;
import NovelForm.NovelForm.global.ErrorResultCreater;
import NovelForm.NovelForm.global.exception.CustomFieldException;
import NovelForm.NovelForm.global.exception.NoSuchListElement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static NovelForm.NovelForm.global.SessionConst.LOGIN_MEMBER_ID;

/**
 * 커뮤니티 글 등록 컨트롤러
 */

@RestController
@Slf4j
@Tag(name = "커뮤니티 게시글 API", description = "커뮤니티 게시글 관련 API입니다.")
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;

    /**
     * 게시글 등록 메서드, 게시글 등록 성공시 등록한 게시글 번호를 반환합니다.
     */
    @PostMapping()
    @Operation(description = "게시글 작성 메소드입니다", responses = @ApiResponse(responseCode = "200", description = "게시글 작성 성공, 성공시 게시글 번호를 result에 반환합니다."))
    public BaseResponse<Long> writePost(@Parameter(description = "게시글 작성 형식") @RequestBody @Valid WriteDto writeDto,
                              BindingResult bindingResult, @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId) throws Exception {

        // 게시글 형태에 오류가 존재하는 경우 throw;
        if(bindingResult.hasErrors()){
            Map<String, String> map = ErrorResultCreater.fieldErrorToMap(bindingResult.getFieldErrors());
            throw new CustomFieldException(map);
        }

        if(memberId == null){ //ssession 값이 없으면
            throw new NotFoundSessionException();
        }

        Long postNum = communityService.writePost(writeDto, memberId);

        if(postNum == -1){ //게시글 생성 권한이 없는 유저라면
            throw new WrongMemberException("게시글 생성 권한이 없습니다.");
        }


        return new BaseResponse(HttpStatus.OK, postNum);
    }


    /**
     * 전체 게시글 조회 메소드 입니다.
     * 파라미터로 넘어오는 페이지 번호에 맞는 게시글을 반환합니다.
     */
    @GetMapping()
    @Operation(description = "전체 게시글 조회 메소드입니다", responses = @ApiResponse(responseCode = "200", description = "게사글 조회 목록 리스트 형태로 제공하며", content = @Content(schema = @Schema(implementation = PostDto.class))))
    public BaseResponse totalPost(    @Parameter(description = "페이지 번호", in = ParameterIn.QUERY)
                                      @RequestParam(value = "page", required = false, defaultValue = "0") int pageNum,
                                      @Parameter(description = "게시글 정렬 기준으로 기본은 최신순(내림차순) : latest 부터 오래된 순서(오름차순)으로 조회하려면 outDate 전달", in = ParameterIn.QUERY)
                                      @RequestParam(value = "orderByDate", required = false, defaultValue = "latest") String date)
            throws NoSuchListElement {

        if(pageNum < 0){
            throw new NumberFormatException("페이지 번호는 음수이거나 문자열일 수 없습니다.");
        }

        PostListDto result = communityService.totalPost(pageNum, date);

        if(result == null){ //List를 받아오지 못하면 에러 호출
            throw new NoSuchListElement();
        }


        return new BaseResponse(HttpStatus.OK, result);
    }

    /**
     *
       게시글 번호에 대응되는 게시글 상세 조회
     * 상쇄 페이지 결과 Dto인 DetailPostDto을 반환합니다.
     */
    @GetMapping("/{post_id}")
    @Operation(description = "게시글 상세 조회 메소드입니다", responses = @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공", content = @Content(schema = @Schema(implementation = DetailPostDto.class))))
    public BaseResponse<DetailPostDto> detailPost(
            @Parameter(description = "상세 조회할 게시글 번호", in = ParameterIn.PATH)
            @PathVariable("post_id") Long post_id) throws Exception{

        DetailPostDto result = communityService.getDetailPost(post_id);

        if(result == null){ //상세 조회할 게시글이 없다면
            throw new NoPostException();
        }

        return new BaseResponse<>(HttpStatus.OK, result);
    }


    /**
     * 게시글 수정 method 입니다.
     * 게시글 수정 성공시 수정한 상세 게시글 Dto 를 반환합니다.
     */
    @PatchMapping("/{post_id}")
    @Operation(description = "게시글 수정 메소드입니다", responses = @ApiResponse(responseCode = "200", description = "수정 성공시 수정된 내용의 게시글 상세 조회 Dto를 반환합니다.", content = @Content(schema = @Schema(implementation = DetailPostDto.class))))
    public BaseResponse<DetailPostDto> modifyPost(
            @Parameter(description = "수정할 게시글의 내용")@RequestBody @Valid WriteDto writeDto,
            BindingResult bindingResult, @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @Parameter(description = "수정할 게시글 번호", in = ParameterIn.PATH) @PathVariable("post_id") Long post_id) throws Exception {

        // 수정할 게시글 형태에 오류가 존재하는 경우 throw;
        if(bindingResult.hasErrors()){
            Map<String, String> map = ErrorResultCreater.fieldErrorToMap(bindingResult.getFieldErrors());
            throw new CustomFieldException(map);
        }

        if(memberId == null){ //ssession 값이 없으면
            throw new NotFoundSessionException();
        }

        DetailPostDto result = communityService.modifyPost(post_id, writeDto, memberId);

        return new BaseResponse<>(HttpStatus.OK, result);
    }

    /**
     * 게시글 삭제 method 입니다.
     * 삭제 성공시 문자열 success를 반환합니다.
     */
    @DeleteMapping("/{post_id}")
    @Operation(description = "게시글 삭제 메소드입니다", responses = @ApiResponse(responseCode = "200", description = "게시글 삭제 성공사 result : 삭제 완료 값이 존재합니다."))
    public BaseResponse<String> deletePost(@Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
                                           @Parameter(description = "삭제할 게시글 번호", in = ParameterIn.PATH) @PathVariable("post_id") Long post_id) throws Exception {

        if(memberId == null){ //ssession 값이 없으면
            throw new NotFoundSessionException();
        }

        String result = communityService.deletePost(memberId, post_id);


        return new BaseResponse<String>(HttpStatus.OK, result);
    }

    /**
     *
     * @param
     * @return 검색어가 포함된 게시글 리스트를 반환합니다.
     */
    @GetMapping("/search")
    @Operation(description = "게시글 검색어 포함 조회 메소드입니다", responses = @ApiResponse(responseCode = "200", description = "게사글 검색어 포함 조회 성공, List 형태로 제공됩니다.", content = @Content(schema = @Schema(implementation = PostDto.class))))
    public BaseResponse<List<PostDto>> keywordPost(    @Parameter(description = "검색어", in = ParameterIn.QUERY)
                                                       @RequestParam("keyword") String keyword,
                                                       @Parameter(description = "게시글 정렬 기준으로 기본은 최신순(내림차순) : latest 부터 오래된 순서(오름차순)으로 조회하려면 outDate 전달", in = ParameterIn.QUERY)
                                                       @RequestParam(value = "orderByDate", required = false, defaultValue = "latest") String date,
                                                       @Parameter(description = "페이지 번호")
                                                       @RequestParam(value = "pageNum", required = false, defaultValue = "0") int page) throws NoSuchListElement {
        if(page < 0){ //음수 페이지 번호시 error;
            throw new NumberFormatException("페이지 번호는 음수가될 수 없습니다..");
        }

        PostListDto result = communityService.keywordPost(keyword, date, page);

        log.info("keyword = {}", keyword);

        if(result == null){
            throw new NoSuchListElement();
        }


        return new BaseResponse(HttpStatus.OK, result);
    }
}
