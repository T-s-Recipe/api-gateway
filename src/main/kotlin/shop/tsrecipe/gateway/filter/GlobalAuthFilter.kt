package shop.tsrecipe.gateway.filter

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import shop.tsrecipe.gateway.auth.JwtService
import shop.tsrecipe.gateway.exception.BaseException
import shop.tsrecipe.gateway.exception.ErrorCode
import shop.tsrecipe.gateway.properties.AuthIgnoreProperties

@Component
class GlobalAuthFilter(
    private val authIgnoreProperties: AuthIgnoreProperties,
    private val jwtService: JwtService
) : GlobalFilter, Ordered {

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        val host = request.uri.host
        val path = request.path.toString()
        val serviceName = host.substringBefore(".")

        if (isAuthIgnoredRequest(serviceName, path)) return chain.filter(exchange)

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

    private fun isAuthIgnoredRequest(serviceName: String, path: String): Boolean {
        return (authIgnoreProperties.host.contains(serviceName))
                || (authIgnoreProperties.api[serviceName]?.contains(path) == true)
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

