package com.glucocare.server.feature.care.domain;

import com.glucocare.server.feature.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareRelationRepository extends JpaRepository<CareRelation, Long> {
    List<CareRelation> findAllByMember(Member member);

    List<CareRelation> findAllByPatient(Member patient);

    Boolean existsByMemberAndPatient(Member member, Member patient);

    Boolean existsByMemberIdAndPatientId(Long memberId, Long patientId);
}
