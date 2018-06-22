import javafx.scene.text.FontWeight
import tornadofx.*
import java.awt.Color


class AppStyle: Stylesheet(){
    companion object {
        // wrapper used for fields
        val wrapper by cssclass()
        val textWrapper by cssclass()
        val title by cssclass()
    }

    init {

        wrapper {
            // adds padding to fields
            padding = box(10.px)

        }

        textWrapper{
            backgroundColor += c("#d49942")
            padding = box(10.px)
            wrapText = true

        }
    }



}