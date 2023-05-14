package NovelForm.NovelForm.domain.favorite.domain;

import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FavoriteBox extends BaseEntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_box_idx")
    private Long id;

    @Column
    private String status;

    @ManyToOne
    @JoinColumn(name = "member_idx")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "box_idx")
    private Box box;


    public void addMember(Member member){
        this.member = member;
        member.getFavoriteBoxes().add(this);
    }


    public FavoriteBox(Box box) {
        this.status = "activate";
        this.box = box;
    }
}
