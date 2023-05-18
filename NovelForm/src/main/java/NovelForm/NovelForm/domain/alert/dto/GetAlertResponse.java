package NovelForm.NovelForm.domain.alert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetAlertResponse {

    @Schema(description = "알림 목록")
    List<AlertInfo> alertInfoList;

    @Schema(description = "알림 갯수", defaultValue = "1")
    Integer alertCnt;
}
