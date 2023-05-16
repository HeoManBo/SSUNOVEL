package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.box.dto.AllBoxResponse;
import NovelForm.NovelForm.domain.box.dto.BoxSearchInfo;
import NovelForm.NovelForm.domain.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoxRepository extends JpaRepository<Box, Long> {

    /**
     * 전체 목록 가져오기
     * @return
     */
    @Query(value = "select new NovelForm.NovelForm.domain.box.dto.AllBoxResponse(" +
                        " b.title, " +
                        " m.nickname, " +
                        " (select n.cover_image from BoxItem bi2 inner join bi2.novel n where bi2.box = b and bi2.is_lead_item = 1), " +
                        " b.id, " +
                        " b.update_at, " +
                        " count(bi), " +
                        " (select cast(count(l) as long) from Like l inner join l.box b2 on b2 = b) )" +
            " from Box b " +
            " inner join b.member m " +
            " left join b.boxItems bi" +
            " where b.is_private = 0 " +
            " group by b",

        countQuery = "select count(b) " +
                " from Box b " +
                " inner join b.member m " +
                " inner join b.boxItems bi" +
                " inner join bi.novel n " +
                " where b.is_private = 0 " +
                " group by b")
    Page<AllBoxResponse> findAllBoxByPublic(PageRequest pageRequest);


    /**
     * 보관함 내부의 작품 목록 가져오기
     *
     * @param boxId
     * @param pageRequest
     * @return
     */
    @Query(value = "select b from Box b inner join fetch b.boxItems bi where b.id = :boxId",
            countQuery = "select count(b) from Box b inner join fetch b.boxItems bi where b.id = :boxId"
    )
    Box findBoxWithBoxItems(@Param("boxId") Long boxId, PageRequest pageRequest);


    /**
     * 제목으로 검색
     * @param search
     * @return
     */
    @Query(value = "select new NovelForm.NovelForm.domain.box.dto.BoxSearchInfo(" +
            " b.id, " +
            " b.title, " +
            " m.nickname, " +
            " (select n.cover_image from BoxItem bi2 inner join bi2.novel n where bi2.box = b and bi2.is_lead_item = 1), " +
            " count(distinct bi), " +
            " count(distinct l) ) " +
            " from Box b " +
            " inner join b.member m " +
            " inner join b.boxItems bi " +
            " inner join bi.novel n " +
            " left join b.likes l " +
            " where b.is_private = 0 and b.title like %:search% " +
            " group by b ",

            countQuery = "select count(b) " +
                         " from Box b " +
                         " inner join b.member m " +
                         " inner join b.boxItems bi " +
                         " inner join bi.novel n " +
                         " left join b.likes l " +
                         " where b.is_private = 0 and b.title like %:search% " +
                         " group by b "
    )
    List<BoxSearchInfo> findBoxByTitle(@Param("search") String search, PageRequest pageRequest);


    /**
     * 보관함 생성자로 검색
     *
     * @param search
     * @param pageRequest
     * @return
     */
    @Query(value = "select new NovelForm.NovelForm.domain.box.dto.BoxSearchInfo(" +
            " b.id, " +
            " b.title, " +
            " m.nickname, " +
            " (select n.cover_image from BoxItem bi2 inner join bi2.novel n where bi2.box = b and bi2.is_lead_item = 1), " +
            " count(distinct bi), " +
            " count(distinct l) ) " +
            " from Box b " +
            " inner join b.member m " +
            " inner join b.boxItems bi " +
            " inner join bi.novel n " +
            " left join b.likes l" +
            " where b.is_private = 0 and m.nickname like %:search% " +
            " group by b",

            countQuery = " select count(b) " +
                         " from Box b " +
                         " inner join b.member m " +
                         " inner join b.boxItems bi" +
                         " inner join bi.novel n " +
                         " left join b.likes l " +
                         " where b.is_private = 0 and m.nickname like %:search% " +
                         " group by b"
    )
    List<BoxSearchInfo> findBoxByMember(@Param("search") String search, PageRequest pageRequest);


    @Query("select b from Box b where b.id = :boxId and b.member = :findMember")
    Optional<Box> findExistBoxWithBoxIDAndMember(@Param("boxId") Long boxId, @Param("findMember") Member findMember);



}