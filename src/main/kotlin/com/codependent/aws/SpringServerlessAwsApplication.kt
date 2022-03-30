package com.codependent.aws

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.security.SecureRandom
import java.util.*

@SpringBootApplication
class SpringServerlessAwsApplication{

    private val random = SecureRandom()
    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun uppercase(): (Map<String, String>) -> Map<String, String> = {
        logger.info("uppercase() Received {}", it)
        it.map { entry -> Pair(entry.key.uppercase(), entry.value.uppercase()) }.toMap()
    }

    @Bean
    fun random(): (Map<String, String>) -> Map<String, String> = {
        logger.info("random() Received {}", it)
        it.mapValues { entry -> "$entry${random.nextDouble()}" }
    }

}

fun main(args: Array<String>) {
    runApplication<SpringServerlessAwsApplication>(*args)
}
