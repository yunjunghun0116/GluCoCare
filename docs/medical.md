# 의료 정보 출처 및 참고 자료

GluCoCare는 이용자에게 보다 신뢰할 수 있는 혈당 정보와 건강관리 참고 자료를 제공하기 위해, 공신력 있는 의료기관·학술지·표준 참고자료를 바탕으로 정보를 구성하고 있습니다.

다만, 본 문서에 포함된 정보와 GluCoCare 앱에서 제공하는 혈당 관련 안내, 예측 결과, 운동 정보는 **일반적인 건강관리 참고자료**이며, **의사의 진단·처방·치료를 대체하지 않습니다.**

---

## 1. 혈당 기준 범위

GluCoCare에서 사용하는 혈당 상태 구분은 일반적인 혈당 관리 참고 범위를 바탕으로 구성되어 있습니다.

| 구분 | 범위 |
|------|------|
| 저혈당 | 70 mg/dL 미만 |
| 정상 | 70–140 mg/dL |
| 주의 | 140–180 mg/dL |
| 고혈당 | 180 mg/dL 초과 |

### 참고
- 개인의 질환 상태, 식사 여부, 약물 복용 여부, 연령, 임신 여부 등에 따라 목표 혈당 범위는 달라질 수 있습니다.
- 앱 내 기준 범위는 **일반적인 참고값**이며, 실제 치료 목표는 담당 의료진의 판단을 우선합니다.

