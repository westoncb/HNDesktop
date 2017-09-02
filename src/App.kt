import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.web.WebView
import javax.swing.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import java.io.InputStreamReader
import javax.swing.UIManager
import java.awt.*
import java.util.*
import javax.swing.event.ListSelectionEvent
import javax.swing.plaf.metal.DefaultMetalTheme
import javax.swing.plaf.metal.MetalLookAndFeel


/**
 * Created by weston on 8/26/17.
 */

class App {
    companion object {
        init {
            MetalLookAndFeel.setCurrentTheme(DefaultMetalTheme())
            UIManager.setLookAndFeel(MetalLookAndFeel())
//            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel")
        }
    }

    val progressBar = JProgressBar(0, 30)
    var contentPane = JPanel()
    var storyControlPanel: JPanel = JPanel()
    var storyPanel: JPanel = JPanel()

    val listModel = DefaultListModel<Story>()
    val storyList = JList(listModel)

    val userLabel = JLabel()
    val pointsLabel = JLabel()
    val storyTimeLabel = JLabel()
    val commentCountLabel = JLabel()
    val contentToggleButton = JButton("View Page")

    var commentTree = JTree()

    val jfxPanel = JFXPanel()

    init {

        val frame = JFrame("Hacker News")
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val frameSize = Dimension((screenSize.width*0.90f).toInt(), (screenSize.height*0.85f).toInt())
        frame.size = frameSize

        frame.contentPane = contentPane
        contentPane.preferredSize = frameSize

        contentPane.layout = GridBagLayout()
        val c = GridBagConstraints()
        c.anchor = GridBagConstraints.CENTER

        progressBar.preferredSize = Dimension(400, 25)
        progressBar.isStringPainted = true

        //Just add so the display updates. Not sure why, but
        //it delays showing the progress bar if we don't do this.
        contentPane.add(JLabel(""), c)

        c.gridy = 1
        contentPane.add(progressBar, c)
        progressBar.value = 0

        frame.setLocation(screenSize.width / 2 - frame.size.width / 2, screenSize.height / 2 - frame.size.height / 2)
        frame.pack()

        frame.isVisible = true
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        val stories = getFrontPageStories()

        contentPane = JPanel(BorderLayout())
        frame.contentPane = contentPane

        val mainPanel = getMainPanel(stories)
        contentPane.add(mainPanel)

        storyList.selectedIndex = 0

        frame.revalidate()
    }

    fun getMainPanel(storyNodes: ArrayList<JsonObject>) : JComponent {
        val stories = storyNodes
                .filter { it.get("type").asString.equals("story", true) }
                .map { Story(it) }

        val def = DefaultListCellRenderer()
        val renderer = ListCellRenderer<Story> { list, value, index, isSelected, cellHasFocus ->
            def.getListCellRendererComponent(list, value.title, index, isSelected, cellHasFocus)
        }

        storyList.cellRenderer = renderer
        storyList.addListSelectionListener { e: ListSelectionEvent? ->
            if (e != null) {
                if (!e.valueIsAdjusting) {
                    changeActiveStory(stories[storyList.selectedIndex])
                }
            }
        }

        for (story in stories) {
            listModel.addElement(story)
        }

        val mainRightPane = buildMainRightPanel()


        mainRightPane.add(jfxPanel)

        val storyScroller = JScrollPane(storyList)
        storyScroller.verticalScrollBar.unitIncrement = 16

        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, storyScroller, mainRightPane)

        return splitPane
    }

    fun changeActiveStory(story: Story) {
        userLabel.text = "Posted by: ${story.user}"
        pointsLabel.text = "Points: ${story.points}"
        storyTimeLabel.text = "Time: ${story.time}"
        commentCountLabel.text = "Comments: ${story.totalComments}"

//        Platform.runLater({
//            val webView = WebView()
//            jfxPanel.scene = Scene(webView)
//            webView.engine.load(story.url)
//        })

        storyPanel.add(getCommentsPanel())
        loadComments(story)
    }

    fun loadComments(story: Story) {

    }

    fun commentArrived(comment: JsonObject) {
        //construct comment object
        //update comment count
        //use associated address to add node to tree model
    }

    fun getCommentsPanel() : JPanel {
        val panel = JPanel()
        commentTree = JTree()
        panel.add(commentTree)

        //set up custom cell renderer

        return panel
    }

    fun buildMainRightPanel() : JPanel {
        val root = JPanel()
        root.layout = BorderLayout()

        storyControlPanel = JPanel(GridLayout(1, 2))
        storyControlPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)

        val leftPanel = JPanel()
        val rightPanel = JPanel(BorderLayout())

        storyControlPanel.add(leftPanel)
        storyControlPanel.add(rightPanel)

        leftPanel.layout = BoxLayout(leftPanel, BoxLayout.Y_AXIS)
        leftPanel.add(pointsLabel)
        leftPanel.add(commentCountLabel)
        leftPanel.add(userLabel)
        leftPanel.add(storyTimeLabel)

        rightPanel.add(contentToggleButton, BorderLayout.EAST)

        storyPanel = JPanel()
        storyPanel.add(JLabel(""))

        root.add(storyControlPanel, BorderLayout.NORTH)

        root.add(storyPanel, BorderLayout.CENTER)

        return root
    }

    //For testing—doesn't download anything
    fun getFrontPageStories2() : ArrayList<JsonObject> {
        val stories = ArrayList<JsonObject>()

        for (i in 0..30) {
            stories.add(getStory2(i).asJsonObject)
            progressBar.value = i
        }

        return stories
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

    //For testing—doesn't download anything
    fun getStory2(id: Int) : JsonElement {
        val story = treeFromURL2("https://hacker-news.firebaseio.com/v0/item/$id.json")

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

    //For testing—doesn't download anything
    fun treeFromURL2(url: String) : JsonElement {


        val parser = JsonParser()
        val element = parser.parse("{\"by\":\"madmork\",\"descendants\":39,\"id\":15111862,\"kids\":[15112163,15112064,15112298,15112289,15112028,15112075,15112092,15112065,15112050,15111894,15111981,15112003,15112149,15112150,15112282],\"score\":62,\"time\":1503857171,\"title\":\"Jumping Ship: Signs It's Time to Quit\",\"type\":\"story\",\"url\":\"https://www.madmork.com/single-post/2017/08/25/Jumping-Ship-7-Signs-its-time-to-quit\"}")


        return element
    }
}

fun main(args: Array<String>) {
    App()
}
