package shop.tsrecipe.gateway.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "auth-ignore")
class AuthIgnoreProperties {
    lateinit var host: Set<String>
    lateinit var api: Map<String, Set<String>>
}