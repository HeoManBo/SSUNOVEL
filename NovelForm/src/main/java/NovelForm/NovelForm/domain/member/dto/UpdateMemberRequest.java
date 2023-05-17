package NovelForm.NovelForm.domain.member.dto;


import NovelForm.NovelForm.domain.member.domain.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateMemberRequest {

    @Schema(description = "비밀번호", defaultValue = "12345678")
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

    @Schema(description = "나이", defaultValue = "2023-05-16")
    @NotNull
    private String age;
}
