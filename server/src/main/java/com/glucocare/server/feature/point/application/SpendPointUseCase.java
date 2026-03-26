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
import com.glucocare.server.feature.point.dto.SpendPointRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SpendPointUseCase {
    private final MemberRepository memberRepository;
    private final PointWalletRepository pointWalletRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public void execute(Long memberId, SpendPointRequest spendPointRequest) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var pointWallet = pointWalletRepository.findByMember(member)
                                               .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        spendPoint(member, pointWallet, spendPointRequest);
    }

    private void spendPoint(Member member, PointWallet pointWallet, SpendPointRequest spendPointRequest) {
        if (!pointWallet.canSpend(spendPointRequest.amount())) {
            throw new ApplicationException(ErrorMessage.INVALID_BALANCE_IN_WALLET);
        }
        var balanceAfter = pointWallet.getBalance() - spendPointRequest.amount();
        var pointHistory = new PointHistory(member, PointTransactionType.SPEND, spendPointRequest.amount(), balanceAfter, spendPointRequest.description());
        pointWallet.spend(spendPointRequest.amount());
        pointHistoryRepository.save(pointHistory);
    }
}
