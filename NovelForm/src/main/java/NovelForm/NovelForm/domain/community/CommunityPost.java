package NovelForm.NovelForm.domain.community;

import NovelForm.NovelForm.domain.comment.Comment;
import NovelForm.NovelForm.domain.community.dto.PostDto;
import NovelForm.NovelForm.domain.community.dto.WriteDto;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class CommunityPost extends BaseEntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="community_post_idx")
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", nullable = false)
    private Member member;

    //해당 게시글이 가지고 있는 댓글/대댓글의 모임 -> 게시글 삭제시 댓글까지 모두 삭제
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "communityPost")
    List<Comment> comments = new ArrayList<>();

    public void addMember(Member member){
        this.member = member;
        member.addCommunityPost(this);
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
    }


    @Builder
    // 게시글 생성자로 처음 입력받을 수 있는 제목, 본문, 작성자를 전달받는다.
    public CommunityPost(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    // == 게시글 수정 메소드 === //
    public void changeTitle(String newTitle){
        this.title = newTitle;
    }

    public void changeContent(String newContent){
        this.content = newContent;
    }
}
