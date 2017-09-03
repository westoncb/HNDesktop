import java.util.ArrayList
import java.util.HashMap

/**
 * Created by weston on 3/10/17.
 */
object PubSub {
    private val eventMap = HashMap<String, ArrayList<Subscriber>>()

    val STORY_ICON_LOADED = "story_icon_loaded"

    @JvmOverloads
    fun publish(eventName: String, data: Any? = null) {
        var subscribers: ArrayList<Subscriber>? = eventMap[eventName]

        if (subscribers == null) {
            subscribers = ArrayList()
            eventMap.put(eventName, subscribers)

            return
        } else {
            for (subscriber in subscribers) {
                subscriber.messageArrived(eventName, data)
            }
        }
    }

    fun subscribe(eventName: String, subscriber: Subscriber) {
        var subscribers: ArrayList<Subscriber>? = eventMap[eventName]

        if (subscribers == null) {
            subscribers = ArrayList()
            eventMap.put(eventName, subscribers)
        }

        subscribers.add(subscriber)
    }

    interface Subscriber {
        fun messageArrived(eventName: String, data: Any?)
    }
}
