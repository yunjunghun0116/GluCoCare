package com.glucocare.server.feature.point.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.point.domain.PointHistory;
import com.glucocare.server.feature.point.domain.PointHistoryRepository;
import com.glucocare.server.feature.point.domain.PointTransactionType;
import com.glucocare.server.feature.point.domain.PointWallet;
import com.glucocare.server.feature.point.domain.PointWalletRepository;
import com.glucocare.server.feature.point.dto.EarnPointRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class EarnPointUseCase {
    private final MemberRepository memberRepository;
    private final PointWalletRepository pointWalletRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public void execute(Long memberId, EarnPointRequest earnPointRequest) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var pointWallet = pointWalletRepository.findByMember(member)
                                               .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        earnPoint(member, pointWallet, earnPointRequest);
    }

    private void earnPoint(Member member, PointWallet pointWallet, EarnPointRequest earnPointRequest) {
        var balanceAfter = pointWallet.getBalance() + earnPointRequest.amount();
        var pointHistory = new PointHistory(member, PointTransactionType.EARN, earnPointRequest.amount(), balanceAfter, earnPointRequest.description());
        pointWallet.earn(earnPointRequest.amount());
        pointHistoryRepository.save(pointHistory);
    }
}
