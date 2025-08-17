package shop.tsrecipe.gateway.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import shop.tsrecipe.gateway.util.Logging
import shop.tsrecipe.gateway.util.baseResponse

@RestControllerAdvice
class GlobalExceptionHandler: Logging {
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ResponseEntity<ErrorResponse> {
        return baseResponse(e.httpStatus, ErrorResponse(e))
    }
}