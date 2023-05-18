package NovelForm.NovelForm.domain.alert.dto;


import NovelForm.NovelForm.domain.alert.domain.AlertType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlertInfo {

    @Schema(description = "알림 id", defaultValue = "1")
    private Long id;

    @Schema(description = "알림 제목", defaultValue = "알림 제목입니다.")
    private String title;

    @Schema(description = "알림 타입", defaultValue = "AUTHOR")
    private AlertType alertType;

    @Schema(description = "요청해야 하는 URL 입니다. (GET 으로 요청)", defaultValue = "https://www.novelforum.site/novel/34")
    private String url;

    @Schema(description = "알림을 읽었는지 체크")
    private int readCheck;
}
