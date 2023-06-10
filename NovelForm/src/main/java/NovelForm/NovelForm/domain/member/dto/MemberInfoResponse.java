package NovelForm.NovelForm.domain.member.dto;

import NovelForm.NovelForm.domain.member.domain.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoResponse {

    @Schema(description = "이메일", defaultValue = "test@naver.com")
    String email;

    @Schema(description = "닉네임", defaultValue = "test")
    String nickname;

    @Schema(description = "성별", defaultValue = "MALE")
    Enum<Gender> gender;

    @Schema(description = "나이", defaultValue = "20")
    int age;

}
