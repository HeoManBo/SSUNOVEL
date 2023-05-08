package NovelForm.NovelForm.domain.box;


import NovelForm.NovelForm.domain.box.dto.AllBoxResponse;
import NovelForm.NovelForm.domain.box.dto.BoxInfoResponse;
import NovelForm.NovelForm.domain.box.dto.BoxSearchResponse;
import NovelForm.NovelForm.domain.box.dto.CreateBoxRequest;
import NovelForm.NovelForm.global.BaseResponse;
import NovelForm.NovelForm.global.ErrorResultCreater;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static NovelForm.NovelForm.global.SessionConst.*;


@Tag(name = "보관함", description = "보관함 관련 api입니다.")
@Slf4j
@RestController
@RequestMapping("/box")
@RequiredArgsConstructor
public class BoxController {


    private final BoxService boxService;


    /**
     * 보관함 생성 메서드
     *
     *
     * @param memberId
     * @param createBoxRequest
     * @param bindingResult
     * @return
     * @throws Exception
     */
    @Operation(summary = "보관함 생성", description = "보관함 생성 메서드입니다.")
    @PostMapping("")
    public BaseResponse<Long> createBox(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @Valid @RequestBody CreateBoxRequest createBoxRequest, BindingResult bindingResult) throws Exception {

        // 필드 에러 발생 시 예외 호출
        if(bindingResult.hasFieldErrors()){
            String message = ErrorResultCreater.objectErrorToJson(bindingResult.getFieldErrors());
            throw new IllegalArgumentException(message);
        }


        // 보관함 아이템 리스트에 대표 작품 번호가 있는지 체크
        if(!createBoxRequest.getBoxItems().contains(createBoxRequest.getLeadItemId())){
            throw new IllegalArgumentException("보관함 아이템 리스트에 대표 작품 번호가 없습니다 : " + createBoxRequest.getLeadItemId());
        }


        Long boxId = boxService.createBox(memberId, createBoxRequest);

        return new BaseResponse<>(boxId);
    }


    /**
     * 보관함 삭제 메서드
     *
     * @param memberId
     * @param boxId
     * @return
     */
    @Operation(summary = "보관함 삭제", description = "보관함 번호(id)를 입력받아 해당 보관함을 삭제합니다.")
    @DeleteMapping("/{boxId}")
    public BaseResponse<String> deleteBox(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @Parameter(description = "보관함 번호(id)", in = ParameterIn.PATH) @PathVariable Long boxId) throws Exception {


        boxService.deleteBox(memberId, boxId);

        return new BaseResponse<>("삭제 성공");
    }


    /**
     * 보관함 업데이트 메서드
     *
     * 일단, 삭제하고 다시 생성하는 방식으로 만들었다.
     *
     * @param memberId
     * @param createBoxRequest
     * @param boxId
     * @return
     */
    @Operation(summary = "보관함 수정", description = "보관함 번호(id)를 받아 해당 보관함을 수정하는 메서드입니다.")
    @PostMapping("/{boxId}")
    public BaseResponse<Long> updateBox(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @Valid @RequestBody CreateBoxRequest createBoxRequest,
            BindingResult bindingResult,
            @Parameter(description = "보관함 번호(id)", in = ParameterIn.PATH) @PathVariable Long boxId) throws Exception {


        // 필드 에러 발생 시 예외 호출
        if(bindingResult.hasFieldErrors()){
            String message = ErrorResultCreater.objectErrorToJson(bindingResult.getFieldErrors());
            throw new IllegalArgumentException(message);
        }


        Long newBoxId = boxService.updateBox(createBoxRequest, memberId, boxId);

        return new BaseResponse<>(newBoxId);
    }


    /**
     * 보관함 전체 목록 가져오기
     *
     * 공개로 설정한 보관함만 가져온다.
     * 보관함 대표이미지, 보관함 제목, 보관함 생성자 이름만 가져온다.
     *
     * @return
     */
    @Operation(summary = "보관함 전체 목록 가져오기", description = "공유로 설정해 놓은 보관함들을 다 가져오는 메서드입니다.")
    @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = AllBoxResponse.class))))
    @GetMapping("/all")
    public BaseResponse<List<AllBoxResponse>> getAllBox(){

        List<AllBoxResponse> allBoxResponses = boxService.getAllBox();

        return new BaseResponse<>(allBoxResponses);
    }


    /**
     * 보관함 상제 정보 가져오기
     *
     * 인자로 전달받은 boxId를 이용해서 해당 보관함 내부 정보를 가져오기
     */
    @Operation(summary = "보관함 상세 정보 가져오기", description = "보관함 내부의 작품 목록및 보관함에 대한 정보를 가져오는 메서드입니다.")
    @GetMapping("/info/{boxId}")
    public BaseResponse<BoxInfoResponse> getBoxInfo(
            @Parameter(name = "보관함 번호(id)", in = ParameterIn.PATH) @PathVariable Long boxId){

        BoxInfoResponse boxInfo = boxService.getBoxInfo(boxId);

        return new BaseResponse<>(boxInfo);
    }


    /**
     * 보관함 검색 기능
     *
     * 검색어에 따른 보관함 목록이 나온다.
     */
    @Operation(summary = "보관함 검색", description = "공유로 설정되어 있는 보관함들을 검색하는 메서드입니다.")
    @GetMapping("/search")
    public BaseResponse<BoxSearchResponse> getSearchBox(
            @Parameter(name = "검색어", required = true, example = "test") @RequestParam String item){

        BoxSearchResponse boxSearchResponse = boxService.searchBox(item);

        return new BaseResponse<>(boxSearchResponse);
    }

}
