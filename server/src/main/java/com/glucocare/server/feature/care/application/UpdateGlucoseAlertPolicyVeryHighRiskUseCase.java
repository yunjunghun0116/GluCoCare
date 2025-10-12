package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicyRepository;
import com.glucocare.server.feature.care.dto.UpdateGlucoseAlertPolicyVeryHighRiskRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 혈당 알림 정책 매우 고위험 기준값 수정 Use Case
 * <p>
 * 혈당 알림 정책의 매우 고위험(Very High Risk) 기준값을 수정합니다.
 * 이 기준값은 간병인에게 혈당 알림을 전송할 때 사용되는 임계값입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateGlucoseAlertPolicyVeryHighRiskUseCase {
    private final GlucoseAlertPolicyRepository glucoseAlertPolicyRepository;

    /**
     * 매우 고위험 기준값 수정
     * <p>
     * 비즈니스 로직 순서:
     * 1. 정책 ID로 데이터베이스에서 혈당 알림 정책 엔티티 조회
     * 2. 정책의 매우 고위험 기준값 업데이트
     * 3. 변경된 정책 정보를 데이터베이스에 저장
     *
     * @param id      수정할 혈당 알림 정책의 ID
     * @param request 새로운 매우 고위험 기준값을 포함한 수정 요청
     * @throws ApplicationException 혈당 알림 정책을 찾을 수 없는 경우 (NOT_FOUND)
     */
    public void execute(Long id, UpdateGlucoseAlertPolicyVeryHighRiskRequest request) {
        var glucoseAlertPolicy = glucoseAlertPolicyRepository.findById(id)
                                                             .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        glucoseAlertPolicy.updateVeryHighRiskValue(request.veryHighRiskValue());
        glucoseAlertPolicyRepository.save(glucoseAlertPolicy);
    }
}
