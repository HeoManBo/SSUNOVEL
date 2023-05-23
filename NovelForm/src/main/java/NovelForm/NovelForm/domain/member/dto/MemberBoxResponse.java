package NovelForm.NovelForm.domain.member.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberBoxResponse {

    @Schema(description = "보관함 갯수")
    int boxCnt;

    @Schema(description = "본인이 작성한 보관함 목록")
    List<MemberBoxInfo> memberBoxInfoList = new ArrayList<>();

}
