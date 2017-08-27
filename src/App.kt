import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import javafx.application.Platform
import javax.swing.*
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
import java.awt.*
import java.util.*
import javax.swing.border.BevelBorder


/**
 * Created by weston on 8/26/17.
 */

val progressBar = JProgressBar(0, 30)

fun main(args: Array<String>) {
//    MetalLookAndFeel.setCurrentTheme(DefaultMetalTheme())
//    UIManager.setLookAndFeel(MetalLookAndFeel())
    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");


    val frame = JFrame("Hacker News")
    val screenSize = Toolkit.getDefaultToolkit().screenSize;
    frame.setSize((screenSize.width*0.6f).toInt(), (screenSize.height*0.8f).toInt())

    val contentPane = JPanel()
    contentPane.layout = BoxLayout(contentPane, BoxLayout.Y_AXIS)

    progressBar.preferredSize = Dimension(300, 100)
    progressBar.minimumSize = Dimension(300, 100)
    progressBar.maximumSize = Dimension(300, 100)
    progressBar.value = 0
//    progressBar.isStringPainted = true
    contentPane.add(progressBar)

    val scroller = JScrollPane(contentPane)
    scroller.verticalScrollBar.unitIncrement = 16
    frame.contentPane.add(scroller)

    frame.setLocation(screenSize.width / 2 - frame.size.width / 2, screenSize.height / 2 - frame.size.height / 2)

    frame.isVisible = true
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    val stories = getFrontPageStories()

    contentPane.remove(progressBar)

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

fun getStoryPanel(story : JsonObject) : JPanel {
    val title = story.get("title").asString
    val points = story.get("score").asInt
    val time = Date(story.get("time").asLong * 1000).toString()
    val kids = story.get("kids")?.asJsonArray
    val numKids = kids?.size()
    val user = story.get("by").asString

    val panel = JPanel(GridBagLayout())
    val titleBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title)
    panel.border = BorderFactory.createCompoundBorder(titleBorder, BorderFactory.createEmptyBorder(4, 4, 4, 4))

    val titleButton = JButton("Go")
    val discussButton = JButton("Discuss ($numKids)")
    val pointsLabel = JLabel("$points points")
    val userButton = JButton(" by $user")
    val timeLabel = JLabel("$time")

    val c = GridBagConstraints()
    c.anchor = GridBagConstraints.FIRST_LINE_START
    c.fill = GridBagConstraints.HORIZONTAL

    c.gridx = 0
    c.gridy = 0
    panel.add(titleButton, c)
    c.gridx = 1
    panel.add(discussButton, c)
    c.gridx = 2
    panel.add(userButton, c)
    c.gridy = 1
    c.gridx = 0
    c.anchor = GridBagConstraints.LAST_LINE_START
    panel.add(pointsLabel, c)
    c.gridx = 1
    c.anchor = GridBagConstraints.LAST_LINE_END
    panel.add(timeLabel, c)

    return panel
}

fun getFrontPageStories() : ArrayList<JsonObject> {
    val storyIDs = treeFromURL("https://hacker-news.firebaseio.com/v0/topstories.json")
    val iter = storyIDs.asJsonArray.iterator()
    val stories = ArrayList<JsonObject>()

    var count : Int = 0
    while (iter.hasNext()) {
        val id = (iter.next() as JsonPrimitive).asInt

        val story = getStory(id)

        stories.add(story.asJsonObject)
        count++
        progressBar.value = count

        if (count > 29)
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
