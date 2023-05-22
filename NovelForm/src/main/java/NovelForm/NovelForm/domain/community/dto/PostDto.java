package NovelForm.NovelForm.domain.community.dto;

import lombok.Data;

@Data
public class PostDto {

    private Long postId; //게시글 번호
    private String title; //제목
    private String nickName; //작성한 사람 이름

    public PostDto(Long postId, String title, String nickName) {
        this.postId = postId;
        this.title = title;
        this.nickName = nickName;
    }
}
