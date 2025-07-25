package shop.tsrecipe.gateway.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val status: HttpStatus? = HttpStatus.NOT_FOUND, var message: String) {
    UNDEFINED_EXCEPTION(status = HttpStatus.INTERNAL_SERVER_ERROR, message = "Sorry, undefined exception."),
    INVALID_REQUEST(status = HttpStatus.BAD_REQUEST, message = "Invalid request. check api documents."),

    // auth
    TOKEN_MISSING(status = HttpStatus.UNAUTHORIZED, message = "Missing or invalid token."),
    TOKEN_EXPIRED(status = HttpStatus.UNAUTHORIZED, message = "Token expired."),
    INVALID_TOKEN(status = HttpStatus.UNAUTHORIZED, message = "Invalid token."),
}