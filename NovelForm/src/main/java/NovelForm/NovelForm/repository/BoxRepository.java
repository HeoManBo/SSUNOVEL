package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.box.domain.BoxItem;
import NovelForm.NovelForm.domain.box.dto.AllBoxResponse;
import NovelForm.NovelForm.domain.box.dto.BoxSearchInfo;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.member.dto.MemberBoxInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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


    /**
     * Member 객체와 보관함 id로 해당 회원이 작성한 보관함 찾아오기
     *
     * @param boxId
     * @param findMember
     * @return
     */
    @Query("select b from Box b where b.id = :boxId and b.member = :findMember")
    Optional<Box> findExistBoxWithBoxIDAndMember(@Param("boxId") Long boxId, @Param("findMember") Member findMember);


    /**
     * memberId로 해당 회원이 작성한 보관함 찾아오기
     */
    List<Box> findBoxesByMemberId(Long memberId);


    /**
     * 특정 회원이 작성한 모든 보관함 삭제
     */
    @Modifying(clearAutomatically = true)
    @Query("delete from Box b where b.member = :member ")
    void bulkDeleteBoxByMember(@Param("member") Member member);



    /**
     * 내가 작성한 보관함 가져오기
     * @return
     */
    @Query("select new NovelForm.NovelForm.domain.member.dto.MemberBoxInfo(" +
            " b.id, " +
            " b.title, " +
            " m.nickname, " +
            " (select n.cover_image from BoxItem bi2 inner join bi2.novel n where bi2.box = b and bi2.is_lead_item = 1), " +
            " b.update_at, " +
            " count(bi), " +
            " (select cast(count(l) as integer) from Like l inner join l.box b2 on b2 = b), " +
            " b.is_private )" +
            " from Box b " +
            " inner join b.member m " +
            " left join b.boxItems bi " +
            " where m = :member" )
    List<MemberBoxInfo> findMemberBoxByMember(@Param("member") Member member);


    /**
     * 좋아요가 없을 때도 동작할 좋아요 개수에 따른 보관함 가져오기
     *  내림차순
     * @param pageRequest
     * @return
     */
    @Query(value = "select new NovelForm.NovelForm.domain.box.dto.AllBoxResponse( " +
            " b.title, " +
            " m.nickname, " +
            " (select n.cover_image from BoxItem bi2 inner join bi2.novel n where bi2.box = b and bi2.is_lead_item = 1), " +
            " b.id," +
            " b.update_at, " +
            " count(distinct bi), " +
            " count(distinct l)) " +
            " from Box b " +
            " left join b.likes l " +
            " inner join b.member m " +
            " inner join b.boxItems bi " +
            " where b.is_private = 0 " +
            " group by b " +
            " order by count(l) DESC ",

            countQuery = " select count(distinct l) " +
                    " from Box b " +
                    " left join b.likes l " +
                    " inner join b.member m " +
                    " inner join b.boxItems bi " +
                    " where b.is_private = 0 " +
                    " group by b "
    )
    Page<AllBoxResponse> findAllBoxByPublicWithLikeDesc(PageRequest pageRequest);


    /**
     * 좋아요가 없을 때도 동작할 좋아요 개수에 따른 보관함 가져오기
     *  내림차순
     * @param pageRequest
     * @return
     */
    @Query(value = "select new NovelForm.NovelForm.domain.box.dto.AllBoxResponse( " +
            " b.title, " +
            " m.nickname, " +
            " (select n.cover_image from BoxItem bi2 inner join bi2.novel n where bi2.box = b and bi2.is_lead_item = 1), " +
            " b.id," +
            " b.update_at, " +
            " count(distinct bi), " +
            " count(distinct l)) " +
            " from Box b " +
            " left join b.likes l " +
            " inner join b.member m " +
            " inner join b.boxItems bi " +
            " where b.is_private = 0 " +
            " group by b " +
            " order by count(l)",

            countQuery = " select count(distinct l) " +
                    " from Box b " +
                    " left join b.likes l " +
                    " inner join b.member m " +
                    " inner join b.boxItems bi " +
                    " where b.is_private = 0 " +
                    " group by b "
    )
    Page<AllBoxResponse> findAllBoxByPublicWithLike(PageRequest pageRequest);
}
