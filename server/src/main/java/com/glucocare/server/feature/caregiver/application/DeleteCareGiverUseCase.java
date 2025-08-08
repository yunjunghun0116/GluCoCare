package com.glucocare.server.feature.caregiver.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.caregiver.domain.CareGiverRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 간병인(CareGiver) 관계 삭제를 담당하는 Use Case 클래스
 * <p>
 * 이 클래스는 기존의 간병인 관계를 삭제하는 비즈니스 로직을 처리합니다.
 * 간병인과 환자 간의 연결 관계를 해제하여 더 이상 해당 환자의 혈당 데이터에 접근할 수 없게 합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DeleteCareGiverUseCase {
    private final CareGiverRepository careGiverRepository;

    /**
     * 간병인 관계를 삭제하는 메인 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 주어진 간병인 관계 ID로 데이터베이스에서 간병인 관계 조회
     * 2. 간병인 관계가 존재하지 않으면 NOT_FOUND 예외 발생
     * 3. 조회된 간병인 관계를 데이터베이스에서 영구 삭제
     *
     * @param id 삭제할 간병인 관계의 ID
     * @throws ApplicationException 간병인 관계를 찾을 수 없는 경우
     */
    public void execute(Long id) {
        var careGiver = careGiverRepository.findById(id)
                                           .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        careGiverRepository.delete(careGiver);
    }

}
