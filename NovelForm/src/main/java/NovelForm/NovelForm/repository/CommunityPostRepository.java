package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.community.CommunityPost;
import NovelForm.NovelForm.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {


    /**
     * 커뮤니티에 작성한 글 모두 삭제하기
     */
    @Modifying(clearAutomatically = true)
    //@Query("delete from CommunityPost cp where cp.member = :member")
    void deleteAllByMember(Member member);
}
