package com.glucocare.server.feature.member.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.member.dto.UpdateMemberNameRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 회원 이름 수정 Use Case
 * <p>
 * 회원의 이름을 수정합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateNameUseCase {
    private final MemberRepository memberRepository;

    /**
     * 회원 이름 수정
     * <p>
     * 비즈니스 로직 순서:
     * 1. 회원 ID로 데이터베이스에서 회원 엔티티 조회
     * 2. 회원 엔티티의 이름 업데이트
     * 3. 변경된 회원 정보를 데이터베이스에 저장
     *
     * @param memberId 수정할 회원의 ID
     * @param request  새로운 이름을 포함한 수정 요청
     * @throws ApplicationException 회원을 찾을 수 없는 경우 (NOT_FOUND)
     */
    public void execute(Long memberId, UpdateMemberNameRequest request) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        member.updateName(request.name());
        memberRepository.save(member);
    }
}
