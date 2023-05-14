package NovelForm.NovelForm.domain.like.domain;

import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;

@Entity
@Table(name = "great")
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

}
