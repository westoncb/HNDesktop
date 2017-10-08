import com.google.gson.JsonObject
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

val prettyTime = PrettyTime()

open class Item(itemNode: JsonObject) {
    val id = itemNode.get("id").asInt
    val type = itemNode.get("type").asString
    val text = itemNode.get("text")?.asString
    val dead = itemNode.get("dead")?.asBoolean
    val title = itemNode.get("title")?.asString
    val points = itemNode.get("score")?.asInt

    // Note: the results PrettyTime is giving do not match actual HN... not sure
    // where the problem is
    val time = prettyTime.format(Date(itemNode.get("time").asLong * 1000))

    val kids = itemNode.get("kids")?.asJsonArray
    val numKids = kids?.size()
    val user = itemNode.get("by")?.asString
    val url = itemNode.get("url")?.asString
    val totalComments = itemNode.get("descendants")?.asInt

    override fun toString(): String {
        return "ID: " + id + ", Type: " + type + ", Title: " + title + ", dead: " + dead + ", kids: " + numKids
    }
}