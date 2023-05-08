package NovelForm.NovelForm.domain.member.dto;


import NovelForm.NovelForm.domain.member.domain.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 회원 가입 요청 시 필요한 내용을 담은 클래스
 *
 * 이메일, 패스워드, 닉네임, 성별 정보를 가져야 한다. (필수 정보들)
 *
 * 문자열에 정수 값이 들어가는 경우는 아직 상정하지 않았다...
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateMemberRequest {

    @Schema(description = "이메일", defaultValue = "test@naver.com")
    @NotEmpty
    @Email
    private String email;

    @Schema(description = "비밀번호", defaultValue = "password123")
    @NotEmpty
    @Size(max = 20, min = 6)
    private String password;

    @Schema(description = "닉네임", defaultValue = "testNickname")
    @NotEmpty
    @Size(max = 20, min = 3)
    private String nickname;

    @Schema(description = "성별", defaultValue = "MALE")
    @NotNull
    private Gender gender;

    @Schema(description = "나이", defaultValue = "10")
    @NotNull
    private Integer age;
}
