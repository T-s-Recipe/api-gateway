package shop.tsrecipe.gateway.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpMethod

@ConfigurationProperties(prefix = "whitelist")
data class WhitelistProperties(
    var service: Set<String> = emptySet(),
    var api: Map<String, List<Endpoint>> = emptyMap(),
    var swaggerPath: List<String> = emptyList()
) {
    fun isWhitelist(serviceName: String, method: HttpMethod, path: String): Boolean {
        return this.api[serviceName]?.any { it.method == method && it.path == path } ?: false
    }

    fun isSwaggerPath(path: String): Boolean {
        return this.swaggerPath.any { path.startsWith(it) }
    }
}

data class Endpoint(
    var method: HttpMethod = HttpMethod.GET,
    var path: String = "/"
)
