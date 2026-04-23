# GluCoCare

CGM 기반 AI 혈당 예측 및 관리 플랫폼

---

## 기술 스택

| 영역       | 기술             |
|----------|----------------|
| Frontend | Flutter (Dart) |
| 상태관리     | Riverpod       |
| Backend  | Spring Boot    |
| 캐시       | Redis          |
| 푸시       | FCM            |
| 아키텍처     | UseCase 기반     |

---

## 프로젝트 구조

```
lib/
├── core/                  # 앱 공통 핵심 로직
│   ├── background/        # 백그라운드 동기화
│   ├── data/              # 로컬 저장소
│   ├── health/            # HealthKit 연동
│   └── notification/      # FCM 알림
├── features/              # 기능별 모듈
│   ├── glucose_history/
│   ├── member/
│   ├── mission/
│   ├── patient/
│   └── point/
└── shared/                # 공통 위젯/상수
    ├── constants/
    │   ├── app_colors.dart
    │   └── local_repository_key.dart
    └── widgets/
        ├── common_app_bar.dart
        ├── common_back_button.dart
        ├── common_button.dart
        ├── common_check_box.dart
        ├── common_date_picker.dart
        └── common_time_picker.dart
```

---

## 코드 디자인 규칙

### 1. 터치 위젯 선택 기준

| UI 유형             | 사용 위젯                                 | 적용 사례                     |
|-------------------|---------------------------------------|---------------------------|
| **AppBar 아이콘 버튼** | `IconButton`                          | AppBar의 뒤로가기, actions 아이콘 |
| **주요 CTA 버튼**     | `Material + InkWell`                  | 저장, 확인, 등록 같은 주요 액션       |
| **텍스트 링크**        | `TextButton`                          | 로그아웃, 회원가입, 다이얼로그 버튼      |
| **리스트 아이템**       | `Material + InkWell`                  | 선택 가능한 카드, 설정 항목          |
| **컨텐츠 영역 아이콘**    | `GestureDetector`                     | 새로고침, 복사 등 인라인 아이콘        |
| **선택형 카드**        | `GestureDetector + AnimatedContainer` | 배경색이 바뀌는 선택 UI (운동 선택)    |
| **전체 화면 제스처**     | `GestureDetector`                     | 키보드 닫기 등 화면 전체 터치         |

#### 선택 가이드

```
AppBar/ToolBar의 아이콘인가?
→ YES: IconButton 사용

Material 리플 효과가 어울리는가?
→ YES: Material + InkWell
→ NO: GestureDetector + AnimatedContainer

텍스트만 있는 버튼인가?
→ YES: TextButton
```

---

### 2. Press 피드백 패턴

#### 기본 패턴

버튼을 누를 때 시각적 반응을 제공해야 한다. `CommonButton`을 기준으로 삼는다.

#### 구현 방법

**1. AnimatedScale + AnimatedContainer**
- Scale: 0.97배로 축소 (90ms)
- Color: 8% 어둡게 (`Color.lerp(baseColor, Colors.black, 0.08)`)
- InkWell의 splash/highlight는 투명 처리

**2. 상태 관리**
- `_isPressed` 플래그 사용
- `onTapDown`: true
- `onTapUp`: false
- `onTapCancel`: false

**3. Material + InkWell 조합**
- 배경색 변경: `_isPressed ? 변경색 : 기본색`
- SettingActionButton 참고

#### 적용 대상

- ✅ 주요 CTA 버튼 (CommonButton, MissionButton)
- ✅ 리스트 아이템 (SettingActionButton)
- ✅ 선택형 카드 (간격 버튼)
- ❌ 아이콘만 있는 버튼 (IconButton이 자체 처리)
- ❌ TextButton (자체 ripple 효과)

---

### 3. 햅틱 피드백

#### 기본 원칙

> **"햅틱은 미션 완료 순간에만 사용한다. 나머지 버튼은 애니메이션으로만 피드백을 제공한다."**

햅틱은 신호(signal)다. 남발하면 노이즈가 되어 정작 중요한 순간의 의미가 희석된다.  
GlucoCare에서 햅틱을 느낄 수 있는 유일한 순간은 미션 완료이며, 이를 통해 사용자는 해당 진동이 특별한 달성 순간임을 명확히 학습한다.

#### ✅ 햅틱을 넣는 경우

| 상황    | 햅틱 종류           | 예시                 |
|-------|-----------------|--------------------|
| 미션 완료 | `lightImpact()` | MissionButton 완료 탭 |

#### ❌ 햅틱을 넣지 않는 경우 (전부)

| 상황                   | 피드백 방식                  |
|----------------------|-------------------------|
| 일반 CTA 버튼 (저장, 확인 등) | AnimatedScale + 색상 변화   |
| 뒤로가기/닫기              | 없음                      |
| 상태 변경 (선택/토글)        | AnimatedContainer 색상 변화 |
| 체크박스/스위치             | 없음                      |
| 파괴적 액션 (삭제 등)        | 없음                      |
| TextField, 스크롤, 차트 등 | 없음                      |

#### 코드 작성

```dart
import 'package:flutter/services.dart';

// ✅ 미션 완료 시에만 사용
onTap: () {
  HapticFeedback.heavyImpact();
  completeMission();
}
```

