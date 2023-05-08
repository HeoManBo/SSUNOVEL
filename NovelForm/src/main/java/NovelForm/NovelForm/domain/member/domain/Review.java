package NovelForm.NovelForm.domain.member.domain;

import NovelForm.NovelForm.domain.novel.Novel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {
    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private Long id;

    @Column
    private String status;

    @ManyToOne
    @JoinColumn(name = "novel_id")
    private Novel novel;


    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private double rating;

    @Column
    @Lob
    private String content;


    @Builder
    public Review(String status, Novel novel, Member member, double rating, String content) {
        this.status = status;
        this.novel = novel;
        this.member = member;
        this.rating = rating;
        this.content = content;
    }

    //연관관계메소드
    public void addMember(Member member){
        this.member = member;
        member.addMyReview(this);
    }

    public void addNovel(Novel novel){
        this.novel = novel;
        novel.addReview(this);
    }


}
