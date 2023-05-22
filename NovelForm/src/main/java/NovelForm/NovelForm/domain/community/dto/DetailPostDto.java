package NovelForm.NovelForm.domain.community.dto;


import NovelForm.NovelForm.domain.community.CommunityPost;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 상세 조회시 사용할 POST
 */
@Data
@AllArgsConstructor
public class DetailPostDto {

    @Schema(description = "작성자 nickname")
    private String nickname;

    @Schema(description = "게시글 제목")
    private String title;

    @Schema(description = "게시글 내용")
    private String content;

    @Schema(description = "작성자 ID")
    private Long id;

    @Schema(description = "댓글 리스트")
    List<CommentDto> commentLists;

    public DetailPostDto(CommunityPost communityPost) {
        this.nickname = communityPost.getMember().getNickname();
        this.title = communityPost.getTitle();
        this.content = communityPost.getContent();
        this.id = communityPost.getMember().getId();
        this.commentLists = communityPost.getComments().stream()
                .map(c -> new CommentDto(c)).collect(Collectors.toList());
    }

    //    @Builder
//    public DetailPostDto(String nickname, String title, String content, Long id, List<CommentDto> commentLists) {
//        this.nickname = nickname;
//        this.title = title;
//        this.content = content;
//        this.id = id;
//        this.commentLists = commentLists;
//    }
//
//    public void addList(List<CommentDto> list){
//        this.commentLists = list;
//    }
}
