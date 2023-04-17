package NovelForm.NovelForm.domain.Box;


import NovelForm.NovelForm.domain.member.Member;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Box extends BaseEntityTime {
    @Id
    @GeneratedValue
    @Column(name = "box_id")
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
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL)
    private List<BoxItem> boxItems = new ArrayList<>();

}
