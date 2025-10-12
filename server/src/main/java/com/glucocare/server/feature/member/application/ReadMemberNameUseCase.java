package com.glucocare.server.feature.member.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 회원 이름 조회 Use Case
 * <p>
 * 회원의 이름을 조회합니다.
 * 주로 마이페이지나 회원 정보 표시 용도로 사용됩니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReadMemberNameUseCase {
    private final MemberRepository memberRepository;

    /**
     * 회원 이름 조회
     * <p>
     * 비즈니스 로직 순서:
     * 1. 회원 ID로 데이터베이스에서 회원 엔티티 조회
     * 2. 조회된 회원의 이름 반환
     *
     * @param memberId 조회할 회원의 ID
     * @return 회원의 이름
     * @throws ApplicationException 회원을 찾을 수 없는 경우 (NOT_FOUND)
     */
    public String execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        return member.getName();
    }
}
