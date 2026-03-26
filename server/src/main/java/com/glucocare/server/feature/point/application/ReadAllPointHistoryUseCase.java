package com.glucocare.server.feature.point.application;

import com.glucocare.server.feature.point.domain.PointHistory;
import com.glucocare.server.feature.point.domain.PointHistoryRepository;
import com.glucocare.server.feature.point.dto.PointHistoryResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadAllPointHistoryUseCase {
    private final PointHistoryRepository pointHistoryRepository;

    public List<PointHistoryResponse> execute(Long memberId) {
        var histories = pointHistoryRepository.findAllByMemberId(memberId);
        return histories.stream()
                        .map(this::convertPointHistoryResponse)
                        .toList();
    }

    private PointHistoryResponse convertPointHistoryResponse(PointHistory pointHistory) {
        return PointHistoryResponse.of(pointHistory.getType(), pointHistory.getAmount(), pointHistory.getBalanceAfter(), pointHistory.getDescription());
    }

}
