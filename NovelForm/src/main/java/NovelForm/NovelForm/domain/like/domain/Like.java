package NovelForm.NovelForm.domain.like.domain;

import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Review;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "great")
@NoArgsConstructor
@Getter
public class Like extends BaseEntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "great_idx")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_idx")
    private Box box;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_idx")
    private Review review;


    @Column(name = "great_type")
    private Integer likeType;

    public Like(Member member, Box box) {
        this.box = box;
        this.member = member;
        this.review = null;
        this.likeType = 2;
    }

    public Like(Member member, Review review) {
        this.member = member;
        this.review = review;
        this.box = null;
        this.likeType = 1;
    }
}
