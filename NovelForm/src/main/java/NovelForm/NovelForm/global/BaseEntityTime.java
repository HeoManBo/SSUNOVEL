package NovelForm.NovelForm.global;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 생성시간과 수정시간은 거의 모든 테이블에서 사용하므로
 * 별도의 클래스로 생성하여
 * 상속받아 사용한다.
 */

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntityTime {

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime create_at;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime update_at;
}
