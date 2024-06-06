package lt.skafis.bankas

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BankasApplication

fun main(args: Array<String>) {
	runApplication<BankasApplication>(*args)
}
