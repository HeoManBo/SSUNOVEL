package NovelForm.NovelForm.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginMemberRequest {
    @Schema(description = "이메일", defaultValue = "test@naver.com")
    @NotEmpty
    @Email
    private String email;

    @Schema(description = "비밀번호", defaultValue = "12345678")
    @NotEmpty
    @Size(max = 20, min = 6)
    private String password;

}
