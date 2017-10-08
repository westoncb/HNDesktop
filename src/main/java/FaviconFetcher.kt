import net.sf.image4j.codec.ico.ICODecoder
import org.jsoup.Jsoup
import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.Icon
import javax.swing.ImageIcon

object FaviconFetcher {
    fun getFavicon(url: String) : Image? {
        if (url != null) {

            var href = getHREFFromIndexPage(url)

            if (href == null) {
                href = getProtocolAndDomain(url) + "/favicon.ico"
            }

            if (href != null) {
                val iconUrl = iconURLFromHREF(href, url)

                val icon = iconFromURL(iconUrl)

                return icon
            }
        }

        return null
    }

    fun getHREFFromIndexPage(url: String) : String? {
        var href : String? = null

        try {
            val doc = Jsoup.connect(url).get()
            val element = doc.head().select("link[href~=.*\\.(ico|png|gif)]").first()
            if (element != null) {
                href = element.attr("href")
            }
        } catch (ex: Exception) {
            println("[FaviconFetcher] Jsoup couldn't get page: " + url)
        }

        return href
    }

    fun iconURLFromHREF(href: String, originalUrl: String) : String {
        var iconUrl = ""
        if (href.startsWith("http")) {
            iconUrl = href
        } else {
            var relPart = "/" + href
            if (href.startsWith("/"))
                relPart = href
            var path = getProtocolAndDomain(originalUrl) + relPart

            iconUrl = path
        }

        return iconUrl
    }

    fun getProtocolAndDomain(url: String) : String {
        var protocolAndDomain = URL(url).host
        if (!protocolAndDomain.startsWith("http")) {

            //Prefix http or https
            protocolAndDomain = url.substring(0, url.indexOfFirst { c -> c == '/'}) + "//" + protocolAndDomain
        }

        return protocolAndDomain
    }

    fun iconFromURL(urlString: String) : Image? {
        var image: Image? = null

        try {
            if (urlString.endsWith(".ico")) {
                val istr = URL(urlString).openStream()
                val list = ICODecoder.read(istr)
                if (!list.isEmpty())
                    image = list.get(0)
            } else {
                val url = URL(urlString)
                image = ImageIO.read(url)
            }
        } catch (ex: Exception) {
            println("[FaviconFetcher] Failed to download icon: " + urlString)
        }

        if (image != null) {
            return image
        } else {
            return null
        }
    }

    fun getScaledImage(srcImg: Image, w: Int, h: Int): Image {
        val resizedImg = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        val g2 = resizedImg.createGraphics()

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g2.drawImage(srcImg, 0, 0, w, h, null)
        g2.dispose()

        return resizedImg
    }
}