package NovelForm.NovelForm.domain.comment;

import NovelForm.NovelForm.domain.community.CommunityPost;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_idx")
    private Long id;

    @Column(length = 45)
    private String status;

    @Column
    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_post_idx")
    private CommunityPost communityPost;

    //대댓글 작성시 작성할 댓글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "co_comment_idx")
    private Comment parentComment;

    //댓글에 달린 대댓글 모음 --> 부모 댓글이 삭제되도 자식 댓글은 살아 있음
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentComment")
    private List<Comment> childComment = new ArrayList<>();

    //연관관계 메소드
    public void addMember(Member member){
        this.member = member;
        member.addComment(this);
    }

    public void addCommunityPost(CommunityPost communityPost){
        this.communityPost = communityPost;
        communityPost.addComment(this);
    }

    public void addChild(Comment comment){
        this.childComment.add(comment);
    }

    @Builder
    public Comment(String content, Member member, CommunityPost communityPost, Comment parentComment) {
        this.content = content;
        this.member = member;
        this.communityPost = communityPost;
        this.parentComment = parentComment;
    }
}
