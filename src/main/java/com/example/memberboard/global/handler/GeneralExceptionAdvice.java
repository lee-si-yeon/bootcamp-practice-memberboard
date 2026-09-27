package com.example.memberboard.global.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class GeneralExceptionAdvice {

    /**
     * @Valid 요청 본문 검증 실패
     * 예: 이메일 형식 오류, 비밀번호 길이 부족, 필수 값 누락
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        FieldError error = e.getBindingResult().getFieldErrors().getFirst();
        return error(HttpStatus.BAD_REQUEST, error.getField() + ": " + error.getDefaultMessage());
    }

    /**
     * JSON 형식 오류 또는 PathVariable 타입 불일치
     */
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        return error(HttpStatus.BAD_REQUEST, "요청 값의 형식이 올바르지 않습니다.");
    }

    /**
     * 서비스에서 지정한 HTTP 상태 코드 처리
     *
     * 400: 잘못된 요청
     * 403: 다른 사용자의 게시글/댓글 수정·삭제
     * 404: 존재하지 않는 회원/게시글/댓글
     * 409: 이미 가입된 이메일
     */
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException e) {
        String message = e.getReason() == null ? "요청을 처리할 수 없습니다." : e.getReason();
        return ResponseEntity.status(e.getStatusCode())
                .body(new ErrorResponse(e.getStatusCode().value(), message));
    }

    /**
     * 처리하지 못한 예외
     */
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        log.error("Unexpected error", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), message));
    }

    public record ErrorResponse(
            int status,
            String message
    ) {}
}
