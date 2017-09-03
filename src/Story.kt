import com.google.gson.JsonObject

/**
 * Created by weston on 8/27/17.
 */

class Story : Item {
    constructor(storyNode: JsonObject) : super(storyNode)

    override fun toString(): String {
        return this.title ?: "--missing title--"
    }
}