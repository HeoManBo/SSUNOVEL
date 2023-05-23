package NovelForm.NovelForm.domain.community;


import NovelForm.NovelForm.domain.box.exception.WrongMemberException;
import NovelForm.NovelForm.domain.comment.Comment;
import NovelForm.NovelForm.domain.community.dto.CommentDto;
import NovelForm.NovelForm.domain.community.dto.CreateCommentDto;
import NovelForm.NovelForm.domain.community.exception.NoPostException;
import NovelForm.NovelForm.domain.community.exception.WrongCommentException;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.repository.CommentRepository;
import NovelForm.NovelForm.repository.CommunityPostRepository;
import NovelForm.NovelForm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommunityPostRepository communityPostRepository;
    private final MemberRepository memberRepository;

    // 댓글 등록 서비스 로직입니다.
    public Long createComment(CreateCommentDto createCommentDto, Long post_id, Long member_id) throws Exception{

        //해당 post_id에 대응하는 게시글이 있는지 확인
        Optional<CommunityPost> findPost = communityPostRepository.findDetailPost(post_id);
        Optional<Member> findMember = memberRepository.findById(member_id);

        //게시글이 없으면 error
        if(findPost.isEmpty()){
            throw new NoPostException();
        }
        //멤버가 없으면 error
        if(findMember.isEmpty()){
            throw new WrongMemberException("잘못된 유저 아이디입니다.");
        }

        //댓글을 생성하여 댓글 레포지토리에 저장한다.
        CommunityPost cp = findPost.get();
        Member m  = findMember.get();

        Comment comment = Comment.builder()
                .content(createCommentDto.getContent())
                .build();

        comment.addCommunityPost(cp);
        comment.addMember(m);

        log.info("comment writer : {}, comment = {}, where comment is written = {}", m.getId(), comment.getContent(), cp.getId());

        Comment ret = commentRepository.save(comment);

        return ret.getId();
    }



    //댓글 수정 서비스 로직입니다.
    public CommentDto updateComment(CreateCommentDto createCommentDto, Long memberId, Long commentId) throws Exception {
        Optional<Member> findMember = memberRepository.findById(memberId);
        Optional<Comment> findComment = commentRepository.findCommentByIdWithMemberAndPost(commentId);

        //멤버가 없는 경우
        if(findMember.isEmpty()){
            throw new WrongMemberException("잘못된 유저 아이디입니다.");
        }

        //수정할 댓글이 없는 경우
        if(findComment.isEmpty()){
            throw new WrongCommentException("댓글을 찾을 수 없습니다.");
        }

        Member m = findMember.get();
        Comment c = findComment.get();

        //댓글 작성자의 id와 세션 멤버 id가 같은지 확인한다.
        if(!m.getId().equals(c.getMember().getId())){
            throw new WrongCommentException("해당 댓글 수정 권한이 없는 사용자의 요청입니다.");
        }

        //수정을 진행한다.
        c.updateComment(createCommentDto);


        //수정된 commentDto를 반환한다.
        return new CommentDto(c);
    }

    //댓글 삭제 서비스 로직입니다.
    public String deleteComment(Long memberId, Long postId, Long commentId) throws Exception{
        Optional<Member> findMember = memberRepository.findById(memberId);
        Optional<Comment> findComment = commentRepository.findCommentByIdWithMemberAndPost(commentId);

        //멤버가 없는 경우
        if(findMember.isEmpty()){
            throw new WrongMemberException("잘못된 유저 아이디입니다.");
        }

        //삭제할 댓글이 없는 경우
        if(findComment.isEmpty()){
            throw new WrongCommentException("댓글을 찾을 수 없습니다.");
        }

        Member m = findMember.get();
        Comment c = findComment.get();

        //댓글 작성자의 id와 세션 멤버 id가 같은지 확인한다.
        if(!m.getId().equals(c.getMember().getId())){
            throw new WrongCommentException("해당 댓글 삭제 권한이 없는 사용자의 요청입니다.");
        }

        m.getComments().remove(c); //작성자와의 연관 관계 해제
        c.getCommunityPost().getComments().remove(c); ////게시글와의 연관 관계 해제
        commentRepository.deleteById(c.getId());

        return "success";
    }

}
