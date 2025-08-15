package com.glucocare.server.feature.care.domain;

import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.patient.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareGiverRepository extends JpaRepository<CareGiver, Long> {
    List<CareGiver> findAllByMember(Member member);

    Boolean existsByMemberAndPatient(Member member, Patient patient);
}
