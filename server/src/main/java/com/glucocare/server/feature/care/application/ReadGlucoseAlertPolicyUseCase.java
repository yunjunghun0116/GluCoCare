package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareGiver;
import com.glucocare.server.feature.care.domain.CareGiverRepository;
import com.glucocare.server.feature.care.dto.ReadCareGiverResponse;
import com.glucocare.server.feature.member.domain.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 간병인(CareGiver) 관계 전체 조회를 담당하는 Use Case 클래스
 * <p>
 * 이 클래스는 특정 회원에게 연결된 모든 간병인 관계를 조회하는 비즈니스 로직을 처리합니다.
 * 조회된 간병인 관계들은 응답 객체로 변환되어 내려집니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReadAllCareGiverUseCase {
    private final MemberRepository memberRepository;
    private final CareGiverRepository careGiverRepository;

    /**
     * 특정 회원의 모든 간병인 관계를 조회하는 메인 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 주어진 memberId로 회원 존재 여부 확인
     * 2. 해당 회원과 연결된 모든 간병인 관계 조회
     * 3. 조회된 간병인 관계들을 응답 객체 리스트로 변환
     *
     * @param memberId 간병인 관계를 조회할 회원의 ID
     * @return 해당 회원의 모든 간병인 관계 정보를 담은 응답 객체 리스트
     * @throws ApplicationException 회원을 찾을 수 없는 경우
     */
    public List<ReadCareGiverResponse> execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var careGivers = careGiverRepository.findAllByMember(member);
        return careGivers.stream()
                         .map((this::convertCareGiverResponse))
                         .toList();
    }

    /**
     * 간병인 엔티티를 조회용 응답 객체로 변환하는 메서드
     * <p>
     * 간병인 관계 엔티티에서 필요한 정보(간병인 ID, 환자 정보)를 추출하여
     * 클라이언트에게 반환할 조회 전용 응답 객체를 생성합니다.
     *
     * @param careGiver 변환할 간병인 관계 엔티티
     * @return 간병인 ID, 환자 ID, 환자 이름, CGM 서버 URL을 포함한 조회 응답 객체
     */
    private ReadCareGiverResponse convertCareGiverResponse(CareGiver careGiver) {
        var patient = careGiver.getPatient();
        return ReadCareGiverResponse.of(careGiver.getId(), patient.getId(), patient.getName());
    }
}
