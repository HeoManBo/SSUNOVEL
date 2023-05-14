package NovelForm.NovelForm.domain.favorite;


import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteAuthor;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteBox;
import NovelForm.NovelForm.domain.favorite.domain.FavoriteNovel;
import NovelForm.NovelForm.domain.favorite.exception.*;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteNovelRepository favoriteNovelRepository;

    private final FavoriteAuthorRepository favoriteAuthorRepository;

    private final FavoriteBoxRepository favoriteBoxRepository;

    private final MemberRepository memberRepository;

    private final BoxRepository boxRepository;
    private final NovelRepository novelRepository;

    private final AuthorRepository authorRepository;


    /**
     * 소설 즐겨찾기 서비스 로직
     *
     * @param memberId
     * @param novelId
     * @return
     * @throws Exception
     */
    public Long createFavoriteNovel(Long memberId, Long novelId) throws Exception {

        Member findMember;
        Novel findNovel;

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Optional<Novel> optionalNovel = novelRepository.findById(novelId);


        if (!optionalMember.isPresent()){
            throw new WrongMemberException("잘못된 사용자 번호: " + memberId);
        }

        if (!optionalNovel.isPresent()){
            throw new WrongNovelException("잘못된 소설 번호: " + novelId);
        }

        findMember = optionalMember.get();
        findNovel = optionalNovel.get();

        FavoriteNovel byMemberWithNovel = favoriteNovelRepository.findByMemberWithNovel(findMember, findNovel);

        if (byMemberWithNovel != null){
            throw new DuplicateFavoriteException("즐겨찾기 번호: " + byMemberWithNovel.getId());
        }



        FavoriteNovel favoriteNovel = new FavoriteNovel(findNovel);
        favoriteNovel.addMember(findMember);

        FavoriteNovel save = favoriteNovelRepository.save(favoriteNovel);
        return save.getId();
    }


    /**
     * 보관함 즐겨찾기 추가
     *
     * @param memberId

     * @return
     */
    public Long createFavoriteBox(Long memberId, Long boxId) throws Exception {

        Member findMember;
        Box findBox;

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Optional<Box> optionalBox = boxRepository.findById(boxId);

        if(!optionalMember.isPresent()){
            throw new WrongMemberException("잘못된 사용자 번호: " + memberId);
        }

        if(!optionalBox.isPresent()){
            throw new WrongBoxException("잘못된 보관함 번호: " + boxId);
        }


        findMember = optionalMember.get();
        findBox = optionalBox.get();

        FavoriteBox byMemberWithBox = favoriteBoxRepository.findByMemberWithBox(findMember, findBox);
        if(byMemberWithBox != null){
            throw new DuplicateFavoriteException("즐겨찾기 번호: " + byMemberWithBox.getId());
        }


        FavoriteBox favoriteBox = new FavoriteBox(findBox);
        favoriteBox.addMember(findMember);

        FavoriteBox save = favoriteBoxRepository.save(favoriteBox);
        return save.getId();
    }


    /**
     * 작가 즐겨찾기 등록 서비스 로직
     *
     * @param memberId
     * @param authorId
     * @return
     */
    public Long createFavoriteAuthor(Long memberId, Long authorId) throws Exception {

        Author findAuthor;
        Member findMember;

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Optional<Author> optionalAuthor = authorRepository.findById(authorId);

        if(!optionalMember.isPresent()){
            throw new WrongMemberException("잘못된 사용자 번호: " + memberId);
        }

        if(!optionalAuthor.isPresent()){
            throw new WrongAuthorException("잘못된 작가 번호: " + authorId);
        }

        findMember = optionalMember.get();
        findAuthor = optionalAuthor.get();

        FavoriteAuthor byMemberWithAuthor = favoriteAuthorRepository.findByMemberWithAuthor(findMember, findAuthor);
        if(byMemberWithAuthor != null){
            throw new DuplicateFavoriteException("즐겨찾기 번호: " + byMemberWithAuthor.getId());
        }

        FavoriteAuthor favoriteAuthor = new FavoriteAuthor(findAuthor);
        favoriteAuthor.addMember(findMember);

        FavoriteAuthor save = favoriteAuthorRepository.save(favoriteAuthor);
        return save.getId();
    }


    /**
     * 즐겨찾기로 등록한 소설 삭제
     *
     * @param memberId
     * @param novelId
     */
    public void delFavoriteNovel(Long memberId, Long novelId) throws Exception {

        Member findMember;
        Novel findNovel;

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Optional<Novel> optionalNovel = novelRepository.findById(novelId);


        if (!optionalMember.isPresent()){
            throw new WrongMemberException("잘못된 사용자 번호: " + memberId);
        }

        if (!optionalNovel.isPresent()){
            throw new WrongNovelException("잘못된 소설 번호: " + novelId);
        }

        findMember = optionalMember.get();
        findNovel = optionalNovel.get();

        FavoriteNovel byMemberWithNovel = favoriteNovelRepository.findByMemberWithNovel(findMember, findNovel);

        // 없으면, 예외
        if(byMemberWithNovel == null){
            throw new WrongFavoriteAccessException();
        }

        favoriteNovelRepository.delete(byMemberWithNovel);
    }


    /**
     * 보관함 즐겨찾기 삭제 서비스 로직
     *
     * @param memberId
     * @param boxId
     */
    public void delFavoriteBox(Long memberId, Long boxId) throws Exception {

        Member findMember;
        Box findBox;

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Optional<Box> optionalBox = boxRepository.findById(boxId);

        if(!optionalMember.isPresent()){
            throw new WrongMemberException("잘못된 사용자 번호: " + memberId);
        }

        if(!optionalBox.isPresent()){
            throw new WrongBoxException("잘못된 보관함 번호: " + boxId);
        }


        findMember = optionalMember.get();
        findBox = optionalBox.get();

        FavoriteBox byMemberWithBox = favoriteBoxRepository.findByMemberWithBox(findMember, findBox);

        if(byMemberWithBox == null){
            throw new WrongFavoriteAccessException();
        }

        favoriteBoxRepository.delete(byMemberWithBox);
    }


    /**
     * 작가 즐겨찾기 취소 서비스 로직
     */
    public void delFavoriteAuthor(Long memberId, Long authorId) throws Exception {

        Author findAuthor;
        Member findMember;

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Optional<Author> optionalAuthor = authorRepository.findById(authorId);

        if(!optionalMember.isPresent()){
            throw new WrongMemberException("잘못된 사용자 번호: " + memberId);
        }

        if(!optionalAuthor.isPresent()){
            throw new WrongAuthorException("잘못된 작가 번호: " + authorId);
        }

        findMember = optionalMember.get();
        findAuthor = optionalAuthor.get();

        FavoriteAuthor byMemberWithAuthor = favoriteAuthorRepository.findByMemberWithAuthor(findMember, findAuthor);

        if(byMemberWithAuthor == null){
            throw new WrongFavoriteAccessException();
        }

        favoriteAuthorRepository.delete(byMemberWithAuthor);
    }
}
