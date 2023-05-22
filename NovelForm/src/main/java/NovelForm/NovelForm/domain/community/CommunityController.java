package NovelForm.NovelForm.domain.community;


import NovelForm.NovelForm.domain.community.dto.DetailPostDto;
import NovelForm.NovelForm.domain.community.dto.PostDto;
import NovelForm.NovelForm.domain.community.dto.WriteDto;
import NovelForm.NovelForm.domain.community.exception.NoPostException;
import NovelForm.NovelForm.domain.community.exception.NotFoundSessionException;
import NovelForm.NovelForm.domain.favorite.exception.WrongMemberException;
import NovelForm.NovelForm.global.BaseResponse;
import NovelForm.NovelForm.global.ErrorResultCreater;
import NovelForm.NovelForm.global.exception.CustomFieldException;
import NovelForm.NovelForm.global.exception.NoSuchListElement;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
@Tag(name = "커뮤니티 API", description = "커뮤니티 관련 API입니다.")
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;

    /**
     * 게시글 등록 메서드, 게시글 등록 성공시 등록한 게시글 번호를 반환합니다.
     */
    @PostMapping()
    public BaseResponse<Long> writePost(@RequestBody @Valid WriteDto writeDto,
                              BindingResult bindingResult, @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId) throws Exception {

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
    public BaseResponse totalPost(@RequestParam(value = "page", required = false, defaultValue = "0") int pageNum)
            throws NoSuchListElement {

        if(pageNum < 0){
            throw new NumberFormatException("페이지 번호는 음수이거나 문자열일 수 없습니다.");
        }

        List<PostDto> result = communityService.totalPost(pageNum);

        if(result == null){ //List를 받아오지 못하면 에러 호출
            throw new NoSuchListElement();
        }


        return new BaseResponse(HttpStatus.OK, result);
    }

    /**
     *
     * @param post_id 에 대응되는 게시글 상세 조회
     * 상쇄 페이지 결과 Dto인 DetailPostDto을 반환합니다.
     */
    @GetMapping("/{post_id}")
    public BaseResponse<DetailPostDto> detailPost(@PathVariable("post_id") Long post_id) throws Exception{

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
    public BaseResponse<DetailPostDto> modifyPost(@RequestBody @Valid WriteDto writeDto,
                                           BindingResult bindingResult,
                                           @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
                                           @PathVariable("post_id") Long post_id) throws Exception {

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
    public BaseResponse<String> deletePost(@SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
                                           @PathVariable("post_id") Long post_id) throws Exception {

        if(memberId == null){ //ssession 값이 없으면
            throw new NotFoundSessionException();
        }

        String result = communityService.deletePost(memberId, post_id);


        return new BaseResponse<String>(HttpStatus.OK, result);
    }

    /**
     *
     * @param keyword 검색어
     * @return 검색어가 포함된 게시글 리스트를 반환합니다.
     */
    @GetMapping("/search")
    public BaseResponse<List<PostDto>> keywordPost(@RequestParam("keyword") String keyword) throws NoSuchListElement {
        List<PostDto> result = communityService.keywordPost(keyword);

        log.info("keyword = {}", keyword);

        if(result == null){
            throw new NoSuchListElement();
        }

        return new BaseResponse(HttpStatus.OK, result);
    }
}
