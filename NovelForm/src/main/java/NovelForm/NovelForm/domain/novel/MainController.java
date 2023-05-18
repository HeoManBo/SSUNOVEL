package NovelForm.NovelForm.domain.novel;


import NovelForm.NovelForm.domain.member.MemberService;
import NovelForm.NovelForm.domain.novel.dto.MainDto;
import NovelForm.NovelForm.domain.novel.dto.searchdto.NovelDto;
import NovelForm.NovelForm.global.BaseResponse;
import com.sun.tools.javac.Main;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

import static NovelForm.NovelForm.global.SessionConst.LOGIN_MEMBER_ID;


/**
 * 메인페이지 소설 조회 컨트롤러입니다
 * 전체 TOP 10 소설(별점 개수 + 별점 평균 높은 소설을 내림차순으로 JSON 형태로 반환합니다.
 */
@RestController
@Slf4j
@Tag(name = "소설 검색", description = "소설 검색 관련 api입니다")
@RequiredArgsConstructor
@RequestMapping("/novel/main")
public class MainController {

    private final MemberService memberService;
    private final NovelService novelService;

    /**
     * 메인화면 조회 페이지입니다.
     */
    @GetMapping("")
    public BaseResponse<MainDto> mainPage(@Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId){
        MainDto result = new MainDto();
        List<NovelDto> rankingNovel = novelService.findRankingNovel();
        result.setRankingNovel(rankingNovel);
        return new BaseResponse(HttpStatus.OK, result);
    }
}
