import com.google.gson.JsonObject
import java.awt.Image
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.UIManager


/**
 * Created by weston on 8/27/17.
 */

class Story : Item {
    var favicon: Icon? = null
    var bigIcon: Image? = null

    constructor(storyNode: JsonObject) : super(storyNode) {
        this.favicon = UIManager.getIcon("FileView.fileIcon")

        Thread(Runnable { getFavicon() }).start()
    }

    fun getFavicon() {
        if (this.url != null) {
            val icon = FaviconFetcher.getFavicon(this.url)
            if (icon != null) {
                val image = FaviconFetcher.getScaledImage(icon, 16, 16)
                this.favicon = ImageIcon(image)
                this.bigIcon = FaviconFetcher.getScaledImage(icon, Math.min(48, icon.getWidth(null)), Math.min(48, icon.getHeight(null)))
                PubSub.publish(PubSub.STORY_ICON_LOADED)
            }
        }
    }

    override fun toString(): String {
        return this.title ?: "--missing title--"
    }
}