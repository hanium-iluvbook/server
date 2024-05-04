package hanium.server.iluvbook.exception.advice;

import hanium.server.iluvbook.exception.BusinessException;
import hanium.server.iluvbook.exception.ErrorResponse;
import hanium.server.iluvbook.exception.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.naming.SizeLimitExceededException;

/**
 * Spring 전역에서의 예외처리를 위한 Advice 클래스.
 * 로깅 및 발생한 예외마다의 ErrorResponse 로 바꾸어 핸들링
 *
 * @author ijin
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionAdvice {

    // 비즈니스 예외 처리시 발생
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        return createErrorResponse(e, e.getErrorCode());
    }

    // javax.validation.Valid or @Validated 으로 binding error 발생시 발생
    // HttpMessageConverter 에서 등록한 HttpMessageConverter binding 못할경우 발생
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> methodArgumentValidation(MethodArgumentNotValidException e) {
        return createErrorResponse(e, ErrorCode.INVALID_REQUEST_PARAMETER);
    }

    // @ModelAttribute 으로 바인딩 에러시 발생
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> bindException(BindException e) {
        return createErrorResponse(e, ErrorCode.INVALID_REQUEST_PARAMETER);
    }

    // 지원하지 않은 HTTP Method 호출 할 경우 발생
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> requestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return createErrorResponse(e, ErrorCode.INVALID_METHOD_TYPE);
    }

    // JSON 형식 지키지 않았을 시 발생
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> invalidHttpMessageParsing(HttpMessageNotReadableException e) {
        return createErrorResponse(e, ErrorCode.INVALID_JSON_TYPE);
    }

    // 데이터 잘못 넘어갔을 경우 발생
    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<ErrorResponse> illegalArgumentException(IllegalArgumentException e) {
        return createErrorResponse(e, ErrorCode.INVALID_REQUEST_PARAMETER);
    }

    // 데이터 무결성 위반한 경우 발생
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponse> dataIntegrityViolationException(DataIntegrityViolationException e) {
        return createErrorResponse(e, ErrorCode.DATA_INTEGRITY_VIOLATE);
    }

    // 이미지 크기 초과시 발생
    @ExceptionHandler({MaxUploadSizeExceededException.class, SizeLimitExceededException.class, MissingServletRequestPartException.class, MultipartException.class})
    protected ResponseEntity<ErrorResponse> imageFileSizeExceedException(Exception e) {
        return createErrorResponse(e, ErrorCode.FILE_SIZE);
    }

    // 나머지 에러 여기서 핸들링
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        return createErrorResponse(e, ErrorCode.SERVICE_UNAVAILABLE);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(Exception e, ErrorCode errorCode) {
        // Create ExceptionResponse
        ResponseEntity<ErrorResponse> response = ErrorResponse.toResponseEntity(errorCode);

        // Logging And Return with Exception
        ExceptionResponseLogger.logResponse(response, e);
        return response;
    }
}
