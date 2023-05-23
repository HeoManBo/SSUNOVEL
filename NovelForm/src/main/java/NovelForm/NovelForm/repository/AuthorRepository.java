package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // 작가 이름으로 검색
    @Query("select au from Author au where au.name = :authorName")
    public Author findByAuthorName(@Param("authorName") String authorName);



    // 즐겨찾기한 작가 정보 검색
    // 해당 작가의 소설 정보도 같이 가져오기
    @Query("select distinct a from FavoriteAuthor fa inner join fa.author a join fetch a.novels where fa.member =:member")
    List<Author> findAuthorsByMemberFavorite(@Param("member") Member member);
}
