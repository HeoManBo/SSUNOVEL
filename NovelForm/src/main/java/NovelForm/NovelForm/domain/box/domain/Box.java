package NovelForm.NovelForm.domain.box.domain;


import NovelForm.NovelForm.domain.like.domain.Like;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
public class Box extends BaseEntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "box_idx")
    private Long id;

    @Column
    private String status;

    @Column(length = 100)
    private String title;

    @Column
    @Lob
    private String content;

    @Column
    private int is_private;

    // 여러 box는 하나의 멤버가 만들 수 있으므로 ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member member;


    @OneToMany(mappedBy = "box", cascade = CascadeType.REMOVE)
    private List<Like> likes = new ArrayList<>();

    // boxItem은 box가 그 생명주기를 모두 관리한다.
    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoxItem> boxItems = new ArrayList<>();

    public Box() {

    }


    public void addBoxItem(BoxItem boxItem){
        this.boxItems.add(boxItem);
        boxItem.setBox(this);
    }


    public Box(String title, String content, int is_private, Member member) {
        this.title = title;
        this.content = content;
        this.is_private = is_private;
        this.member = member;
        this.status = "activated";
    }
}
