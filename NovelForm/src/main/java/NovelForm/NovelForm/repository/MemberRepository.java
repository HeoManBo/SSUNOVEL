package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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


}
