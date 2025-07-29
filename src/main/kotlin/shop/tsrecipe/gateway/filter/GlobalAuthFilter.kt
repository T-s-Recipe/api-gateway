package shop.tsrecipe.gateway.filter

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import shop.tsrecipe.gateway.auth.JwtService
import shop.tsrecipe.gateway.exception.BaseException
import shop.tsrecipe.gateway.exception.ErrorCode
import shop.tsrecipe.gateway.properties.WhitelistProperties

@Component
class GlobalAuthFilter(
    private val whitelistProperties: WhitelistProperties,
    private val jwtService: JwtService
) : GlobalFilter, Ordered {

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        try {
            val path = request.path.value().trimStart('/')
            val serviceName = path.substringBefore("/")
            val apiPath = path.substringAfter(serviceName)
            val httpMethod = exchange.request.method

            if (isAuthIgnoredRequest(serviceName, httpMethod, apiPath)) return chain.filter(exchange)
        } catch (e: Exception) {
            throw BaseException(ErrorCode.INVALID_REQUEST)
        }

        val authHeader = exchange.request.headers.getFirst(HeaderNames.AUTHORIZATION)
        val token = getAuthTokenIfValid(authHeader)

        val memberId = jwtService.verifyToken(token)

        val mutatedRequest = request.mutate()
            .headers { headers ->
                headers.remove(HeaderNames.X_MEMBER_ID)
                headers.add(HeaderNames.X_MEMBER_ID, memberId)
            }
            .build()

        val mutatedExchange = exchange.mutate()
            .request(mutatedRequest)
            .build()

        return chain.filter(mutatedExchange)
    }

    private fun isAuthIgnoredRequest(serviceName: String, httpMethod: HttpMethod, path: String): Boolean {
        return (whitelistProperties.service.contains(serviceName))
                || whitelistProperties.isSwaggerPath(path)
                || (whitelistProperties.isWhitelist(serviceName, httpMethod, path))
    }

    private fun getAuthTokenIfValid(value: String?): String {
        if (value.isNullOrBlank() || !value.startsWith(HeaderNames.BEARER_PREFIX)) {
            throw BaseException(ErrorCode.TOKEN_MISSING)
        }
        return value.removePrefix(HeaderNames.BEARER_PREFIX).trim()
    }

    override fun getOrder(): Int {
        return -1
    }
}

