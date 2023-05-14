package NovelForm.NovelForm.domain.box.domain;

import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.global.BaseEntityTime;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class BoxItem extends BaseEntityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "box_item_idx")
    private Long id;

    @Column
    private String status;

    @Column
    private Integer is_lead_item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_idx")
    private Box box;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_idx")
    private Novel novel;


    public BoxItem(Integer is_lead_item, Novel novel) {
        this.is_lead_item = is_lead_item;
        this.novel = novel;
        this.status = "activated";
    }

    public BoxItem() {

    }

    public void setBox(Box box) {
        this.box = box;
    }
}
