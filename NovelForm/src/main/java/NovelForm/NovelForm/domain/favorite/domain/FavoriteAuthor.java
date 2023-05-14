package NovelForm.NovelForm.domain.favorite.domain;


import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class FavoriteAuthor extends BaseEntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_author_idx")
    private Long id;

    private String status;


    @ManyToOne
    @JoinColumn(name = "member_idx")
    private Member member;


    @ManyToOne
    @JoinColumn(name = "author_idx")
    private Author author;



    // 연관관계 메소드
    public void addMember(Member member){
        this.member = member;
        member.getFavoriteAuthors().add(this);
    }

    public FavoriteAuthor(Author author) {
        this.status = "activated";
        this.author = author;
    }
}
