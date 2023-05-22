package NovelForm.NovelForm.domain.community;


import NovelForm.NovelForm.domain.community.dto.DetailPostDto;
import NovelForm.NovelForm.domain.community.dto.PostDto;
import NovelForm.NovelForm.domain.community.dto.WriteDto;
import NovelForm.NovelForm.domain.community.exception.NoPostException;
import NovelForm.NovelForm.domain.community.exception.NotPostOwner;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.member.exception.WrongMemberException;
import NovelForm.NovelForm.repository.CommunityPostRepository;
import NovelForm.NovelForm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static NovelForm.NovelForm.domain.novel.NovelService.PagingSize;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommunityService {

    private final CommunityPostRepository communityPostRepository;
    private final MemberRepository memberRepository;

    /**
     * 게시글 등록을 합니다.
     * @return : 등록한 게시글 번호
     */
    public Long writePost(WriteDto writeDto, Long memberId) {
        // 멤버를 찾아옴
        Optional<Member> byId = memberRepository.findById(memberId);
        if(byId.isEmpty()){ //멤버를 찾을 수 없는 경우
            return -1L;
        }
        Member member = byId.get();

        //게시글 객체를 만들어 DB에 저장한다.
        CommunityPost communityPost = CommunityPost
                .builder()
                .title(writeDto.getTitle())
                .content(writeDto.getContent()).build();

        //게시글 작성자 id 등록
        communityPost.addMember(member);

        log.info("writer : {}, title : {}, content", member.getNickname(), communityPost.getTitle(), communityPost.getContent());

        CommunityPost save = communityPostRepository.save(communityPost);

        //등록 번호 반환
        return save.getId();
    }


    /**
     * 페이지 번호에 맞는 게시글 리스트 반환
     */
    @Transactional(readOnly = true)
    public List<PostDto> totalPost(int pageNum) {
        PageRequest page = PageRequest.of(pageNum, PagingSize);//최신순으로 내림차순 등록을 한다

        Page<CommunityPost> postListWithPaging = communityPostRepository.findPostListWithPaging(page);

        if(postListWithPaging.getNumberOfElements() == 0){ //현재 페이지에 조회될 게시글이 0개라면
            return null;
        }

        //현재 페이지에 해당하는 게시글 리스트 반환
        return postListWithPaging.stream().
                map(c -> new PostDto(c.getId(), c.getTitle(), c.getMember().getNickname())).toList();

    }

    /**
     *  상세 게시글 조회 로직입니다. 상세 조회 결과 dto를 반환합니다
     */
    @Transactional(readOnly = true)
    public DetailPostDto getDetailPost(Long postId) {

        Optional<CommunityPost> detailPost = communityPostRepository.findDetailPost(postId);

        //해당 게시글이 없으면 null반환
        if(detailPost.isEmpty()){
                return null;
        }

        return new DetailPostDto(detailPost.get());
    }

    /**
     * 게시글 수정 로직입니다. 수정후 상세 조회 결과 dto를 반환합니다
     */
    public DetailPostDto modifyPost(Long postId, WriteDto writeDto, Long memberId) throws Exception {
        Optional<Member> byMemberId = memberRepository.findById(memberId);
        Optional<CommunityPost> byPostId = communityPostRepository.findDetailPost(postId);

        if(byMemberId.isEmpty()){ //해당하는 멤버가 없다면
            throw new WrongMemberException("잘못된 유저 아이디입니다.");
        }

        if(byPostId.isEmpty()){ //postId에 대한 Post가 DB에 없으면  --> 잘못된 Pathvariable
            throw new NoPostException();
        }

        Member member = byMemberId.get();
        CommunityPost communityPost = byPostId.get();

        log.info("owner id : {}, sesseion id : {}", memberId.longValue(), communityPost.getId());

        //로그인한 사람이 자기가 작성한 글이 아닌 다른 사람의 글을 삭제하려는 경우
        if(!member.getId().equals(communityPost.getId())){
            throw new NotPostOwner();
        }

        //게시글 수정
        communityPost.changeContent(writeDto.getContent());
        communityPost.changeTitle(writeDto.getTitle());


        //수정된 상세 게시글 조회 dto 반환
        return new DetailPostDto(communityPost);
    }

    /**
     * 게시글 삭제 로직입니다.
     */
    public String deletePost(Long memberId, Long postId) throws Exception{
        Optional<Member> byMemberId = memberRepository.findById(memberId);
        Optional<CommunityPost> byPostId = communityPostRepository.findDetailPost(postId);

        if(byMemberId.isEmpty()){ //해당하는 멤버가 없다면
            throw new WrongMemberException("잘못된 유저 아이디입니다.");
        }

        if(byPostId.isEmpty()){ //postId에 대한 Post가 DB에 없으면  --> 잘못된 Pathvariable
            throw new NoPostException();
        }

        Member member = byMemberId.get();
        CommunityPost communityPost = byPostId.get();

        //로그인한 사람이 자기가 작성한 글이 아닌 다른 사람의 글을 삭제하려는 경우
        if(member.getId() != communityPost.getId()){
            throw new NotPostOwner();
        }

        communityPostRepository.deleteById(communityPost.getId());


        return "삭제 완료";
    }

    /**
     * 검색어가 주어졌을 때 검색어에 해당되는 게시물을 찾아옵니다.
     */
    @Transactional(readOnly = true)
    public List<PostDto> keywordPost(String keyword) {
        List<CommunityPost> communityPostWithKeyword = communityPostRepository.findCommunityPostWithKeyword(keyword);

        if(communityPostWithKeyword.size() == 0){ //검색 조건에 맞는 게시글이 없으면
            return null;
        }

        return communityPostWithKeyword.stream().
                map(c -> new PostDto(c.getId(), c.getTitle(), c.getMember().getNickname())).toList();
    }
}
