package lt.skafis.bankas.config

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Aspect
@Component
@ConditionalOnProperty(name = ["logging.aspect.enabled"], havingValue = "true", matchIfMissing = true)
class LoggingAspect {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Around("@within(lt.skafis.bankas.config.Logged)")
    fun logAround(joinPoint: ProceedingJoinPoint): Any? {
        val methodName = joinPoint.signature.name
        val className = joinPoint.signature.declaringTypeName
        val args = joinPoint.args

        val authentication = SecurityContextHolder.getContext().authentication as? JwtAuthenticationToken
        val userId = authentication?.token?.claims?.get("user_id") as? String
        val userRoles = authentication?.token?.claims?.get("roles") as? List<*>

        val result =
            try {
                val proceed = joinPoint.proceed()

                logger.info("user: $userId , roles: $userRoles : $className.$methodName called with args: $args")
                proceed
            } catch (ex: Throwable) {
                logger.error("Method: $methodName threw an exception: ${ex.message}")
                throw ex
            }

        return result
    }
}
