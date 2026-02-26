package com.glucocare.server.feature.glucose.domain;

import com.glucocare.server.feature.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GlucoseHistoryRepository extends JpaRepository<GlucoseHistory, Long> {
    List<GlucoseHistory> findAllByPatientOrderByDateTimeDesc(Member patient);

    @Query(
            """
            SELECT gh FROM GlucoseHistory gh
            WHERE gh.patient IN :patients
            AND gh.dateTime = (
                SELECT MAX(gh2.dateTime) FROM GlucoseHistory gh2
                WHERE gh2.patient = gh.patient
            )
            """
    )
    List<GlucoseHistory> findLatestByPatient(@Param("patients") List<Member> patients);

    List<GlucoseHistory> findTop20ByPatientOrderByDateTimeDesc(Member patient);
}
