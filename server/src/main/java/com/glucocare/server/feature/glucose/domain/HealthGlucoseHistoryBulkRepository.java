package com.glucocare.server.feature.glucose.domain;

import com.glucocare.server.feature.glucose.dto.HealthGlucoseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HealthGlucoseHistoryBulkRepository {

    private static final String UPSERT_SQL = """
                                             INSERT INTO glucose_history (patient_id, dateTime, sgv, created_at, last_modified_at)
                                             VALUES (?, ?, ?, ?, ?)
                                             ON DUPLICATE KEY UPDATE
                                                 sgv = VALUES(sgv),
                                                 last_modified_at = VALUES(last_modified_at)
                                             """;
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private final JdbcTemplate jdbcTemplate;

    public void upsertBatch(Long patientId, List<HealthGlucoseRequest> glucoseRequestList) {
        jdbcTemplate.batchUpdate(UPSERT_SQL, glucoseRequestList, 500, (ps, r) -> {
            ps.setLong(1, patientId);
            ps.setLong(2, r.dateTime());
            ps.setInt(3, r.sgv());

            var now = LocalDateTime.now(ZONE);
            ps.setTimestamp(4, Timestamp.valueOf(now)); // created_at (insert 때만 실제 반영)
            ps.setTimestamp(5, Timestamp.valueOf(now)); // last_modified_at (insert / update 둘 다 반영)
        });
    }
}
