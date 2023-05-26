package NovelForm.NovelForm.domain.novel;


import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.member.exception.WrongMemberException;
import NovelForm.NovelForm.domain.novel.dto.detailnoveldto.ReviewDto;
import NovelForm.NovelForm.domain.novel.dto.reivewdto.BestReviewDto;
import NovelForm.NovelForm.domain.novel.dto.reivewdto.ReviewBodyDto;
import NovelForm.NovelForm.domain.novel.exception.NoMatchingGenre;
import NovelForm.NovelForm.domain.novel.exception.NotReviewOwner;
import NovelForm.NovelForm.global.exception.NoSuchListElement;
import NovelForm.NovelForm.repository.LikeRepository;
import NovelForm.NovelForm.repository.MemberRepository;
import NovelForm.NovelForm.repository.NovelRepository;
import NovelForm.NovelForm.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static NovelForm.NovelForm.domain.novel.NovelService.PagingSize;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final NovelRepository novelRepository;
    private final LikeRepository likeRepository;

    private final static String[] genre = {"로맨스", "로판", "판타지", "현판", "무협", "미스테리", "라이트노벨"};

    //파라미터로 들어온 소설에 작성된 리뷰를 반환한다.
//    @Transactional(readOnly = true)
//    public List<ReviewDto> findReviewMatchingNovel(Novel novel){
//        List<Review> reviews = reviewRepository.findByReview(novel);
//        if(reviews.size() == 0){
//            return null; //공 List를 반환함
//        }
//        return reviews.stream().map(r -> new ReviewDto(r.getMember().getNickname(), r.getContent(), r.getRating(),
//                r.getCreate_at(), 0, r.getMember().getId(), r.getId())).collect(Collectors.toList());
//    }

    //파라미터로 들어온 소설에 작성된 리뷰를 반환한다.
    @Transactional(readOnly = true)
    public List<ReviewDto> findReviewMatchingNovel(Novel novel){
       return reviewRepository.findByReviewWithLike(novel);
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
    @Transactional
    public Long writeReview(ReviewBodyDto reviewBodyDto, Long memberId, Long novelId) throws Exception{
        Member member = memberRepository.findById(memberId).orElse(null); //멤버 찾기
        Novel novel = novelRepository.findById(novelId).orElse(null);

        //세션값에 대응되는 멤버를 못찾으면
        if(member == null) {
            throw new WrongMemberException("잘못된 유저 아이디입니다.");
        }
        //novelId에 대응되는 소설을 못찾으면
        if(novel == null) {
            throw new IllegalArgumentException("해당하는 소설이 존재하지 않습니다.");
        }

        Optional<Review> reivew = reviewRepository.findSingleReivew(member, novel);

        //이미 리뷰를 작성한 소설에 또 리뷰를 작성하려는 경우 fail;
        if(reivew.isPresent()){
            throw new IllegalArgumentException("잘못된 리뷰 등록입니다.");
        }

        // 리뷰 등록
        String inputContent;

        if(reviewBodyDto.getContent() == null){
            inputContent = "";
        }
        else inputContent = reviewBodyDto.getContent();

        Review review = Review.builder()
                .rating(reviewBodyDto.getRating())
                .content(inputContent)
                .build();

        review.addNovel(novel);
        review.addMember(member);
        Review save = reviewRepository.save(review);

        return save.getId();
    }

    /**
     * 리뷰를 삭제하는 method
     *
     */
    @Transactional
    public String deleteReview(Long memberId, Long novelId, Long review_idx) throws Exception {
        Member member = memberRepository.findByMemberIdWithReviews(memberId); //멤버 찾기 이때 리뷰까지 같이 가져온다.
        Novel novel = novelRepository.findByNovelIdWithReviews(novelId);

        //세션값에 대응되는 멤버를 못찾으면
        if(member == null) {
            throw new WrongMemberException("잘못된 유저 아이디입니다.");
        }
        //novelId에 대응되는 소설을 못찾으면
        if(novel == null) {
            throw new IllegalArgumentException("해당하는 소설이 존재하지 않습니다.");
        }

        //본인이 작성한 리뷰 조회 리뷰는 소설당 하나 밖에 작성하지 못하므로 하나만 가져올 수 있다.
        Optional<Review> myReview = reviewRepository.reviewForDelete(review_idx);

        if(myReview.isEmpty()){ //내 리뷰가 없으면 fail
            throw new WrongMemberException("잘못된 유저 아이디입니다.");
        }

        Review deleteReview = myReview.get();

        if(!novel.getReviews().contains(deleteReview)){
            throw new IllegalArgumentException("소설에 없는 리뷰를 삭제하려합니다.");
        }

        //로그인한 사람이 자기가 작성한 글이 아닌 다른 사람의 글을 삭제하려는 경우
        if(!member.getId().equals(deleteReview.getMember().getId())){
            throw new NotReviewOwner();
        }

        member.deleteMyReview(deleteReview); //일관성을 위해 현재 멤버 객체에서도 제거해줌
        novel.deleteReview(deleteReview); //일관성을 위해 현재 소설 객체에서도 제거해줌
        reviewRepository.deleteReviewById(deleteReview.getId()); //리뷰 삭제

        return "success";
    }

    /**
     * 리뷰 수정 Method
     */
    @Transactional
    public String modifyReview(ReviewBodyDto reviewBodyDto, Long memberId, Long novelId, Long reviewId) throws Exception {
        Member member = memberRepository.findById(memberId).orElse(null);

        Novel novel = novelRepository.findById(novelId).orElse(null);

        //세션값에 대응되는 멤버를 못찾으면
        if(member == null) {
            throw new WrongMemberException("잘못된 유저 아이디입니다.");
        }
        //novelId에 대응되는 소설을 못찾으면
        if(novel == null) {
            throw new IllegalArgumentException("해당하는 소설이 존재하지 않습니다.");
        }

        //본인이 작성한 리뷰 조회
        Optional<Review> myReview = reviewRepository.findById(reviewId);

        if(myReview.isEmpty()){ //내 리뷰가 없으면 fail
            throw new WrongMemberException("잘못된 유저 아이디입니다.");
        }

        Review modifyReview = myReview.get();

        //로그인한 사람이 자기가 작성한 글이 아닌 다른 사람의 리뷰를 수정하려는 경우
        if(!member.getId().equals(modifyReview.getMember().getId())){
            throw new NotReviewOwner();
        }


        //modifyReview는 영속성 컨텍스트 내에 있으므로
        //modifyReview를 수정하면 자연스럽게 update Query가 나가게 된다 --> 변경감지
        modifyReview.modifyContent(reviewBodyDto.getContent());
        modifyReview.modifyRating(modifyReview.getRating(), reviewBodyDto.getRating(), novel); //리뷰 값 수정

        return "success";
    }

    @Transactional(readOnly = true)
    public List<BestReviewDto> findBestReview(int page, String input) throws Exception {
        Page<BestReviewDto> result = null;
        log.info("select genre = {}", input);
        for(String s : genre){
            Pageable pageable = PageRequest.of(page, PagingSize);
            if(s.equals(input)) {
                if (input.equals("로판")) {
                    result = likeRepository.findNovelWithinGenreLikeReviewDesc2(input, "로맨스 판타지", pageable);
                    break;
                } else if (input.equals("현판")) {
                    result = likeRepository.findNovelWithinGenreLikeReviewDesc3(input, "현대판타지", "현대 판타지", pageable);
                    break;
                } else {
                    result = likeRepository.findNovelWithinGenreLikeReviewDesc(input, pageable);
                    break;
                }
            }
        }
        //결과가 null 인 경우 -> 장르 소설이름이 일치하지 않은 경우
        if(result == null){
            throw new NoMatchingGenre("잘못된 장르 소설 이름입니다.");
        }
        if(result.getTotalPages() < page){
            throw new NumberFormatException("잘못된 페이지 값입니다.");
        }
        //아직 해당 장르 베스트 리뷰가 없는 경우 --> DB상 inner join으로 좋아요가 없는 리뷰의 경우는 조회되지 않음..
        if(result.getNumberOfElements() == 0){
            throw new NoMatchingGenre("아직 베스트 리뷰가 없습니다.");
        }

        return result.getContent();
    }
}
