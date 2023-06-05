package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.box.domain.BoxItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoxItemRepository extends JpaRepository<BoxItem, Long> {

    /**
     * boxItem 가져오기
     * @param boxId
     * @param pageRequest
     * @return
     */
    @Query(value = "select bi from Box b inner join b.boxItems bi where b.id = :boxId",
            countQuery = "select count(bi) from Box b inner join b.boxItems bi where b.id = :boxId")
    Page<BoxItem> findBoxItemsWithBoxId(@Param("boxId") Long boxId, PageRequest pageRequest);

}
