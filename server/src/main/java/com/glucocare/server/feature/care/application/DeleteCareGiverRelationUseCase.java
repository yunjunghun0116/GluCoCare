package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteCareGiverRelationUseCase {
    private final MemberPatientRelationRepository memberPatientRelationRepository;

    public void execute(Long memberId, Long id) {
        var careGiverRelation = memberPatientRelationRepository.findById(id)
                                                               .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        careGiverRelation.validateOwnership(memberId);
        memberPatientRelationRepository.delete(careGiverRelation);
    }

}