### 출처
- [American Diabetes Association (ADA), *Standards of Care in Diabetes—2024*](https://diabetesjournals.org/care/issue/47/Supplement_1)
- [대한당뇨병학회, *당뇨병 진료지침*](https://www.diabetes.or.kr)

---

## 2. 목표 혈당 범위 (TIR, Time in Range)

GluCoCare는 연속혈당측정(CGM) 기반 혈당 관리 지표로 **TIR(Time in Range)** 개념을 참고합니다.

| 구분 | 목표 |
|------|------|
| 정상 범위 내 시간 (TIR) | 70% 이상 (70–180 mg/dL) |
| 저혈당 시간 | 4% 미만 (70 mg/dL 미만) |
| 고혈당 시간 | 25% 미만 (180 mg/dL 초과) |

### 참고
- 위 수치는 **일반적인 성인 당뇨병 환자**를 대상으로 제시되는 대표적 CGM 목표값입니다.
- 임신, 고령자, 소아청소년, 중증 저혈당 위험군 등은 별도 목표가 적용될 수 있습니다.

### 출처
- [Battelino T, et al. *Clinical Targets for Continuous Glucose Monitoring Data Interpretation: Recommendations From the International Consensus on Time in Range.* Diabetes Care, 2019.](https://doi.org/10.2337/dci19-0028)

---

## 3. 운동 강도 (MET, Metabolic Equivalent of Task)

GluCoCare는 운동 강도 정보를 구성할 때 대표적인 신체활동 에너지 소모 기준 자료인 **MET(Metabolic Equivalent of Task)** 값을 참고합니다.

다만, 앱 내에서 사용되는 운동 강도 값은 표준 MET 값을 그대로 표시하는 것이 아니라,  
**혈당 변화 예측 및 행동 가이드 제공에 보다 적합하도록 내부 기준에 따라 조정된 값**을 사용할 수 있습니다.

즉, 아래의 표준 MET 자료를 참고하되, GluCoCare에서는 서비스 목적에 맞추어  
운동 종류별 상대적 강도를 반영한 **내부 운동 강도 기준**을 함께 활용합니다.

| 운동 | 참고 MET |
|------|----------|
| 산책 | 3.0 |
| 빠른 걷기 | 4.3 |
| 런닝 | 7.5 |
| 사이클 | 7.5 |
| 웨이트 | 5.0 |
| 배드민턴 | 5.5 |
| 수영 | 6.0 |

### 내부 기준에 대한 안내
- 위 값은 공인된 신체활동 참고 자료를 기반으로 한 일반적인 MET 값입니다.
- GluCoCare는 혈당 예측 정확도 및 행동 가이드의 일관성을 높이기 위해, 표준 MET 값을 직접적으로 그대로 사용하지 않고 **서비스 내부 기준에 맞게 일부 조정된 운동 강도 값**을 활용할 수 있습니다.
- 따라서 앱 내 운동 강도 표시는 학술적·의학적 표준 MET 수치와 완전히 동일하지 않을 수 있습니다.
- 내부 조정값은 운동 간 상대적 강도를 반영하기 위한 참고값이며, 의료적 진단이나 처방 기준으로 사용되지 않습니다.

### 참고
- MET 값은 운동 강도 추정을 위한 **일반화된 참고값**입니다.
- 실제 에너지 소모량은 개인의 체중, 운동 숙련도, 수행 시간, 심박수, 환경 조건에 따라 달라질 수 있습니다.
- 앱에서 제공하는 운동 관련 정보는 참고용이며, 개인의 건강 상태를 고려한 운동 계획은 전문가 상담이 필요할 수 있습니다.

### 출처
- [Ainsworth BE, et al. *2011 Compendium of Physical Activities: A Second Update of Codes and MET Values.* Medicine & Science in Sports & Exercise, 2011.](https://sites.google.com/site/compendiumofphysicalactivities)

---

## 4. 혈당 예측 모델

GluCoCare의 혈당 예측 기능은 연속혈당측정(CGM) 데이터를 기반으로 한 **자체 개발 LSTM 기반 딥러닝 모델**을 사용합니다.

이 기능은 현재 혈당 흐름을 바탕으로 향후 혈당 변화를 예측하여 이용자의 자기관리를 돕기 위한 참고 기능입니다.

### 모델 평가 참고 지표
- **RMSE (Root Mean Square Error)**
- **Clarke Error Grid Analysis (EGA)**

### 참고
- 예측 결과는 실제 혈당값과 차이가 발생할 수 있습니다.
- 센서 오차, 데이터 누락, 식사·운동·스트레스·수면 등의 다양한 요인에 따라 예측 정확도는 달라질 수 있습니다.
- GluCoCare의 예측 결과는 **경고 및 참고용 정보**이며, 의료적 진단·치료·응급 판단의 단독 근거로 사용되어서는 안 됩니다.

### 참고 자료
- [Clarke WL, et al. *Evaluating Clinical Accuracy of Systems for Self-Monitoring of Blood Glucose.* Diabetes Care, 1987.](https://doi.org/10.2337/diacare.10.5.622)
- [OhioT1DM Dataset: *A Blood Glucose Level Prediction Dataset for Type 1 Diabetes.*](http://smarthealth.cs.ohio.edu/OhioT1DM-dataset.html)

---

## 5. 의료 정보 이용 시 주의사항

> GluCoCare가 제공하는 혈당 정보, 통계, 예측 결과, 운동 정보는 모두 **참고용 정보**입니다.  
> 본 서비스는 의료인의 전문적 판단, 진단, 처방 또는 치료를 대체하지 않습니다.

다음과 같은 경우에는 반드시 의료진과 상담하시기 바랍니다.

- 반복적인 저혈당 또는 고혈당이 발생하는 경우
- 혈당 변동성이 매우 큰 경우
- 약물 또는 인슐린 조정이 필요한 경우
- 어지러움, 식은땀, 의식 저하, 심한 갈증, 구토 등 응급 증상이 있는 경우
- 임신, 소아, 고령, 중증 질환 등 개별적인 관리 기준이 필요한 경우

응급상황이 의심되는 경우에는 앱 안내보다 **의료기관 또는 응급 대응 체계**를 우선 이용하시기 바랍니다.

---

## 6. 문서의 한계 및 업데이트

본 문서는 서비스 내 혈당 정보의 기준과 참고 자료를 설명하기 위한 문서입니다.  
의학적 권고와 가이드라인은 개정될 수 있으며, GluCoCare는 필요 시 본 문서를 업데이트할 수 있습니다.

---

## 7. 문의

의료 정보 또는 본 문서의 출처에 관한 문의사항이 있으시면 아래로 연락해 주세요.

- 이메일: yun8831@naver.com
- 버전: 1.1.2
- 최종 업데이트: 2026년 3월
