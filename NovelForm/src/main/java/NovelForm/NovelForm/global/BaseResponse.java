package NovelForm.NovelForm.global;


import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

/**
 *  API 성공 시 전달될 기본 포멧
 *  T에는 result에 들어갈 타입을 명시하면 된다.
 *  ex) new BaseResponse<Long>(id);
 *
 * @Getter가 있어야 핸들러가 가져다 JSON파싱이 가능하다?
 */
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공",
        content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못 된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
})
@Slf4j
@Getter
@NoArgsConstructor
public class BaseResponse<T>{

    // HTTP 상테 코드를 반환할 변수
    @Schema(description = "HTTP 상태 코드", defaultValue = "OK")
    private HttpStatus code = OK;

    // 결과 데이터를 전달할 변수 (제네릭 타입)
    @Schema(description = "응답 결과", defaultValue = "null")
    private T result ;

    // 응답에 포함할 메시지
    @Schema(description = "응답 상태 메시지", defaultValue = "성공")
    private String message = "성공";

    public BaseResponse(HttpStatus code, T result) {
        this.code = code;
        this.result = result;
    }

    public BaseResponse(HttpStatus code, T result, String message) {
        this.code = code;
        this.result = result;
        this.message = message;
    }

    public BaseResponse(T result) {
        this.result = result;
    }


}
