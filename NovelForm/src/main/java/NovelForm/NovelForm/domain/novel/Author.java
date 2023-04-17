package NovelForm.NovelForm.domain.novel;

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
public class Author extends BaseEntityTime {
    @Id
    @GeneratedValue
    @Column(name = "author_id")
    private Long id;

    @Column
    private String name;

    @Column
    private String status;


    // Author 입장에서 한 명의 작가가 여러 개의 작품을 가질 수 있으므로 1 : N;
    // Author 가 제거돌 때 Author가 만든 웹 소설 삭제
    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    List<Novel> novels = new ArrayList<>();

    @Builder
    public Author(String name) {
        this.name = name;
    }


    //연관관계 메소드
    public void addNovel(Novel novel){
        novels.add(novel);
    }
}
