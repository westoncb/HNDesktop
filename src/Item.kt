import com.google.gson.JsonObject
import java.util.*

open class Item(itemNode: JsonObject) {
    val id = itemNode.get("id").asInt
    val type = itemNode.get("type").asString
    val text = itemNode.get("text")?.asString
    val dead = itemNode.get("dead")?.asBoolean
    val title = itemNode.get("title").asString
    val points = itemNode.get("score").asInt
    val time = Date(itemNode.get("time").asLong * 1000).toString()
    val kids = itemNode.get("kids")?.asJsonArray
    val numKids = kids?.size()
    val user = itemNode.get("by").asString
    val url = itemNode.get("url")?.asString
    val totalComments = itemNode.get("descendants").asInt
}