import com.google.gson.JsonObject
import javax.swing.Icon
import javax.swing.UIManager


/**
 * Created by weston on 8/27/17.
 */

class Story : Item {
    var favicon: Icon? = null

    constructor(storyNode: JsonObject) : super(storyNode) {
        this.favicon = UIManager.getIcon("FileView.fileIcon")

        Thread(Runnable { getFavicon() }).start()
    }

    fun getFavicon() {
        if (this.url != null) {
            val icon = FaviconFetcher.getFavicon(this.url)
            if (icon != null) {
                this.favicon = icon
                PubSub.publish(PubSub.STORY_ICON_LOADED)
            }
        }
    }

    override fun toString(): String {
        return this.title ?: "--missing title--"
    }
}