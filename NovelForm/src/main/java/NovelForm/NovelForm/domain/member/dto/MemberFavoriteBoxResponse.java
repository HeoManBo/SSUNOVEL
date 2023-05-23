package NovelForm.NovelForm.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class MemberFavoriteBoxResponse {

    @Schema(description = "즐겨찾기로 등록해둔 보관함의 수")
    int favoriteBoxCnt;

    @Schema(description = "즐겨찾기로 등록한 보관함 목록")
    List<MemberBoxInfo> memberBoxInfoList = new ArrayList<>();

}
