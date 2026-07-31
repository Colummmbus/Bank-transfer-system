# 금융 이체 시스템 (Bank Transfer System)

Java Swing, JDBC, MySQL을 활용하여 구현한 금융 이체 시스템입니다.

회원가입, 로그인, 계좌 생성, 계좌 조회, 이체, 거래내역 조회 기능을 구현했으며, 실제 금융 시스템에서 고려되는 계좌 검증, 이체 한도, 트랜잭션, 동시성 제어, 데드락 방지 등을 반영하여 데이터 정합성을 보장하는 이체 프로세스를 구현했습니다.

## 핵심 기술

- 🔒 JDBC Transaction (Commit / Rollback)
- 🔒 SELECT ... FOR UPDATE
- 🔒 Deadlock Prevention (Lock Ordering)
- ⚡ Composite Index (Transaction History)

--- 

# 주요 기능

- 회원가입 및 로그인
- 계좌 생성 및 계좌 조회
- 계좌 간 이체
- 거래내역 전체 조회
- 기간별 거래내역 조회
- 거래 상세 조회

---

# 핵심 구현 내용

---

## 시스템 설계

- Domain - DAO - Service - GUI 계층형 아키텍처 설계
- Service 계층 중심의 비즈니스 로직 구현
- 업무 상황별 사용자 정의 Exception 분리

---

## 금융 업무 로직

### 금융 이체 검증

이체 수행 전 다음 검증을 순차적으로 수행하도록 구현했습니다.

- 이체 요청값 검증
- 출금 계좌 존재 여부 및 본인 계좌 확인
- 출금 계좌 상태 확인
- 입금 계좌 존재 여부 및 상태 확인
- 계좌 비밀번호 검증
- 잔액 검증
- 1회 이체 한도 검증
- 1일 이체 한도 검증

모든 검증을 통과한 경우에만 실제 이체가 수행됩니다.

### 이체 한도

- 1회 이체 한도
- 1일 이체 한도

당일 성공한 거래 금액을 합산한 뒤 현재 요청 금액을 더하여 일일 한도를 초과하는지 검증하도록 구현했습니다.

### 거래내역 조회

- 전체 거래 조회
- 기간별 거래 조회
- 거래 상세 조회

조회 계좌를 기준으로 입금과 출금을 구분하여 표시하도록 구현했습니다.

---

## 🔒 데이터 정합성 보장

### JDBC 트랜잭션

출금, 입금, 거래내역 저장을 하나의 트랜잭션으로 처리하여 모든 작업이 성공한 경우에만 Commit하고, 오류 발생 시 Rollback하도록 구현했습니다.

### 동시성 제어

동일 계좌에 대한 여러 이체 요청이 동시에 처리되는 상황을 고려하여 `SELECT ... FOR UPDATE`를 적용하고, 계좌 레코드를 잠근 후 잔액을 갱신하여 데이터 정합성을 유지하도록 구현했습니다.

### 데드락 방지

송금 계좌와 입금 계좌를 잠글 때 account_id가 작은 계좌부터 일관된 순서로 Lock을 획득하도록 구현하여 데드락 발생 가능성을 줄였습니다.

### 인덱스 적용

거래내역 조회 성능 향상을 위해 `(from_account_id, t_created_at)` 복합 인덱스를 적용했습니다.

---

# 기술 스택

- Java 21: 최신 문법이며 장기 지원이 가능한 버전을 택해 안정적인 개발 환경을 확보했습니다.
- Java Swing
- JDBC: Java에서 DB에 접근하기 위한 표준 API로, 트랜잭션을 직접 제어해 원자성을 확보했습니다.
- MySQL: 관계형 DB의 제약 조건과 트랜잭션, 잠금 기능을 활용해 정합성을 유지하기 위해 선택했습니다.
- Git / GitHub

---

# 프로젝트 구조

```text
src
├─ dao
├─ domain
├─ exception
├─ gui
├─ main
├─ service
└─ util
```

---

# 데이터베이스 설계

## users

- user_id
- login_id
- password
- name

## accounts

- account_id
- user_id
- account_number
- balance
- account_password
- status
- one_time_limit
- daily_limit
- created_at

## transactions

- transaction_id
- from_account_id
- to_account_id
- amount
- type
- t_status
- t_created_at

---

# 실행 화면

- 로그인
- 회원가입
- 메인 화면
- 계좌 조회
- 이체
- 거래내역 조회

---

# 프로젝트에서 배운 점

- 계층형 아키텍처를 적용하며 역할 분리와 유지보수성을 고려한 설계의 중요성을 경험했습니다.
- 금융 업무 규칙을 시스템 로직으로 구현하며 도메인 중심의 비즈니스 로직 설계를 경험했습니다.
- 트랜잭션과 동시성 제어를 적용하여 데이터 정합성을 보장하는 방법을 학습했습니다.

---

# 향후 개선 사항

- BCrypt를 활용한 비밀번호 암호화
- 계좌 잠금 정책 적용
- 관리자 기능 구현
- JUnit 테스트 코드 작성
- Spring Boot 기반 웹 애플리케이션으로 확장
