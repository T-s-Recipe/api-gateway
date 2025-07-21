package shop.tsrecipe.gateway.exception

import org.springframework.http.HttpStatus
import shop.tsrecipe.gateway.util.getCurrentTimestamp

class BaseException(
    val httpStatus: HttpStatus,
    val code: String? = null,
    override val message: String
) : RuntimeException() {

    val timestamp = getCurrentTimestamp()

    constructor(e: ErrorCode) : this(
        httpStatus = e.status!!,
        code = e.name,
        message = e.message,
    )
}