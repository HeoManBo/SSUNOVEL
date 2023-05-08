package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.box.dto.AllBoxResponse;
import NovelForm.NovelForm.domain.box.dto.BoxSearchInfo;
import NovelForm.NovelForm.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface BoxRepository extends JpaRepository<Box, Long> {

    /**
     * 전체 목록 가져오기
     * @return
     */
    @Query("select new NovelForm.NovelForm.domain.box.dto.AllBoxResponse(b.title, m.nickname, n.cover_image, b.id)" +
            " from Box b " +
            " inner join b.member m " +
            " inner join b.boxItems bi on bi.is_lead_item = 1 " +
            " inner join bi.novel n" +
            " where b.is_private = 0 ")
    List<AllBoxResponse> findAllBoxByPublic();


    /**
     * 보관함 내부의 작품 목록 가져오기
     *
     * @param boxId
     * @return
     */
    @Query("select b from Box b inner join fetch b.boxItems bi where b.id = :boxId")
    Box findBoxWithBoxItems(@Param("boxId") Long boxId);


    @Query("select new NovelForm.NovelForm.domain.box.dto.BoxSearchInfo(b.id, b.title, m.nickname, n.cover_image) " +
            " from Box b " +
            " inner join b.member m " +
            " inner join b.boxItems bi on bi.is_lead_item = 1 " +
            " inner join bi.novel n" +
            " where b.is_private = 0 and b.title like :search")
    List<BoxSearchInfo> findBoxByTitle(@Param("search") String search);



    @Query("select new NovelForm.NovelForm.domain.box.dto.BoxSearchInfo(b.id, b.title, m.nickname, n.cover_image) " +
            " from Box b " +
            " inner join b.member m " +
            " inner join b.boxItems bi on bi.is_lead_item = 1 " +
            " inner join bi.novel n" +
            " where b.is_private = 0 and m.nickname like :search")
    List<BoxSearchInfo> findBoxByMember(@Param("search") String search);


    @Query("select b from Box b where b.id = :boxId and b.member = :findMember")
    Optional<Box> findExistBoxWithBoxIDAndMember(@Param("boxId") Long boxId, @Param("findMember") Member findMember);
}
