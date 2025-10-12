package com.glucocare.server.feature.care.domain;

import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.patient.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberPatientRelationRepository extends JpaRepository<MemberPatientRelation, Long> {
    List<MemberPatientRelation> findAllByMember(Member member);

    List<MemberPatientRelation> findAllByPatient(Patient patient);

    List<MemberPatientRelation> findByMemberAndRelationType(Member member, RelationType relationType);

    Boolean existsByMemberAndPatient(Member member, Patient patient);

    Boolean existsByMemberIdAndPatientId(Long memberId, Long patientId);

    Boolean existsByMemberAndRelationType(Member member, RelationType relationType);
}
