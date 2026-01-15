package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteCareRelationUseCase {
    private final CareRelationRepository careRelationRepository;

    public void execute(Long memberId, Long id) {
        var careRelation = careRelationRepository.findById(id)
                                                 .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        careRelation.validateOwnership(memberId);
        careRelationRepository.delete(careRelation);
    }

}
