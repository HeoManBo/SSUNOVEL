package NovelForm.NovelForm.global;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

/**
 *  API 요청 실패 시 전달 될 기본 포멧
 */
@Getter
@NoArgsConstructor
public class ErrorResponse<T> {
    // HTTP 상테 코드를 반환할 변수
    private HttpStatus code = BAD_REQUEST;

    // 결과 데이터를 전달할 변수 (제네릭 타입)
    private T result = null;

    // 응답에 포함할 메시지
    private String message = "실패";

    public ErrorResponse(HttpStatus code, String message) {
        this.code = code;
        this.message = message;
    }
}
