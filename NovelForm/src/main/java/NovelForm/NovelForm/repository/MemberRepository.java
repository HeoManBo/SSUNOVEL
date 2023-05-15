package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 닉네임으로 멤버 찾기
     * 
     * @param nickname
     * @return
     */
    @Query("select m from Member m where m.nickname = :nickname")
    Member findByNickname(@Param("nickname") String nickname);


    /**
     * email 로 멤버 찾기
     * 
     * @param email
     * @return
     */
    @Query("select m from Member m where m.email = :email")
    Member findByEmail(@Param("email") String email);

    /**
     * 멤버 객체와 해당 멤버가 작성한 리뷰리스트 가져오기 -> 제거를 위해
     */
    //@Query("select m from Member m join fetch m.reviews rs join fetch rs.novel where m.id = :member_id") //가능한 쿼리일까?
    @Query("select m from Member m join fetch m.reviews where m.id = :member_id")
    Member findByMemberIdWithReviews(@Param("member_id") Long member_id);

}
