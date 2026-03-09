package ptystudybuddy.psb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PsbApplication

fun main(args: Array<String>) {
    runApplication<PsbApplication>(*args)
}
