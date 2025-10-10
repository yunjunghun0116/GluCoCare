package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.GlucoseAlertPolicyRepository;
import com.glucocare.server.feature.care.dto.UpdateGlucoseAlertPolicyHighRiskRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 혈당 경고 정책의 고위험 기준값 업데이트를 담당하는 Use Case 클래스
 * <p>
 * 이 클래스는 기존 혈당 경고 정책의 고위험(High Risk) 혈당 기준값을 업데이트하는
 * 비즈니스 로직을 처리합니다. 이 기준값은 간병인에게 혈당 경고 알림을 전송할 때 사용됩니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateGlucoseAlertPolicyHighRiskUseCase {
    private final GlucoseAlertPolicyRepository glucoseAlertPolicyRepository;

    /**
     * 혈당 경고 정책의 고위험 기준값을 업데이트하는 메인 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 주어진 정책 ID로 혈당 경고 정책 조회
     * 2. 정책이 존재하지 않으면 NOT_FOUND 예외 발생
     * 3. 정책의 고위험 기준값 업데이트
     * 4. 변경된 정책 정보를 데이터베이스에 저장
     *
     * @param id      업데이트할 혈당 경고 정책의 ID
     * @param request 새로운 고위험 기준값을 포함한 업데이트 요청 객체
     * @throws ApplicationException 혈당 경고 정책을 찾을 수 없는 경우
     */
    public void execute(Long id, UpdateGlucoseAlertPolicyHighRiskRequest request) {
        var glucoseAlertPolicy = glucoseAlertPolicyRepository.findById(id)
                                                             .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        glucoseAlertPolicy.updateHighRiskValue(request.highRiskValue());
        glucoseAlertPolicyRepository.save(glucoseAlertPolicy);
    }
}
