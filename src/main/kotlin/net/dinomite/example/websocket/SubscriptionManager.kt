package net.dinomite.example.websocket

import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.concurrent.ConcurrentHashMap
import javax.websocket.Session

@Singleton
class SubscriptionManager
@Inject
constructor() {
    val subscriptions: ConcurrentHashMap<String, Session> = ConcurrentHashMap()

    fun add(id: String, session: Session) {
        subscriptions.put(id, session)
    }

    fun remove(id: String) {
        subscriptions.remove(id)
    }

    fun sendAll(message: String) {
        subscriptions.forEach {
            it.value.asyncRemote.sendText(message)
        }
    }
}
