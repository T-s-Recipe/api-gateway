package shop.tsrecipe.gateway.exception

import shop.tsrecipe.gateway.util.getCurrentTimestamp

class ErrorResponse(
    val code: String?,
    val message: String,
    val timestamp: String = getCurrentTimestamp()
) {
    constructor(e: BaseException) : this(
        code = e.code,
        message = e.message,
        timestamp = e.timestamp
    )
}