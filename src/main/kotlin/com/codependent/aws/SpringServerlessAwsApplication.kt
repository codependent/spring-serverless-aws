package com.codependent.aws

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.function.context.FunctionRegistration
import org.springframework.cloud.function.context.FunctionalSpringApplication
import org.springframework.cloud.function.context.catalog.FunctionTypeUtils
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import java.security.SecureRandom
import java.util.function.Function
import java.util.function.Supplier
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue


@SpringBootApplication
class SpringServerlessAwsApplication : ApplicationContextInitializer<GenericApplicationContext> {

    private val secureRandom = SecureRandom()
    private val logger = LoggerFactory.getLogger(javaClass)
    private val objectMapper = jacksonObjectMapper()

    override fun initialize(context: GenericApplicationContext) {

        val uppercase = Function<Any, Map<String, String>> {
            logger.info("uppercase() Received {}", it)
            logger.info("type {}", it.javaClass)
            val ba: ByteArray = if(it is String) {
                it.toByteArray()
            } else {
                it as ByteArray
            }
            val map: Map<String, String> = objectMapper.readValue(ba)
            val proc = map.map { entry -> Pair(entry.key.uppercase(), entry.value.uppercase()) }.toMap()
            proc
        }

        val random= Function<Map<String, String>, Map<String, String>> {
            logger.info("random() Received {}", it)
            it.mapValues { entry -> "$entry${secureRandom.nextDouble()}" }
        }

        context.registerBean("uppercase", FunctionRegistration::class.java, Supplier {
            FunctionRegistration(uppercase)
                .type(FunctionTypeUtils.discoverFunctionType(uppercase, "uppercaseFn", context))
        })
        context.registerBean("random", FunctionRegistration::class.java, Supplier {
            FunctionRegistration(random)
                .type(FunctionTypeUtils.discoverFunctionType(random, "randomFn", context))
        })
    }

}

fun main(args: Array<String>) {
    FunctionalSpringApplication.run(SpringServerlessAwsApplication::class.java, *args)
    //runApplication<SpringServerlessAwsApplication>(*args)
}
