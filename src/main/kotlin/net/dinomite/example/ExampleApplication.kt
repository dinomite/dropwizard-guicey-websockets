package net.dinomite.example

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.inject.Injector
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.dropwizard.websockets.WebsocketBundle
import net.dinomite.example.websocket.ExampleWebsocket
import net.dinomite.example.websocket.ExampleWsConfigurator
import ru.vyarus.dropwizard.guice.GuiceBundle
import javax.websocket.server.ServerEndpointConfig

class ExampleApplication : Application<ExampleConfiguration>() {
    companion object {
        lateinit var guiceBundle: GuiceBundle<ExampleConfiguration>
        fun injector(): Injector = guiceBundle.injector

        @JvmStatic fun main(args: Array<String>) {
            ExampleApplication().run(*args)
        }
    }

    override fun getName(): String {
        return "example"
    }

    override fun initialize(bootstrap: Bootstrap<ExampleConfiguration>) {
        bootstrap.objectMapper.registerModule(KotlinModule())

        guiceBundle = GuiceBundle.builder<ExampleConfiguration>()
                .enableAutoConfig(javaClass.`package`.name)
                .build()
        bootstrap.addBundle(guiceBundle)

        val config = ServerEndpointConfig.Builder.create(ExampleWebsocket::class.java, "/ws")
                .configurator(ExampleWsConfigurator())
                .build()
        bootstrap.addBundle(WebsocketBundle(config))
    }

    override fun run(configuration: ExampleConfiguration, environment: Environment) {
    }
}
