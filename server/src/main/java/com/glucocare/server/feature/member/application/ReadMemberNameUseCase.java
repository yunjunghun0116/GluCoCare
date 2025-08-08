package com.glucocare.server.feature.member.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 회원 이름 조회 기능을 담당하는 Use Case 클래스
 * <p>
 * 이 클래스는 회원 ID를 통해 회원의 이름을 조회하는 비즈니스 로직을 처리합니다.
 * 주로 마이페이지나 회원 정보 표시 용도로 사용됩니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReadMemberNameUseCase {
    private final MemberRepository memberRepository;

    /**
     * 회원 ID로 회원의 이름을 조회하는 메인 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 주어진 memberId로 데이터베이스에서 회원 조회
     * 2. 회원이 존재하지 않으면 NOT_FOUND 예외 발생
     * 3. 조회된 회원의 이름 반환
     *
     * @param memberId 이름을 조회할 회원의 ID
     * @return 회원의 이름
     * @throws ApplicationException 회원을 찾을 수 없는 경우
     */
    public String execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        return member.getName();
    }
}
