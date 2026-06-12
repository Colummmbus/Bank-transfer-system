package exception;
// 계좌 비밀번호 오류 예외

public class InvalidAccountPasswordException extends RuntimeException {
    public InvalidAccountPasswordException(String message) {
        super(message);
    }
}
