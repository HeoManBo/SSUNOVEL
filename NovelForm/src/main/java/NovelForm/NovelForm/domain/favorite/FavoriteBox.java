package NovelForm.NovelForm.domain.favorite;

import NovelForm.NovelForm.domain.Box.Box;
import NovelForm.NovelForm.domain.member.Member;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class FavoriteBox extends BaseEntityTime {
    @Id
    @GeneratedValue
    @Column(name = "favorite_box_id")
    private Long id;

    @Column
    private String status;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "box_id")
    private Box box;


    public void addMember(Member member){
        this.member = member;
        member.getFavoriteBoxes().add(this);
    }



}
