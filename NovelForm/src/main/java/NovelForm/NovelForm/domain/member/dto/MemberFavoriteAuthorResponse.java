package NovelForm.NovelForm.domain.member.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberFavoriteAuthorResponse {

    @Schema(description = "즐겨찾기한 작가의 수")
    int authorCnt;

    @Schema(description = "즐겨찾기한 작가 목록")
    List<MemberFavoriteAuthorInfo> memberFavoriteAuthorInfoList = new ArrayList<>();

}
