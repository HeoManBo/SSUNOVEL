package NovelForm.NovelForm.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.AnyKeyJavaClass;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberBoxInfo {

    @Schema(description = "보관함 번호(id)", defaultValue = "1")
    private Long boxId;         // 보관함 번호

    @Schema(description = "보관함 이름", defaultValue = "test title")
    private String title;       // 보관함 이름

    @Schema(description = "보관함 생성자", defaultValue = "test creater")
    private String memberName;  // 보관함 생성자 이름

    @Schema(description = "대표 이미지 링크", defaultValue = "https://img.xxx.net/cover")
    private String imgSrc;      // 대표 이미지 링크

    @Schema(description = "보관함 수정 시간", defaultValue = "2023-05-01T07:53:59")
    private LocalDateTime updateTime;

    @Schema(description = "보관함 내 작품 개수", defaultValue = "3")
    private Long itemCnt;

    @Schema(description = "해당 보관함에 좋아요 개수", defaultValue = "3")
    private int likeCnt;

    @Schema(description = "공개 여부 확인")
    private int isPrivate;

}
