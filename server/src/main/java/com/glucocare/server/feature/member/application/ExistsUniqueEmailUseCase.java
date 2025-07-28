package com.glucocare.server.feature.member.application;

import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.member.dto.ExistsUniqueEmailRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 이메일 중복 확인 기능을 담당하는 Use Case 클래스
 * 
 * 이 클래스는 회원가입 시 입력된 이메일이 이미 데이터베이스에 등록되어 있는지
 * 확인하는 비즈니스 로직을 처리합니다. 중복된 이메일로 가입을 방지하기 위해 사용됩니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ExistsUniqueEmailUseCase {
    private final MemberRepository memberRepository;

    /**
     * 주어진 이메일이 데이터베이스에 등록되어 있는지 확인하는 메인 메서드
     * 
     * 이 메서드는 회원가입 시 이메일 중복 검사를 위해 사용되며,
     * 요청된 이메일이 이미 등록된 회원의 이메일인지 확인합니다.
     * 
     * @param request 중복 확인할 이메일 정보를 포함한 요청 객체
     * @return 이메일이 이미 등록되어 있으면 true, 사용 가능하면 false
     */
    public Boolean execute(ExistsUniqueEmailRequest request) {
        return memberRepository.existsByEmail(request.email());
    }
}
