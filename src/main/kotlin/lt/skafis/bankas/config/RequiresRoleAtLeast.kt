package lt.skafis.bankas.config

import lt.skafis.bankas.model.Role

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresRoleAtLeast(val role: Role)
