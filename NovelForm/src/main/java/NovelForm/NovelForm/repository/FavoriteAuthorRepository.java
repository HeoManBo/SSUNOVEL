package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.favorite.domain.FavoriteAuthor;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Author;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FavoriteAuthorRepository extends JpaRepository<FavoriteAuthor, Long> {

    @Query("select fa from FavoriteAuthor fa join fetch fa.author where fa.member =:member")
    List<FavoriteAuthor> findByMember(@Param("member") Member member, PageRequest pageRequest);


    @Query("select fa from FavoriteAuthor fa join fetch fa.author where fa.member =:member and fa.author =:author")
    FavoriteAuthor findByMemberWithAuthor(@Param("member") Member member, @Param("author") Author author);
}
