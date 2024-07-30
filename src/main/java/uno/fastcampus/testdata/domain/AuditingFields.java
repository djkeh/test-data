package uno.fastcampus.testdata.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 각 필요한 테이블에 들어가는 메타정보.
 * 테이블 데이터가 기록된 시간과 작성자 정보를 담고 있다.
 * 이 필드들은 Spring Data JPA에 의해 자동으로 관리되므로,
 * 코드 사용자가 직접 손댈 필요 없게끔 구현되어 있다.
 *
 * @author Uno
 */
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AuditingFields {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt; // 생성일시

    @CreatedBy
    @Column(nullable = false, updatable = false)
    protected String createdBy; // 생성자

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @LastModifiedDate
    @Column(nullable = false)
    protected LocalDateTime modifiedAt; // 수정일시

    @LastModifiedBy
    @Column(nullable = false)
    protected String modifiedBy; // 수정자

}
