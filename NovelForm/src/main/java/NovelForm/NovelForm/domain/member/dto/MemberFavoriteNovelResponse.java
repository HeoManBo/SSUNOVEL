package NovelForm.NovelForm.domain.member.dto;

import NovelForm.NovelForm.domain.novel.dto.searchdto.NovelDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberFavoriteNovelResponse {

    @Schema(description = "즐겨찾기 한 소설 개수")
    int novelCnt;

    @Schema(description = "즐겨찾기 한 소설 정보 목록")
    List<MemberFavoriteNovelInfo> favoriteNovelList = new ArrayList<>();
}
