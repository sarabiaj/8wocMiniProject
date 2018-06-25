import javafx.scene.text.FontWeight
import tornadofx.*
import java.awt.Color

/**
 * Alex Liu, Jennifer Huang - Wycliffe Associates - 6/20/2018 - 8wocMiniChallenge
 * stylesheet for the app
 */
class AppStyle: Stylesheet(){
    companion object {
        // wrapper used for fields
        val wrapper by cssclass()
        // wrapper used for text
        val textWrapper by cssclass()
    }

    init {

        wrapper {
            // adds padding to fields
            padding = box(20.px)

        }

        textWrapper{
            backgroundColor += c("#d49942")
            padding = box(15.px)
            wrapText = true

        }
    }



}