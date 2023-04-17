package NovelForm.NovelForm.domain.Box;

import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class BoxItem extends BaseEntityTime {
    @Id
    @GeneratedValue
    @Column(name = "box_item_id")
    private Long id;

    @Column
    private String status;

    @Column
    private int is_lead_item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_id")
    private Box box;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id")
    private Novel novel;

}
