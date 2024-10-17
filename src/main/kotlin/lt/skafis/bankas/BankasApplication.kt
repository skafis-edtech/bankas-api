package lt.skafis.bankas

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// @EnableSentry(
//    dsn = "\${SENTRY_DSN}",
//    exceptionResolverOrder = Ordered.LOWEST_PRECEDENCE,
// )
@SpringBootApplication
class BankasApplication

fun main(args: Array<String>) {
    runApplication<BankasApplication>(*args)
}
