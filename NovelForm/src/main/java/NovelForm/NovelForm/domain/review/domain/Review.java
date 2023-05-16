package NovelForm.NovelForm.domain.review.domain;

import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntityTime {
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

    @Column
    private int up;


    @Builder
    public Review(String status, Novel novel, Member member, double rating, String content, int up) {
        this.status = status;
        this.novel = novel;
        this.member = member;
        this.rating = rating;
        this.content = content;
        this.up = up;
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

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        Review compare = (Review) obj;
        if(this.member.getId() == compare.getMember().getId()){ // memberId와, obj의 memberId 가 같은 경우 -> member에서 reviews의 제거
            return true;
        }
        else if(this.novel.getId() == compare.getNovel().getId()){ // novelId와 obj의 novelId가 같은 경우 -> novel에서의 reviews의 제거
            return true;
        }
        return false;
    }

    //별점 수정 메소드
    public void modifyRating(double before_rating, double after_rating, Novel novel){
        this.rating = after_rating;
        novel.modifyRating(before_rating, after_rating);
    }

    //리뷰 내용 수정 메소드
    public void modifyContent(String content){
        this.content = content;
    }
}
