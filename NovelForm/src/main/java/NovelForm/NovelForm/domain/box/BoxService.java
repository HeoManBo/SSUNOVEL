package NovelForm.NovelForm.domain.box;

import NovelForm.NovelForm.domain.box.domain.Box;
import NovelForm.NovelForm.domain.box.domain.BoxItem;
import NovelForm.NovelForm.domain.box.dto.*;
import NovelForm.NovelForm.domain.box.exception.NoSuchBoxItemException;
import NovelForm.NovelForm.domain.box.exception.WrongAccessBoxException;
import NovelForm.NovelForm.domain.box.exception.WrongBoxException;
import NovelForm.NovelForm.domain.box.exception.WrongMemberException;
import NovelForm.NovelForm.domain.member.domain.Member;
import NovelForm.NovelForm.domain.novel.Novel;
import NovelForm.NovelForm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoxService {

    private final BoxRepository boxRepository;
    private final MemberRepository memberRepository;

    private final NovelRepository novelRepository;
    
    private final LikeRepository likeRepository;


    /**
     * 보관함 생성 로직
     *
     * @param memberId
     * @param createBoxRequest
     * @return
     */
    public Long createBox(Long memberId, CreateBoxRequest createBoxRequest) throws NoSuchElementException{


        // 생성자
        Member findMember = memberRepository.findById(memberId).get();

        // 보관함
        Box box = new Box(createBoxRequest.getTitle(),
                createBoxRequest.getContent(),
                createBoxRequest.getIs_private(),
                findMember);


        // 보관함 아이템
        for (Long boxItemId : createBoxRequest.getBoxItems()) {

            Optional<Novel> optionalNovel = novelRepository.findById(boxItemId);
            Novel novel;

            log.info("==================================================");
            log.info("id={}",boxItemId);
            log.info("create request 속 novels: {}",optionalNovel);

            // 없는 작품을 보관함에 넣으려고 하면, 실패
            if(optionalNovel.isPresent()){
                novel = novelRepository.findById(boxItemId).get();
            }
            else{
                throw new NoSuchBoxItemException("없는소설번호: " + boxItemId);
            }


            BoxItem boxItem;

            if (boxItemId == createBoxRequest.getLeadItemId()){
                boxItem = new BoxItem(1, novel);
            }
            else{
                boxItem = new BoxItem(0, novel);
            }



            box.addBoxItem(boxItem);
        }

        // 저장
        Box saveBox = boxRepository.save(box);

        return saveBox.getId();
    }




    /**
     * 보관함 삭제 로직
     *
     * @param memberId
     * @param boxId
     */
    public void deleteBox(Long memberId, Long boxId) throws Exception {

        Optional<Member> memberById = memberRepository.findById(memberId);
        Optional<Box> boxById = boxRepository.findById(boxId);

        if(!memberById.isPresent()){
            throw new WrongMemberException("잘못 된 사용자");
        }

        if(!boxById.isPresent()){
            throw new WrongBoxException("잘못 된 보관함: " + boxId);
        }

        Member findMember =  memberById.get();
        Box findBox = boxById.get();


        Optional<Box> existBox = boxRepository.findExistBoxWithBoxIDAndMember(findBox.getId(), findMember);

        if(!existBox.isPresent()){
            throw new WrongAccessBoxException("권한 없는 사용자의 접근");
        }


        boxRepository.deleteById(findBox.getId());
    }

    /**
     * 보관함 수정 로직
     *
     * @param createBoxRequest
     * @param memberId
     * @param boxId
     * @return
     */
    public Long updateBox(CreateBoxRequest createBoxRequest, Long memberId, Long boxId) throws Exception{

        // 삭제
        deleteBox(memberId, boxId);

        // 생성
        Long createBoxId = createBox(memberId, createBoxRequest);

        return createBoxId;
    }

    /**
     * 보관함 전체 조회 로직
     *
     * @return
     */
    public List<AllBoxResponse> getAllBox(Integer page, FilteringType filtering) {

        List<AllBoxResponse> allBoxByPublic = null;

        PageRequest pageRequest = PageRequest.of(page - 1, 10);

        // 필터링 조건에 따른 정렬 순서 조정
        switch (filtering) {
            case TIME_DESC -> {
                PageRequest pageRequestBySort = pageRequest.withSort(Sort.by(Sort.Order.desc("update_at")));
                allBoxByPublic = boxRepository.findAllBoxByPublic(pageRequestBySort).getContent();
            }
            case TIME_ASC -> {
                PageRequest pageRequestBySort = pageRequest.withSort(Sort.by("update_at"));
                allBoxByPublic = boxRepository.findAllBoxByPublic(pageRequestBySort).getContent();
            }
            case LIKE_ASC -> {
                allBoxByPublic = likeRepository.findAllBoxByPublicWithLike(pageRequest).getContent();
            }
            case LIKE_DESC -> {
                allBoxByPublic = likeRepository.findAllBoxByPublicWithLikeDesc(pageRequest).getContent();
            }

        }




        return allBoxByPublic;
    }


    /**
     * 보관함 상세 조회 로직
     * @param boxId
     * @return
     */
    public BoxInfoResponse getBoxInfo(Long boxId, Integer page) {

        PageRequest pageRequest = PageRequest.of(page - 1, 10);

        Box findBox = boxRepository.findBoxWithBoxItems(boxId, pageRequest);


        if(findBox == null){
            throw new NoSuchBoxItemException("보관함: " + boxId);
        }


        List<BoxItemInfo> boxItemInfos = new ArrayList<>();

        for (BoxItem boxItem : findBox.getBoxItems()) {
            boxItemInfos.add(new BoxItemInfo(
                    boxItem.getNovel().getId(),
                    boxItem.getNovel().getCover_image(),
                    boxItem.getNovel().getCategory(),
                    boxItem.getNovel().getAuthor().getName(),
                    boxItem.getNovel().getTitle(),
                    boxItem.getNovel().getRating(),
                    boxItem.getNovel().getReview_cnt(),
                    boxItem.getIs_lead_item()));
        }


        BoxInfoResponse boxInfoResponse = new BoxInfoResponse(
                findBox.getId(),
                findBox.getTitle(),
                findBox.getContent(),
                boxItemInfos
        );

        return boxInfoResponse;
    }


    /**
     *  보관함 검색 (보관함 이름, 보관함 생성자 이름)
     *
     *  둘 다 10개씩 검색하기
     */
    public BoxSearchResponse searchBox(String search, Integer page) {

        PageRequest pageRequest = PageRequest.of(page - 1, 10);

        List<BoxSearchInfo> boxByTitle = boxRepository.findBoxByTitle(search, pageRequest);
        List<BoxSearchInfo> boxByMember = boxRepository.findBoxByMember(search, pageRequest);

        return new BoxSearchResponse(boxByTitle, boxByMember);
    }


    /**
     *  보관함 검색 (보관함 이름)
     *
     *  10개씩 검색하기
     */
    public BoxSearchByTitleResponse searchBoxByTitle(String search, Integer page) {

        PageRequest pageRequest = PageRequest.of(page - 1, 10);

        List<BoxSearchInfo> boxByTitle = boxRepository.findBoxByTitle(search, pageRequest);
        return new BoxSearchByTitleResponse(boxByTitle);
    }


    /**
     *  보관함 검색 (보관함 생성자)
     *
     *  10개씩 검색하기
     */
    public BoxSearchByCreatorResponse searchBoxByCreator(String search, Integer page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10);

        List<BoxSearchInfo> boxByCreator = boxRepository.findBoxByMember(search, pageRequest);
        return new BoxSearchByCreatorResponse(boxByCreator);
    }
}
