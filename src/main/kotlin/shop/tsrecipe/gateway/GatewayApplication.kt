package shop.tsrecipe.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import shop.tsrecipe.gateway.properties.AuthIgnoreProperties
import shop.tsrecipe.gateway.properties.JwtProperties

@EnableConfigurationProperties(value = [AuthIgnoreProperties::class, JwtProperties::class])
@SpringBootApplication
class GatewayApplication

suspend fun main(args: Array<String>) {
	runApplication<GatewayApplication>(*args)
}
