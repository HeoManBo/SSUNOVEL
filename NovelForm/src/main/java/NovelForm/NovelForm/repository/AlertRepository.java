package NovelForm.NovelForm.repository;

import NovelForm.NovelForm.domain.alert.domain.Alert;
import NovelForm.NovelForm.domain.alert.dto.AlertInfo;
import NovelForm.NovelForm.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {


    @Query(" select new NovelForm.NovelForm.domain.alert.dto.AlertInfo( " +
            " a.id, " +
            " a.title, " +
            " a.alertType, " +
            " a.url, " +
            " a.readCheck ) from Alert a where a.member = :member")
    List<AlertInfo> findAlertByMember(Member member);


    Alert findAlertByIdAndMemberId(Long alertId, Long memberId);


}
