package com.glucocare.server.feature.auth.application;

import com.glucocare.server.feature.auth.dto.ExistsUniqueEmailRequest;
import com.glucocare.server.feature.member.domain.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 이메일 중복 확인 Use Case
 * <p>
 * 회원가입 전 이메일 중복 여부를 확인하는 기능을 제공합니다.
 * 사용자 경험 개선을 위해 회원가입 폼에서 실시간으로 이메일 사용 가능 여부를 피드백합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ExistsUniqueEmailUseCase {
    private final MemberRepository memberRepository;

    /**
     * 이메일 중복 여부 확인
     * <p>
     * 비즈니스 로직 순서:
     * 1. 요청된 이메일로 데이터베이스에서 회원 존재 여부 확인
     * 2. 존재하면 true (사용 불가), 존재하지 않으면 false (사용 가능) 반환
     *
     * @param request 중복 확인할 이메일을 포함한 요청
     * @return true: 이메일이 이미 등록되어 있음 (사용 불가), false: 사용 가능한 이메일
     */
    public Boolean execute(ExistsUniqueEmailRequest request) {
        return memberRepository.existsByEmail(request.email());
    }
}
