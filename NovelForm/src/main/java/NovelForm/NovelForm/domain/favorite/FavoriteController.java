package NovelForm.NovelForm.domain.favorite;


import NovelForm.NovelForm.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static NovelForm.NovelForm.global.SessionConst.LOGIN_MEMBER_ID;


@Tag(name = "즐겨찾기", description = "즐겨찾기 관련 api입니다.")
@Slf4j
@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {


    private final FavoriteService favoriteService;


    /**
     * 소설 즐겨찾기 추가
     *
     * 소설 번호를 받아 해당 회원에게 즐겨찾기 등록
     *
     * @param memberId
     * @Param novelId
     * @return
     * @throws Exception
     */
    @Operation(summary = "소설 즐겨찾기 등록", description = "해당 사용자의 소설 즐겨찾기 목록에 전달받은 소설을 추가합니다.")
    @PostMapping("/novel/{novelId}")
    public BaseResponse<Long> createFavoriteNovel(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @Parameter(description = "소설 번호(id)", in = ParameterIn.PATH) @PathVariable Long novelId) throws Exception {



        Long resultId = favoriteService.createFavoriteNovel(memberId, novelId);

        return new BaseResponse<>(resultId);
    }


    /**
     * 보관함 즐겨찾기 추가
     *
     */
    @Operation(summary = "보관함 즐겨찾기 등록", description = "보관함을 즐겨찾기에 등록합니다.")
    @PostMapping("/box/{boxId}")
    public BaseResponse<Long> createFavoriteBox(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @Parameter(description = "보관함 번호(id)", in = ParameterIn.PATH) @PathVariable Long boxId) throws Exception {


        Long resultId = favoriteService.createFavoriteBox(memberId, boxId);
        return new BaseResponse<>(resultId);
    }


    /**
     * 작가 즐겨찾기 추가
     *
     */
    @Operation(summary = "작가 즐겨찾기 추가", description = "작가를 즐겨찾기에 추가합니다.")
    @PostMapping("/author/{authorId}")
    public BaseResponse<Long> createFavoriteAuthor(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @Parameter(description = "작가 번호(id)", in = ParameterIn.PATH) @PathVariable Long authorId) throws Exception {


        Long resultId = favoriteService.createFavoriteAuthor(memberId, authorId);
        return new BaseResponse<>(resultId);
    }




    /**
     * 소설 즐겨찾기 삭제
     *
     */
    @Operation(summary = "소설 즐겨찾기 취소", description = "등록한 소설 즐겨찾기를 삭제합니다.")
    @DeleteMapping("/novel/{novelId}")
    public BaseResponse<String> delFavoriteNovel(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @Parameter(description = "소설 번호(id)", in = ParameterIn.PATH) @PathVariable Long novelId) throws Exception {


        favoriteService.delFavoriteNovel(memberId, novelId);

        return new BaseResponse<>("삭제 완료");
    }



    /**
     * 보관함 즐겨찾기 삭제
     *
     */
    @Operation(summary = "보관함 즐겨찾기 취소", description = "등록한 보관함 즐겨찾기를 취소합니다.")
    @DeleteMapping("/box/{boxId}")
    public BaseResponse<String> delFavoriteBox(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @Parameter(description = "보관함 번호(id)", in = ParameterIn.PATH) @PathVariable Long boxId) throws Exception {

        favoriteService.delFavoriteBox(memberId, boxId);

        return new BaseResponse("삭제 완료");
    }


    /**
     * 작가 즐겨찾기 삭제
     *
     */
    @Operation(summary = "작가 츨겨찾기 취소", description = "등록한 작가 즐겨찾기를 취소합니다.")
    @DeleteMapping("/author/{authorId}")
    public BaseResponse<String> delFavoriteAuthor(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @Parameter(description = "작가 번호(id)", in = ParameterIn.PATH) @PathVariable Long authorId) throws Exception {

        favoriteService.delFavoriteAuthor(memberId, authorId);

        return new BaseResponse<>("삭제 완료");
    }


}
