package NovelForm.NovelForm.domain.community.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 검색 조건에 맞는 전체 게시글 개수 및
 * 해당 페이지 게시글 조회시 반환할 DTO
 */
@Data
public class PostListDto {
    @Schema(description = "검색 조건에 맞는 전체 게시글 개수")
    private int count;

    @Schema(description = "각 게시글 정보가 있는 리스트")
    private List<PostDto>  postDto;

    public PostListDto(int count, List<PostDto> postDto) {
        this.count = count;
        this.postDto = postDto;
    }
}