---

### 4. 중복 탭 방지

#### 필요한 경우

- 비동기 작업 버튼 (저장, 등록, API 호출)
- 뒤로가기 버튼
- 화면 전환 버튼

#### 구현 방법

**1. 비동기 액션**
```dart
bool _isLoading = false;

Future<void> _handleTap() async {
  if (_isLoading) return;  // 중복 방지
  setState(() => _isLoading = true);
  try {
    await someAction();
  } finally {
    if (mounted) setState(() => _isLoading = false);
  }
}
```

**2. 뒤로가기**
```dart
bool _isPopping = false;

void _handlePop() {
  if (_isPopping) return;  // 중복 방지
  if (!Navigator.canPop(context)) return;
  _isPopping = true;
  Navigator.pop(context);
}
```

**주의:** CommonAppBar에서는 중복 pop 방지를 구현하지 않았음 (대부분 StatelessWidget 사용)

---

### 5. 상태별 스타일 관리

#### 원칙

삼항연산자가 2중 이상 중첩되면 getter로 분리한다.

#### 기준

```dart
// ✅ 단순 삼항연산자는 OK
color: isSelected ? AppColors.mainColor : AppColors.fontGray200Color

// ❌ 2중 이상 중첩은 가독성 저하
color: isCompleted ? completedColor : canComplete ? activeColor : disabledColor

// ✅ getter로 분리
Color get backgroundColor {
  if (missionResponse.isCompleted) return AppColors.missionCompletedBackgroundColor;
  if (missionResponse.canComplete) return AppColors.missionActiveBackgroundColor;
  return AppColors.missionDisabledBackgroundColor;
}
```

#### 적용 사례

MissionButton의 색상 관리 참고

---

### 6. withOpacity 사용 금지

Flutter 3.27+부터 `withOpacity`가 deprecated되었다. `withValues(alpha: ...)`를 사용한다.

```dart
// ❌ Deprecated
Colors.black.withOpacity(0.1)

// ✅ 올바른 방법
Colors.black.withValues(alpha: 0.1)
```

---

### 7. 공통 위젯 목록

새로운 화면을 만들 때 아래 공통 위젯을 우선 사용한다.

| 위젯                 | 용도                  |
|--------------------|---------------------|
| `CommonButton`     | 하단 CTA 버튼           |
| `CommonAppBar`     | 앱바                  |
| `CommonBackButton` | 뒤로가기 (중복 pop 방지 포함) |
| `CommonCheckBox`   | 체크박스                |
| `CommonDatePicker` | 날짜 선택               |
| `CommonTimePicker` | 시간 선택               |

---

### 8. Riverpod 규칙

#### watch vs read

| 상황         | 사용                            | 이유             |
|------------|-------------------------------|----------------|
| 상태를 UI에 반영 | `ref.watch(provider)`         | 상태 변경 시 자동 리빌드 |
| 액션만 실행     | `ref.read(provider.notifier)` | 일회성 호출         |

#### 주의사항

**setState와 ref.read 조합 금지**

```dart
// ❌ 잘못된 패턴
setState(() => ref.read(provider.notifier).update(value));

// ✅ 올바른 패턴
ref.read(provider.notifier).update(value);
// watch가 자동으로 UI를 리빌드하므로 setState 불필요
```

#### 예외

로컬 상태를 업데이트하는 경우 setState 사용 가능
```dart
setState(() => _localState = newValue);
```

---

### 9. mounted 체크

#### 원칙

비동기 작업 후 setState 호출 전에 반드시 `mounted`를 확인한다.

#### 이유

위젯이 이미 dispose된 상태에서 setState를 호출하면 에러 발생

#### 적용 위치

```dart
Future<void> fetchData() async {
  try {
    final result = await repository.fetch();

    // ✅ setState 전 mounted 체크
    if (!mounted) return;
    setState(() => _data = result);

  } finally {
    // ✅ finally에서도 체크
    if (mounted) setState(() => _isLoading = false);
  }
}
```

#### 주의

- `await` 이후에는 항상 mounted 체크
- 화면 전환 후 호출되는 setState는 특히 주의

---

## 색상 시스템

### 원칙

모든 색상은 `AppColors`에서 가져온다. 직접 색상 코드 사용 금지.

```dart
// ❌ 직접 색상 코드
color: Color(0xFF1D9E75)
color: Colors.white

// ✅ AppColors 사용
color: AppColors.mainColor
color: AppColors.whiteColor
```

### 주요 색상

| 카테고리    | 키                             | 용도        |
|---------|-------------------------------|-----------|
| **브랜드** | `mainColor`                   | 메인 컬러     |
| **배경**  | `backgroundColor`             | 화면 배경     |
| **텍스트** | `fontGray100` ~ `fontGray900` | 텍스트 단계별   |
| **미션**  | `missionActive*`              | 미션 활성 상태  |
|         | `missionCompleted*`           | 미션 완료 상태  |
|         | `missionDisabled*`            | 미션 비활성 상태 |
| **혈당**  | `glucoseNormal*`              | 정상 구간     |
|         | `glucoseWarning*`             | 경고 구간     |
|         | `glucoseDanger*`              | 위험 구간     |