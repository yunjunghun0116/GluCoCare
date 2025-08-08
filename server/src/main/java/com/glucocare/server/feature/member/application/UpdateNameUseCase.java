package com.glucocare.server.feature.member.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.member.dto.UpdateNameRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 회원 이름 수정 기능을 담당하는 Use Case 클래스
 * <p>
 * 이 클래스는 기존 회원의 이름을 업데이트하는 비즈니스 로직을 처리합니다.
 * 회원 ID를 통해 회원을 조회한 후 이름 업데이트를 수행합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateNameUseCase {
    private final MemberRepository memberRepository;

    /**
     * 회원의 이름을 업데이트하는 메인 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 주어진 memberId로 회원 조회
     * 2. 회원 엔티티의 이름 업데이트 메서드 호출
     * 3. 변경된 회원 정보를 데이터베이스에 저장
     *
     * @param memberId 이름을 업데이트할 회원의 ID
     * @param request  새로운 이름 정보를 포함한 업데이트 요청 객체
     * @throws ApplicationException 회원을 찾을 수 없는 경우
     */
    public void execute(Long memberId, UpdateNameRequest request) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        member.updateName(request.name());
        memberRepository.save(member);
    }
}
