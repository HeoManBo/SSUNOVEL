package NovelForm.NovelForm.domain.community;

import NovelForm.NovelForm.domain.comment.Comment;
import NovelForm.NovelForm.domain.community.dto.CreateCommentDto;
import NovelForm.NovelForm.domain.member.domain.Gender;
import NovelForm.NovelForm.domain.member.domain.LoginType;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.repository.CommentRepository;
import NovelForm.NovelForm.repository.CommunityPostRepository;
import NovelForm.NovelForm.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * 게시글/댓글 CRUD 테스트입니다.
 */

@SpringBootTest
@Transactional
@Slf4j
public class CommunityAndCommentTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CommunityPostRepository communityPostRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private EntityManager em;

    /**
     * 10명의 회원이 있다고 가정한다.
     * 그리고 각 회원이 5명의 게시글을 작성했다고 가정
     */
    @BeforeEach
    void MakeExample(){
        // 10명의 회원 생성
        List<Member> members = new ArrayList<>();
        for(int i=1; i<=10; i++){
            Member member = Member.builder()
                    .email(i + "@naver.com")
                    .password("1234")
                    .nickname("ssu" + i)
                    .gender(Gender.MALE)
                    .loginType(LoginType.USER)
                    .build();
            members.add(member);
        }

        List<CommunityPost> communities = new ArrayList<>();

        // 10 * 5, 총 50개의 게시글을 생성한다.
        for(int i=0; i<10; i++){
            for(int j=1; j<=5; j++) {
                CommunityPost communityPost = CommunityPost.builder()
                        .title((i + 1) + "번째유저의" + j + "번째 글")
                        .content("test")
                        .build();
                communityPost.addMember(members.get(i));
                communities.add(communityPost);
            }
        }

        //저장한다
        memberRepository.saveAll(members);
        communityPostRepository.saveAll(communities);

        em.flush();
        em.clear();
    }

    @Test
    void 게시글생성테스트(){
        //given : 10명의 유저가 5개씩 게시글을 작성한 상태이다.

        //when : 1번 유저가 게시글을 작성했다고 하자.
        Member ssu1 = memberRepository.findByNickname("ssu1");

        CommunityPost communityPost = CommunityPost.builder()
                .title("소설좀 찾아주세요").content("이게 뭔가요?").build();

        communityPost.addMember(ssu1);
        communityPostRepository.save(communityPost);

        em.flush();
        em.clear();

        //then : DB에서 방금 작성한 게시글을 가져와서 저장되었는지 확인하고 작성자의 post 수가 6개인지 확인한다.
        Optional<CommunityPost> byId = communityPostRepository.findById(communityPost.getId());
        Member findMember = memberRepository.findById(ssu1.getId()).get();
        List<CommunityPost> iter = findMember.getCommunityPosts();

        for (CommunityPost post : iter) {
            log.info("title = {}, content = {}", post.getTitle(),post.getContent());
        }


        if(byId.isEmpty()){
            fail("저장에 실패하였습니다");
        }
        else{
            CommunityPost findPost = byId.get();
            assertThat(findPost.getTitle()).isEqualTo(communityPost.getTitle());
            assertThat(findPost.getContent()).isEqualTo(communityPost.getContent());
            //멤버가 작성한 글의 수가 6개로 증가했는지 확인한다.
            assertThat(findMember.getCommunityPosts().size()).isEqualTo(6);
        }
    }

    @Test
    void 게시글조회테스트(){
        //given : beforeEach 수행
        //when : 1페이지 조회
        Pageable paging = PageRequest.of(1, 10);
        Page<CommunityPost> postListWithPaging = communityPostRepository.findPostListWithPaging(paging);
        List<CommunityPost> communityPosts = postListWithPaging.getContent();
        log.info("=========================================");
        for (CommunityPost communityPost : communityPosts) {
            log.info("title : {}, content : {}, nickname : {}, time : {}",
                    communityPost.getTitle(), communityPost.getContent(), communityPost.getMember().getNickname()
            , communityPost.getCreate_at());
        }

        //then : 전체 개시글 개수, 현재 가져온 게시글 개수를 확인한다.
        assertThat(postListWithPaging.getTotalElements()).isEqualTo(50); //beforeEach에서 50개 생성
        assertThat(postListWithPaging.getNumberOfElements()).isEqualTo(10); //현재 페이지에선 10개 가져와야함.
    }

    @Test
    void 게시글조회테스트2(){
        //given : 1번째 유저가 3개의 게시글을 올린다.
        Member ssu1 = memberRepository.findByNickname("ssu1");
        for(int i=0; i<3; i++){
            CommunityPost communityPost = CommunityPost.builder()
                    .title("test").content("test").build();
            communityPost.addMember(ssu1);
            communityPostRepository.save(communityPost);
        }
        em.clear();
        em.flush();

        //when : 마지막 페이지 조회 -> 53개의 게시글이 있으므로 1,2,3,4,5,6 / 페이징은 0번부터이므로 5번 페이지를 조회해본다ㅣ
        Pageable paging = PageRequest.of(5, 10);
        Page<CommunityPost> postListWithPaging = communityPostRepository.findPostListWithPaging(paging);
        List<CommunityPost> communityPosts = postListWithPaging.getContent();
        log.info("=========================================");
        for (CommunityPost communityPost : communityPosts) {
            log.info("title : {}, content : {}, nickname : {}, time : {}",
                    communityPost.getTitle(), communityPost.getContent(), communityPost.getMember().getNickname()
                    , communityPost.getCreate_at());
        }

        //then : 전체 개시글 개수, 현재 가져온 게시글 개수를 확인한다.
        assertThat(postListWithPaging.getTotalElements()).isEqualTo(53); //beforeEach에서 50개 given에서 3개
        assertThat(postListWithPaging.getNumberOfElements()).isEqualTo(3); //현재 페이지에선 3개 가져와야함.
    }

    @Test
    void 게시글수정테스트_제목(){
        //given : 2번째 유저가 주어졌을 때,
        Member ssu2 = memberRepository.findByNickname("ssu2");

        //when : 자신의 2번째 게시글 제목을 수정한다
        List<CommunityPost> memberPost = communityPostRepository.findMemberPost(ssu2);
        CommunityPost findPost = memberPost.get(1);

        findPost.changeTitle("testtest");

        //수정사항 DB 반영
        em.flush();
        em.clear();

        //다시 2번재 게시글을 가져온다.
        CommunityPost communityPost = communityPostRepository.findById(findPost.getId()).get();

        log.info("title : {}, content : {}", communityPost.getTitle(), communityPost.getContent());


        //then : DB에서 가져온 Post와 수정한 Post의 제목과 내용이 같은지 확인한다.
        assertThat(findPost.getId()).isEqualTo(communityPost.getId()); //같은 작성자인 경우
        assertThat(findPost.getTitle()).isEqualTo(communityPost.getTitle()); //제목이 같은지
        assertThat(findPost.getContent()).isEqualTo(communityPost.getContent());
    }

    @Test
    void 게시글수정테스트_내용(){
        //given : 2번째 유저가 주어졌을 때,
        Member ssu2 = memberRepository.findByNickname("ssu2");

        //when : 자신의 2번째 게시글 제목을 수정한다
        List<CommunityPost> memberPost = communityPostRepository.findMemberPost(ssu2);
        CommunityPost findPost = memberPost.get(1);

        findPost.changeContent("testtestTESTTEST");

        //수정사항 DB 반영
        em.flush();
        em.clear();

        //다시 2번재 게시글을 가져온다.
        CommunityPost communityPost = communityPostRepository.findById(findPost.getId()).get();

        log.info("title : {}, content : {}", communityPost.getTitle(), communityPost.getContent());


        //then : DB에서 가져온 Post와 수정한 Post의 제목과 내용이 같은지 확인한다.
        assertThat(findPost.getId()).isEqualTo(communityPost.getId()); //같은 작성자인 경우
        assertThat(findPost.getTitle()).isEqualTo(communityPost.getTitle()); //제목이 같은지
        assertThat(findPost.getContent()).isEqualTo(communityPost.getContent());
    }

    @Test
    void 게시글삭제테스트(){
        //given : 9번째 유저가 주어졌을 때,
        Member ssu9 = memberRepository.findByNickname("ssu9");

        //when : 자신의 3번째 게시글을 삭제한다.
        List<CommunityPost> memberPost = communityPostRepository.findMemberPost(ssu9);
        CommunityPost post3 = memberPost.get(2);

        log.info("post3 : id : {}", post3.getId());
        log.info("===============================");

        //삭제 후 즉시반영
        communityPostRepository.deleteById(post3.getId());

        em.flush();
        em.clear();

        //then : 전체 게시글이 49개가 되었는지, 9번째 유저가 작성한 게시글이 4개가 되었는지 확인한다.
        Member member = memberRepository.findById(ssu9.getId()).get();
        List<CommunityPost> communityPosts = communityPostRepository.findAll();

        assertThat(communityPosts.size()).isEqualTo(49);
        assertThat(member.getCommunityPosts().size()).isEqualTo(4);
    }

    @Test
    void 멤버삭제테스트(){
        //given : 10번유저가 주어졌을 때,
        Member ssu10 = memberRepository.findByNickname("ssu10");

        //when : 10번유저가 회원탈퇴를 한다고 해보자.
        communityPostRepository.deleteAllPostByMember(ssu10);
        memberRepository.delete(ssu10);

        em.flush();
        em.clear();

        //then : 게시글이 전체 50->45개가 됐는지 확인한다
        List<CommunityPost> all = communityPostRepository.findAll();

        assertThat(all.size()).isEqualTo(45);
    }

    @Test
    void 상세조회테스트(){
        //given : 5번 게시글이 존재할때,
        CommunityPost communityPost = communityPostRepository.findById(5L).get();

        //10번 유저가 5개의 댓글을 달았다고 가정한다. 단순화를 위해 한명이 5개 댓글을 달았다고 하자.
        Member ssu10 = memberRepository.findByNickname("ssu10");

        for(int i=1; i<=5; i++){
            Comment comment = Comment.builder()
                    .content("testtest").build();
            comment.addCommunityPost(communityPost);
            comment.addMember(ssu10);
            commentRepository.save(comment);
        }

        em.flush();
        em.clear();

        log.info("====================");

        //then : 5번 게시글을 상세 조회 해보자.
        //findDetailPost는 댓글에 대해 가져오진 않았지만
        //batch 전략으로 인해 한번에 쿼리로 댓글을 가져온다. -> N+1 문제 해결
        CommunityPost findPost = communityPostRepository.findDetailPost(5L).get();
        assertThat(findPost.getComments().size()).isEqualTo(5);
    }

    @Test
    void 댓글생성테스트(){
        //given : 1,2,3번 유저가 주어졌을 때,
        Member member = memberRepository.findById(2L).get();
        Member m1 = memberRepository.findById(1L).get();
        Member m2 = memberRepository.findById(3L).get();

        //when : 1번 유저의 3번 게시글에 댓글을 작성한다고 해보자.
        CommunityPost c = communityPostRepository.findDetailPost(3L).get();

        Comment comment1 = Comment.builder().content("그거 저도 기억날 것 같아요").build();
        comment1.addCommunityPost(c);
        comment1.addMember(member);

        Comment comment2 = Comment.builder().content("그거 저도 기억날 것 같아요").build();
        comment2.addCommunityPost(c);
        comment2.addMember(m1);

        Comment comment3 = Comment.builder().content("그거 저도 기억날 것 같아요").build();
        comment3.addCommunityPost(c);
        comment3.addMember(m2);

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        em.flush();
        em.clear();

        log.info("=========================================================");

        //when : 2번 유저와 3번 게시글을 DB에서 다시 조회했을 때 2번의 댓글 개수가 1개이고, 3번 게시글의 댓글 수가 3개인지 확인한다.
        Member fm = memberRepository.findById(2L).get();
        CommunityPost fc = communityPostRepository.findDetailPost(3L).get();

        log.info("=========================================================");

        assertThat(fm.getComments().size()).isEqualTo(1);
        assertThat(fc.getComments().size()).isEqualTo(3);
    }


    @Test
    void 댓글수정테스트(){
        //given : 1,2,3번 유저가 주어졌을 때,
        Member member = memberRepository.findById(2L).get();
        Member m1 = memberRepository.findById(1L).get();
        Member m2 = memberRepository.findById(3L).get();

        //when : 3번 댓글을 수정한다.
        CommunityPost c = communityPostRepository.findDetailPost(3L).get();

        Comment comment1 = Comment.builder().content("tttt").build();
        comment1.addCommunityPost(c);
        comment1.addMember(member);

        Comment comment2 = Comment.builder().content("test").build();
        comment2.addCommunityPost(c);
        comment2.addMember(m1);

        Comment comment3 = Comment.builder().content("testtest").build();
        comment3.addCommunityPost(c);
        comment3.addMember(m2);

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        em.flush();
        em.clear();

        Comment comment = commentRepository.findById(3L).get();
        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setContent("3214");
        comment.updateComment(createCommentDto);

        em.flush();
        em.clear();

        //then : 다시 DB에서 가져왔을 때, 수정한 값이 같은지 비교한다.
        Comment fc = commentRepository.findById(3L).get();

        assertThat(comment.getContent()).isEqualTo(fc.getContent());
    }


    @Test
    void 댓글삭제테스트(){
        //given : 1,2,3번 유저가 주어졌을 때, 3번 게시글의 각자 1개씩 댓글을 단다
        Member member = memberRepository.findById(2L).get();
        Member m1 = memberRepository.findById(1L).get();
        Member m2 = memberRepository.findById(3L).get();

        CommunityPost c = communityPostRepository.findDetailPost(3L).get();

        Comment comment1 = Comment.builder().content("tttt").build();
        comment1.addCommunityPost(c);
        comment1.addMember(member);

        Comment comment2 = Comment.builder().content("test").build();
        comment2.addCommunityPost(c);
        comment2.addMember(m1);

        Comment comment3 = Comment.builder().content("testtest").build();
        comment3.addCommunityPost(c);
        comment3.addMember(m2);

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        em.flush();
        em.clear();


        //when : 3번 게시글의 3번 댓글을 삭제한다.
        Comment comment = commentRepository.findCommentByIdWithMemberAndPost(3L).get();

        Member writer = comment.getMember();
        CommunityPost communityPost = comment.getCommunityPost();

        //연관관계 제거
        writer.getComments().remove(comment);
        communityPost.getComments().remove(comment);
        commentRepository.deleteById(comment.getId());

        em.flush();
        em.clear();

        log.info("===============================================================");

        //then : 3번 게시물과 3번 댓글을 작성한 3번 유저를 가져와서 댓글 수를 비교해본다
        Member fm = memberRepository.findById(3L).get();
        CommunityPost fc = communityPostRepository.findDetailPost(3L).get();

        assertThat(fm.getComments().size()).isEqualTo(0);
        assertThat(fc.getComments().size()).isEqualTo(2);

    }

}
