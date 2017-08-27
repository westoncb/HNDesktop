import com.google.gson.JsonObject
import java.util.*

/**
 * Created by weston on 8/27/17.
 */

class Story(storyNode: JsonObject) {
    val title = storyNode.get("title").asString
    val points = storyNode.get("score").asInt
    val time = Date(storyNode.get("time").asLong * 1000).toString()
    val kids = storyNode.get("kids")?.asJsonArray
    val numKids = kids?.size()
    val user = storyNode.get("by").asString
}