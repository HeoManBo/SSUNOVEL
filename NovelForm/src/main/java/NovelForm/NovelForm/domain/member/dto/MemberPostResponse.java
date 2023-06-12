package NovelForm.NovelForm.domain.member.dto;


import NovelForm.NovelForm.domain.community.dto.PostDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberPostResponse {

    @Schema(description = "전체 작성글 갯수")
    int memberPostCnt;

    @Schema(description = "이번 페이지의 작성글 목록")
    List<PostDto> memberPostList = new ArrayList<>();
}
