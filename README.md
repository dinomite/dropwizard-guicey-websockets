This is an example of using [dropwizard-guicey](https://github.com/xvik/dropwizard-guicey) and [dropwizard-websockets](https://github.com/LivePersonInc/dropwizard-websockets) together.

# Wiring
The application wiring is the only really interesting part of this due to the interaction between Guice and JSR-356 websockets.

[Inject](https://github.com/google/guice/wiki/Injections)ion into a `javax.websocket` servlet requires using a [Configurator](http://docs.oracle.com/javaee/7/api/javax/websocket/server/ServerEndpointConfig.Configurator.html), which, to utilize Guice, must have a handle on the `inejctor`.  Websocket servlets are magicked up by the [Builder]() calling `getEndpointInstance()` on your Configurator, so the `ExampleWsConfigurator` accesses the static member of the `Application`; while this is a bit ugly at first glance, the `guiceBunle` isis [`lateinit`](https://kotlinlang.org/docs/reference/properties.html#late-initialized-properties)ed, protecting it from access before initialization.

    // in ExampleApplication.kt
    companion object {
        lateinit var guiceBundle: GuiceBundle<ExampleConfiguration>
        fun injector(): Injector = guiceBundle.injector
    }

    // in ExampleWebsocket.kt
    class ExampleWsConfigurator : ServerEndpointConfig.Configurator() {
        override fun <T> getEndpointInstance(clazz: Class<T>): T {
            return ExampleApplication.injector().getInstance(clazz)
        }
    }

# Kotlin
This project is written in Kotlin.  Kotlin is better than Java.  I see no ~~compelling~~ reason to ever write straight Java again.
