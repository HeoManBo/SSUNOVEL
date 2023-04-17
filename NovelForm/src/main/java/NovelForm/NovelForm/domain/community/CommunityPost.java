package NovelForm.NovelForm.domain.community;

import NovelForm.NovelForm.domain.member.Member;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class CommunityPost extends BaseEntityTime {
    @Id
    @GeneratedValue
    @Column(name ="community_post_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    //해당 게시글이 가지고 있는 댓글/대댓글의 모임 -> 게시글 삭제시 댓글까지 모두 삭제
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "communityPost", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Comment> comments = new ArrayList<>();

    public void addMember(Member member){
        this.member = member;
        member.addCommunityPost(this);
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
    }

    //해당 게시글을 작성한 멤버인지 확인
    public boolean vaildMember(Member member){
        boolean chk = this.member.equals(member);
        return chk == true ? true : false;
    }



}
