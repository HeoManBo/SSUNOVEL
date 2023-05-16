package NovelForm.NovelForm.domain.like;

import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.like.domain.Like;
import NovelForm.NovelForm.domain.like.exception.DuplicateAddLikeException;
import NovelForm.NovelForm.domain.like.exception.WrongBoxException;
import NovelForm.NovelForm.domain.like.exception.WrongMemberException;
import NovelForm.NovelForm.domain.like.exception.WrongReviewException;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.review.domain.Review;
import NovelForm.NovelForm.repository.BoxRepository;
import NovelForm.NovelForm.repository.LikeRepository;
import NovelForm.NovelForm.repository.MemberRepository;
import NovelForm.NovelForm.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {


    private final LikeRepository likeRepository;

    private final MemberRepository memberRepository;

    private final BoxRepository boxRepository;

    private final ReviewRepository reviewRepository;

    public Long createBoxLike(Long memberId, Long boxId) throws Exception {

        Member member;
        Box box;

        // 전달 받은 사용자 번호 및 보관함 번호 체크
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Optional<Box> optionalBox = boxRepository.findById(boxId);

        if(!optionalBox.isPresent()){
            throw new WrongBoxException("잘못된 보관함 번호: " + boxId);
        }

        if(!optionalMember.isPresent()){
            throw new WrongMemberException("잘못된 사용자 번호: " + memberId);
        }


        member = optionalMember.get();
        box = optionalBox.get();

        // 이미 등록한 좋아요인지 체크
        List<Like> likeList = likeRepository.findLikesByMemberAndBox(member, box);


        if(!likeList.isEmpty()){
            Map<String, Long> errorFieldMap = new HashMap<>();

            errorFieldMap.put("memberId", memberId);
            errorFieldMap.put("boxId", boxId);

            throw new DuplicateAddLikeException(errorFieldMap);
        }

        
        // 문제 없으면 저장
        Like like = new Like(member, box);
        Like save = likeRepository.save(like);

        return save.getId();
    }

    public Long createReviewLike(Long memberId, Long reviewId) throws Exception {

        Member member;
        Review review;

        // 전달 받은 사용자번호와 리뷰 번호가 정확한지 체크
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);


        if(!optionalMember.isPresent()){
            throw new WrongMemberException("잘못된 사용자 번호: "+ memberId);
        }

        if(!optionalReview.isPresent()){
            throw new WrongReviewException("잘못된 리뷰 번호: " + reviewId);
        }

        // 문제 없으면 객체를 가져오기
        member = optionalMember.get();
        review = optionalReview.get();


        // 이미 등록한 좋아요인지 확인
        List<Like> likeList = likeRepository.findLikesByMemberAndReview(member, review);

        if(!likeList.isEmpty()){
            Map<String, Long> errorFieldMap = new HashMap<>();
            errorFieldMap.put("memberId", member.getId());
            errorFieldMap.put("reviewId", review.getId());

            throw new DuplicateAddLikeException(errorFieldMap);
        }


        Like like = new Like(member, review);
        Like save = likeRepository.save(like);

        return save.getId();
    }
}
