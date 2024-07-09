package lt.skafis.bankas.config

import lt.skafis.bankas.service.UserService
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Aspect
@Component
class RoleAspect {

    @Autowired
    private lateinit var userService: UserService

    @Before("@within(lt.skafis.bankas.config.RequiresRoleAtLeast) || @annotation(lt.skafis.bankas.config.RequiresRoleAtLeast)")
    fun checkRole(joinPoint: JoinPoint) {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method

        val requiresRole = method.getAnnotation(RequiresRoleAtLeast::class.java)
            ?: joinPoint.target::class.java.getAnnotation(RequiresRoleAtLeast::class.java)

        if (requiresRole != null) {
            userService.grantRoleAtLeast(requiresRole.role)
        }
    }
}
