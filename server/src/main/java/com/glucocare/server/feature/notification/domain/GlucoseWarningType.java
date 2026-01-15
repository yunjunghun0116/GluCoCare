package com.glucocare.server.feature.notification.domain;

import com.glucocare.server.feature.care.domain.GlucoseAlertPolicy;

public enum GlucoseWarningType {
    HIGH_RISK,
    VERY_HIGH_RISK,
    NORMAL,
    LOW_RISK,
    VERY_LOW_RISK;

    public static GlucoseWarningType from(Integer sgv, GlucoseAlertPolicy policy) {
        if (sgv >= policy.getVeryHighRiskValue()) return VERY_HIGH_RISK;
        if (sgv >= policy.getHighRiskValue()) return HIGH_RISK;
        return NORMAL;
    }

    public boolean isNeedSendNotification() {
        return this != NORMAL;
    }
}
