package com.glucocare.server.feature.glucose.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.glucose.domain.DexcomGlucoseHistoryBulkRepository;
import com.glucocare.server.feature.glucose.dto.DexcomGlucoseRequest;
import com.glucocare.server.feature.glucose.infra.GlucoseHistoryCache;
import com.glucocare.server.feature.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DexcomCreateGlucoseHistoryUseCase {

    private final MemberRepository memberRepository;
    private final DexcomGlucoseHistoryBulkRepository dexcomGlucoseHistoryBulkRepository;
    private final GlucoseHistoryCache glucoseHistoryCache;

    public void execute(Long patientId, String accessCode, List<DexcomGlucoseRequest> glucoseRequestList) {
        var patient = memberRepository.findById(patientId)
                                      .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        patient.validateAccessCode(accessCode);
        dexcomGlucoseHistoryBulkRepository.upsertBatch(patientId, glucoseRequestList);
        glucoseHistoryCache.clearByPatientId(patientId);
    }
}
