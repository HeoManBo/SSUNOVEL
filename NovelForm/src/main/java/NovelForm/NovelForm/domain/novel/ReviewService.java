package NovelForm.NovelForm.domain.novel;


import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.review.domain.Review;
import NovelForm.NovelForm.domain.novel.dto.detailnoveldto.ReviewDto;
import NovelForm.NovelForm.domain.novel.dto.reivewdto.ReviewBodyDto;
import NovelForm.NovelForm.repository.MemberRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import NovelForm.NovelForm.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final NovelRepository novelRepository;

    //파라미터로 들어온 소설에 작성된 리뷰를 반환한다.
    @Transactional(readOnly = true)
    public List<ReviewDto> findReviewMatchingNovel(Novel novel){
        List<Review> reviews = reviewRepository.findByReview(novel);
        if(reviews.size() == 0){
            return null; //공 List를 반환함
        }
        return reviews.stream().map(r -> new ReviewDto(r.getMember().getNickname(), r.getContent(), r.getRating(),
                r.getCreate_at(), r.getUp())).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    // novel에 리뷰를 작성한 member의 review 를 가져온다. 없다면 null로 반환.
    public Review findReviewWithMemberAndNovel(Novel novel, Member member){
        Optional<Review> singleReivew = reviewRepository.findSingleReivew(member, novel);
        return singleReivew.orElse(null);
    }

    /**
     *
     * 리뷰를 DB상에 등록합니다.
     * 리턴 값은 String 으로 성공시 Success, 실패시 fail 문자열을 반환합니다.
     */
    public String writeReview(ReviewBodyDto reviewBodyDto, Long memberId, Long novelId){
        Member member = memberRepository.findById(memberId).orElse(null); //멤버 찾기
        if(member == null) return "fail"; //memberId에 대응되는 소설이 없다면 fail 반환
        Novel novel = novelRepository.findById(novelId).orElse(null);
        if(novel == null) return "fail"; //novelId에 대응되는 소설이 없다면 fail

        // 리뷰 등록
        String inputContent;

        if(reviewBodyDto.getContent() == null){
            inputContent = "";
        }
        else inputContent = reviewBodyDto.getContent();

        Review review = Review.builder()
                .rating(reviewBodyDto.getRating())
                .content(inputContent)
                .up(0).build();

        review.addNovel(novel);
        review.addMember(member);
        reviewRepository.save(review);

        return "success";
    }

    /**
     * 리뷰를 삭제하는 method
     *
     */
    public String deleteReview(Long memberId, Long novelId) {
        Member member = memberRepository.findByMemberIdWithReviews(memberId); //멤버 찾기 이때 리뷰까지 같이 가져온다.
        if(member == null) return "fail"; //memberId에 대응되는 소설이 없다면 fail 반환
        Novel novel = novelRepository.findByNovelIdWithReviews(novelId);
        if(novel == null) return "fail"; //novelId에 대응되는 소설이 없다면 fail

        //본인이 작성한 리뷰 조회
        Optional<Review> myReview = reviewRepository.findSingleReivew(member, novel);

        if(myReview.isEmpty()){ //내 리뷰가 없으면 fail
            return "fail";
        }

        Review deleteReview = myReview.get();

        member.deleteMyReview(deleteReview); //일관성을 위해 현재 멤버 객체에서도 제거해줌
        novel.deleteReview(deleteReview); //일관성을 위해 현재 소설 객체에서도 제거해줌
        reviewRepository.deleteReviewById(deleteReview.getId()); //리뷰 삭제

        return "success";
    }

    /**
     * 리뷰 수정 Method
     */
    public String modifyReview(ReviewBodyDto reviewBodyDto, Long memberId, Long novelId) {
        Member member = memberRepository.findById(memberId).orElse(null); //멤버 찾기
        if(member == null) return "fail"; //memberId에 대응되는 소설이 없다면 fail 반환
        Novel novel = novelRepository.findById(novelId).orElse(null);
        if(novel == null) return "fail"; //novelId에 대응되는 소설이 없다면 fail

        //본인이 작성한 리뷰 조회
        Optional<Review> myReview = reviewRepository.modifyReview(member, novel);

        if(myReview.isEmpty()){ //내 리뷰가 없으면 fail
            return "fail";
        }

        Review deleteReview = myReview.get();

        //deleteReview는 영속성 컨텍스트 내에 있으므로
        //deleteReview를 수정하면 자연스럽게 update Query가 나가게 된다 --> 변경감지
        deleteReview.modifyContent(reviewBodyDto.getContent());
        deleteReview.modifyRating(deleteReview.getRating(), reviewBodyDto.getRating(), novel); //리뷰 값 수정

        return "success";
    }
}
