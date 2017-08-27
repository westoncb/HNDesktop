import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import javafx.application.Platform
import java.awt.Dimension
import javax.swing.*
import java.awt.Toolkit
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import java.io.InputStreamReader
import javax.swing.UIManager
import javax.swing.plaf.metal.DefaultMetalTheme
import javax.swing.plaf.metal.MetalLookAndFeel
import sun.jvm.hotspot.utilities.soql.SOQLEngine.getEngine
import javafx.scene.Scene
import javafx.scene.web.WebView
import javafx.embed.swing.JFXPanel




/**
 * Created by weston on 8/26/17.
 */


fun main(args: Array<String>) {
    MetalLookAndFeel.setCurrentTheme(DefaultMetalTheme())
    UIManager.setLookAndFeel(MetalLookAndFeel())


    val frame = JFrame("Hacker News")
    val screenSize = Toolkit.getDefaultToolkit().screenSize;
    frame.setSize((screenSize.width*0.6f).toInt(), (screenSize.height*0.8f).toInt())

    val contentPane = JPanel()
    contentPane.layout = BoxLayout(contentPane, BoxLayout.Y_AXIS)
    val scroller = JScrollPane(contentPane)
    scroller.verticalScrollBar.unitIncrement = 20
    frame.contentPane.add(scroller)

    frame.setLocation(screenSize.width / 2 - frame.size.width / 2, screenSize.height / 2 - frame.size.height / 2)

    frame.isVisible = true
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    val stories = getFrontPageStories()

    for (story in stories) {
        contentPane.add(getStoryPanel(story))
    }

//    val jfxPanel = JFXPanel()
//    frame.add(jfxPanel)
//
//    Platform.runLater({
//        val webView = WebView()
//        jfxPanel.scene = Scene(webView)
//        webView.engine.load("https://news.ycombinator.com/")
//    })

    frame.revalidate()
}

fun getStoryPanel(story : JsonElement) : JPanel {
    val panel = JPanel()
    panel.border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), story.asJsonObject.get("title").asString)
    panel.minimumSize = Dimension(500, 100)
    panel.preferredSize = Dimension(500, 100)

    return panel
}

fun getFrontPageStories() : ArrayList<JsonElement> {
    val storyIDs = treeFromURL("https://hacker-news.firebaseio.com/v0/topstories.json")
    val iter = storyIDs.asJsonArray.iterator()
    val stories = ArrayList<JsonElement>()

    var count : Int = 0
    while (iter.hasNext()) {
        val id = (iter.next() as JsonPrimitive).asInt

        val story = getStory(id)

        stories.add(story)
        count++

        if (count > 8)
            break
    }

    return stories
}

fun getStory(id: Int) : JsonElement {
    val story = treeFromURL("https://hacker-news.firebaseio.com/v0/item/$id.json")

    return story
}

fun treeFromURL(url: String) : JsonElement {
    val url = URL(url)
    val connection = (url.openConnection()) as HttpsURLConnection

    val reader = InputStreamReader(connection?.inputStream)

    val parser = JsonParser()
    val element = parser.parse(reader)

    reader.close()

    return element
}
