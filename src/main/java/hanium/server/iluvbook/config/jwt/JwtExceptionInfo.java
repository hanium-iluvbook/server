package hanium.server.iluvbook.config.jwt;

import hanium.server.iluvbook.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.springframework.http.HttpStatus.*;

/**
 * 커스텀 JWT 예외정보를 담은 Enum 클래스
 *
 * @author ijin
 */
@Getter
@AllArgsConstructor
public enum JwtExceptionInfo {

    /**
     * 예외 정보
     */
    EXPIRED_TOKEN(UNAUTHORIZED, "만료된 토큰", "AUTH-001"),
    WRONG_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰", "AUTH-002"),
    UNSUPPORTED_TOKEN(UNAUTHORIZED, "지원하지 않는 토큰 형식", "AUTH-003"),
    EMPTY_TOKEN(UNAUTHORIZED, "토큰이 없음", "AUTH-004"),
    ACCESS_DENIED(FORBIDDEN, "권한이 없음", "AUTH_005"),
    UNKNOWN_ERROR(SERVICE_UNAVAILABLE, "알수 없는 에러", "AUTH-006");

    /**
     * HTTP Status 와 각 Enum 에 담길 커스텀 예외 메세지와 에러코드
     */
    private final HttpStatus status;
    private final String message;
    private final String code;

    public void setResponse(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(createJwtExceptionBody(this)));
    }

    private static ErrorResponse createJwtExceptionBody(JwtExceptionInfo exception) {
        return ErrorResponse.builder()
                .status(401)
                .error(exception.getStatus().name())
                .code(exception.getCode())
                .message(exception.getMessage())
                .build();
    }
}
