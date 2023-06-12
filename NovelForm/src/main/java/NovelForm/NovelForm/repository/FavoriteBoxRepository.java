package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteBox;
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


@Repository
public interface FavoriteBoxRepository extends JpaRepository<FavoriteBox, Long> {

    // 사용자 객체를 이용해 즐겨찾기한 보관함 찾기
    @Query("select fb from FavoriteBox fb join fetch fb.box where fb.member = :member")
    List<FavoriteBox> findByMember(@Param("member")Member member);

    // 사용자 객체와 보관함 객체를 이용해서 보관함 찾기
    @Query("select fb from FavoriteBox fb join fetch fb.box where fb.member = :member and fb.box = :box")
    FavoriteBox findByMemberWithBox(@Param("member")Member member, @Param("box") Box box);


    // 사용자가 즐겨찾기한 모든 내역 삭제
    @Modifying(clearAutomatically = true)
    void deleteAllByMember(Member member);


    // 보관함이 삭제되면서 연관된 즐겨찾기 삭제
    @Modifying(clearAutomatically = true)
    void deleteAllByBox(Box box);



    // 즐겨찾기로 등록한 보관함 목록 가져오기
    @Query(value = "select new NovelForm.NovelForm.domain.member.dto.MemberBoxInfo(" +
            " b.id, " +
            " b.title, " +
            " m.nickname, " +
            " (select n.cover_image from BoxItem bi2 inner join bi2.novel n where bi2.box = b and bi2.is_lead_item = 1), " +
            " b.update_at, " +
            " count(bi), " +
            " (select cast(count(l) as integer) from Like l inner join l.box b2 on b2 = b), " +
            " b.is_private )" +
            " from FavoriteBox fb " +
            " inner join fb.box b " +
            " inner join b.member m " +
            " left join b.boxItems bi " +
            " where fb.member = :member" +
            " group by fb" ,

            countQuery = "select count(fb) " +
                    "       from FavoriteBox fb " +
                    "       inner join fb.box b " +
                    "       inner join b.member m " +
                    "       left join b.boxItems bi " +
                    "       where fb.member = :member" +
                    "       group by fb"
    )
    Page<MemberBoxInfo> findMemberFavoriteBoxByMember(@Param("member") Member member, PageRequest pageRequest);


    /**
     * 본인이 등록한 즐겨찾기 보관함 개수 가져오기
     */
    Integer countFavoriteBoxByMember(Member member);
}
