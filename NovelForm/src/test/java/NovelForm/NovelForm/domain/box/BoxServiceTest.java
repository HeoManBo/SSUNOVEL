package NovelForm.NovelForm.domain.box;

import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.box.domain.BoxItem;
import NovelForm.NovelForm.domain.box.dto.CreateBoxRequest;
import NovelForm.NovelForm.domain.box.exception.NoSuchBoxItemException;
import NovelForm.NovelForm.domain.box.exception.WrongBoxException;
import NovelForm.NovelForm.domain.box.exception.WrongMemberException;
import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Author;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.repository.AuthorRepository;
import NovelForm.NovelForm.repository.BoxRepository;
import NovelForm.NovelForm.repository.MemberRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class BoxServiceTest {


    @Autowired
    BoxRepository boxRepository;

    @Autowired
    NovelRepository novelRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;


    @Test
    @DisplayName("보관함 수정 테스트")
    void updateBox() throws WrongMemberException, WrongBoxException {

        // given
        Member member = new Member("test@test.com", "asdasdasd", "cick", Gender.FEMALE, LoginType.USER,23);

        Author author = new Author("test Author");

        Novel novel = new Novel(
                "title",
                "summary",
                100,
                100,
                220,
                "연재중",
                "https://1211212.csr",
                3.8,
                29,
                "판타지",
                author,
                "sdsdsdsds",
                "kakakakak",
                "ririririri",
                "mmumumum"
        );

        Novel novel2 = new Novel(
                "title",
                "summary",
                100,
                100,
                220,
                "연재중",
                "https://1211212.csr",
                3.8,
                29,
                "판타지",
                author,
                "sdsdsdsds",
                "kakakakak",
                "ririririri",
                "mmumumum"
        );

        BoxItem boxItem = new BoxItem(1, novel);

        Box box = new Box("title", "content", 0, member);


        Long requestMemberId = memberRepository.save(member).getId();
        authorRepository.save(author);
        novelRepository.save(novel);
        Long requestBoxId = boxRepository.save(box).getId();

        Long testNovelId = novelRepository.save(novel2).getId();



        em.flush();
        em.clear();


        List<Long> updateList = new ArrayList<>();
        updateList.add(testNovelId);

        CreateBoxRequest createBoxRequest = new CreateBoxRequest(
                "update title",
                "update content",
                0,
                updateList,
                testNovelId
        );


        // when
        // 보관함 업데이트
        Optional<Member> optionalMember = memberRepository.findById(requestBoxId);
        Optional<Box> optionalBox = boxRepository.findById(requestBoxId);

        if(!optionalMember.isPresent()){
            throw new WrongMemberException();
        }

        if(!optionalBox.isPresent()){
            throw new WrongBoxException();
        }

        Member findMember = optionalMember.get();
        Box findBox = optionalBox.get();


        List<BoxItem> boxItemList = new ArrayList<>();

        // 보관함 아이템
        for (Long boxItemId : createBoxRequest.getBoxItems()) {

            Optional<Novel> optionalNovel = novelRepository.findById(boxItemId);
            Novel findNovel;



            // 없는 작품을 보관함에 넣으려고 하면, 실패
            if(optionalNovel.isPresent()){
                findNovel = novelRepository.findById(boxItemId).get();
            }
            else{
                throw new NoSuchBoxItemException("없는소설번호: " + boxItemId);
            }


            BoxItem updateBoxItem;

            if (boxItemId == createBoxRequest.getLeadItemId()){
                updateBoxItem = new BoxItem(1, findNovel);
            }
            else{
                updateBoxItem = new BoxItem(0, findNovel);
            }

            boxItemList.add(updateBoxItem);
        }

        findBox.updateBox(
                createBoxRequest.getTitle(),
                createBoxRequest.getContent(),
                createBoxRequest.getIs_private(),
                boxItemList
        );


        em.flush();
        em.clear();

        // then
        // 변경사항이 반영되었는지 확인

        Box updateBox = boxRepository.findById(requestBoxId).get();

        Assertions.assertThat(updateBox.getTitle()).isEqualTo(createBoxRequest.getTitle());
        Assertions.assertThat(updateBox.getContent()).isEqualTo(createBoxRequest.getContent());
        Assertions.assertThat(updateBox.getIs_private()).isEqualTo(createBoxRequest.getIs_private());

        for (BoxItem item : updateBox.getBoxItems()) {
            Assertions.assertThat(createBoxRequest.getBoxItems()).contains(item.getNovel().getId());
        }



    }


}
