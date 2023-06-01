package NovelForm.NovelForm.domain.community;


import NovelForm.NovelForm.domain.community.dto.CommentDto;
import NovelForm.NovelForm.domain.community.dto.CreateCommentDto;
import NovelForm.NovelForm.domain.community.exception.NotFoundSessionException;
import NovelForm.NovelForm.global.BaseResponse;
import NovelForm.NovelForm.global.ErrorResultCreater;
import NovelForm.NovelForm.global.exception.CustomFieldException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static NovelForm.NovelForm.global.SessionConst.LOGIN_MEMBER_ID;

@RestController
@Slf4j
@Tag(name = "커뮤니티 API", description = "커뮤니티 댓글 관련 API입니다.")
@RequiredArgsConstructor
@RequestMapping("/community/comment")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성 메소드입니다
     * @param createCommentDto 댓글 내용이 담긴 Dto
     * @param memberId 세션의 멤버 값
     * @param post_id 댓글을 작성할 게시글 번호
     * @return 댓글 작성 성공시 작성한 댓글 db상 번호
     */
    @Operation(description = "댓글 생성 메소드입니다.")
    @PostMapping("/{post_id}")
    public BaseResponse<Long> makeComment(
            @Valid  @RequestBody CreateCommentDto createCommentDto, BindingResult bindingResult,
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @PathVariable("post_id") Long post_id) throws Exception {

        //세션 값이 있는지 검증
        if(memberId == null){
            throw new NotFoundSessionException();
        }
        //댓글이 공백만 있거나 입력을 하지 않은 경우 bindingresult에 값이 담김
        if(bindingResult.hasErrors()){
            Map<String, String> map = ErrorResultCreater.fieldErrorToMap(bindingResult.getFieldErrors());
            throw new CustomFieldException(map);
        }

        Long result = commentService.createComment(createCommentDto, post_id, memberId);

        return new BaseResponse<Long>(HttpStatus.OK, result);
    }

    @Operation(description = "댓글 수정 메소드입니다, 성공시 내용이 수정된 CommentDto를 반환합니다.")
    @PatchMapping("{comment_id}")
    public BaseResponse<CommentDto> updateComment(
            @Valid  @RequestBody CreateCommentDto createCommentDto, BindingResult bindingResult,
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @PathVariable("comment_id") Long comment_id) throws Exception{

        //세션 값이 있는지 검증
        if(memberId == null){
            throw new NotFoundSessionException();
        }
        //댓글이 공백만 있거나 입력을 하지 않은 경우 bindingresult에 값이 담김
        if(bindingResult.hasErrors()){
            Map<String, String> map = ErrorResultCreater.fieldErrorToMap(bindingResult.getFieldErrors());
            throw new CustomFieldException(map);
        }

        CommentDto result = commentService.updateComment(createCommentDto, memberId, comment_id);

        return new BaseResponse<CommentDto>(HttpStatus.OK, result);
    }


    @Operation(description = "댓글 삭제 메소드입니다. 삭제 성공시 success 문자열을 반환합니다")
    @DeleteMapping("{post_id}/{comment_id}")
    public BaseResponse<String> deleteComment(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @PathVariable("comment_id") Long comment_id,
            @PathVariable("post_id") Long post_id) throws Exception {

        //세션 값이 있는지 검증
        if(memberId == null){
            throw new NotFoundSessionException();
        }

        String result = commentService.deleteComment(memberId, post_id, comment_id);


        return new BaseResponse<String>(HttpStatus.OK, result);
    }


}
