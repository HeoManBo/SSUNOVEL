package NovelForm.NovelForm.domain.favorite;


import NovelForm.NovelForm.domain.member.Member;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class FavoriteNovel extends BaseEntityTime {
    @Id
    @GeneratedValue
    @Column(name = "favorite_novel_id")
    private Long id;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id")
    private Novel novel;

    //양뱡향 관계를 위해 두 테이블에 모두 더해줌
    public void addMember(Member member){
        this.member = member;
        member.addFavoriteNovel(this);
    }

    public void addNovel(Novel novel){
        this.novel = novel;
    }
}
