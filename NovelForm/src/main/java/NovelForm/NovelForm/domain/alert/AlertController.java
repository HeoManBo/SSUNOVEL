package NovelForm.NovelForm.domain.alert;


import NovelForm.NovelForm.domain.alert.dto.AlertInfo;
import NovelForm.NovelForm.domain.alert.dto.GetAlertResponse;
import NovelForm.NovelForm.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static NovelForm.NovelForm.global.SessionConst.LOGIN_MEMBER_ID;


@Tag(name = "알림", description = "알림 관련 API 입니다.")
@Slf4j
@RequestMapping("/alert")
@RestController
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;


    /**
     * 알림 조회
     */
    @Operation(summary = "알림 목록 조회", description = "로그인 한 사용자의 알림 목록을 가져옵니다.")
    @GetMapping
    public BaseResponse<GetAlertResponse> getAlert(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId) throws Exception {

        GetAlertResponse getAlertResponse = alertService.getAlert(memberId);
        return new BaseResponse<>(getAlertResponse);
    }


    /**
     * 알림 읽음 처리
     */
    @Operation(summary = "알림 읽기 처리", description = "사용자가 지정한 알림을 읽기 처리합니다.")
    @GetMapping("/{alertId}")
    public BaseResponse<String> readAlert(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @Parameter(description = "알림 번호(id)", in = ParameterIn.PATH) @PathVariable Long alertId) throws Exception {

        String result = alertService.readAlert(memberId, alertId);
        return new BaseResponse<>(result);
    }




    /**
     * 알림 삭제
     */
    @Operation(summary = "알림 삭제", description = "사용자가 지정한 알림을 삭제합니다.")
    @DeleteMapping("/{alertId}")
    public BaseResponse<String> deleteAlert(
            @Parameter(hidden = true) @SessionAttribute(name = LOGIN_MEMBER_ID, required = false) Long memberId,
            @Parameter(description = "알림 번호(id)", in = ParameterIn.PATH) @PathVariable Long alertId) throws Exception {


        String result = alertService.deleteAlert(memberId, alertId);
        return new BaseResponse<>(result);
    }




}
