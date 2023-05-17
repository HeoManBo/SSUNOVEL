package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteBox;
import NovelForm.NovelForm.domain.member.domain.Member;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FavoriteBoxRepository extends JpaRepository<FavoriteBox, Long> {


    @Query("select fb from FavoriteBox fb join fetch fb.box where fb.member = :member")
    List<FavoriteBox> findByMember(@Param("member")Member member, PageRequest pageRequest);

    @Query("select fb from FavoriteBox fb join fetch fb.box where fb.member = :member and fb.box = :box")
    FavoriteBox findByMemberWithBox(@Param("member")Member member, @Param("box") Box box);


    @Modifying(clearAutomatically = true)
    void deleteAllByMember(Member member);
}
