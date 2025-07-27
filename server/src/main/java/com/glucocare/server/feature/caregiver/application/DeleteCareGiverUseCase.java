package com.glucocare.server.feature.caregiver.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.caregiver.domain.CareGiverRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 간병인(CareGiver) 관계 삭제를 담당하는 Use Case 클래스
 * 
 * 이 클래스는 기존의 간병인 관계를 삭제하는 비즈니스 로직을 처리합니다.
 * 삭제 전에 현재 로그인한 회원이 해당 간병인 관계의 소유자인지 검증합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DeleteCareGiverUseCase {
    private final CareGiverRepository careGiverRepository;

    /**
     * 간병인 관계를 삭제하는 메인 메서드
     * 
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 주어진 ID로 간병인 관계 존재 여부 확인
     * 2. 현재 로그인한 회원이 해당 간병인 관계의 소유자인지 검증
     * 3. 권한이 확인되면 간병인 관계를 데이터베이스에서 삭제
     * 
     * @param memberId 현재 로그인한 회원의 ID
     * @param id 삭제할 간병인 관계의 ID
     * @throws ApplicationException 간병인 관계를 찾을 수 없거나 삭제 권한이 없는 경우
     */
    public void execute(Long memberId, Long id) {
        var careGiver = careGiverRepository.findById(id)
                                           .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (!careGiver.getMember()
                      .getId()
                      .equals(memberId)) {
            throw new ApplicationException(ErrorMessage.BAD_REQUEST);
        }
        careGiverRepository.delete(careGiver);
    }

}
