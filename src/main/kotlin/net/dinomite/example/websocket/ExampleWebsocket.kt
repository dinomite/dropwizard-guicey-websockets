package net.dinomite.example.websocket

import com.codahale.metrics.annotation.ExceptionMetered
import com.codahale.metrics.annotation.Metered
import com.codahale.metrics.annotation.Timed
import com.google.inject.Inject
import net.dinomite.example.ExampleApplication
import org.slf4j.LoggerFactory
import java.io.IOException
import javax.websocket.*
import javax.websocket.server.ServerEndpoint
import javax.websocket.server.ServerEndpointConfig

@Metered
@Timed
@ExceptionMetered
@ServerEndpoint(value = "/ws", configurator = ExampleWsConfigurator::class)
class ExampleWebsocket
@Inject
constructor(val subscriptionManager: SubscriptionManager) {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    @OnOpen
    @Throws(IOException::class)
    fun onOpen(session: Session) {
        logger.info("Adding session <${session.id}>")
        subscriptionManager.add(session.id, session)
    }

    @OnMessage
    fun onMessage(session: Session, message: String) {
        logger.debug("Got message <$message> from <${session.id}>")
        subscriptionManager.sendAll(message.toUpperCase())
    }

    @OnClose
    fun onClose(session: Session, reason: CloseReason) {
        logger.info("Closing session <${session.id}>: ${reason.reasonPhrase}")
        subscriptionManager.remove(session.id)
    }

    @OnError
    fun onError(session: Session, throwable: Throwable) {
        logger.warn("Session <${session.id}> error: ${throwable.message}")
        subscriptionManager.remove(session.id)
    }
}

class ExampleWsConfigurator : ServerEndpointConfig.Configurator() {
    override fun <T> getEndpointInstance(clazz: Class<T>): T {
        return ExampleApplication.injector().getInstance(clazz)
    }
}
