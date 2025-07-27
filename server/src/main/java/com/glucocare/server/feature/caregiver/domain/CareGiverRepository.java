package com.glucocare.server.feature.caregiver.domain;

import com.glucocare.server.feature.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareGiverRepository extends JpaRepository<CareGiver, Long> {
    List<CareGiver> findAllByMember(Member member);
}
