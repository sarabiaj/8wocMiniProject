import tornadofx.*
import java.awt.Color


class AppStyle: Stylesheet(){
    companion object {
        // wrapper used for fields
        val wrapper by cssclass()
        val textWrapper by cssclass()
    }

    init {
        wrapper {
            // adds padding to fields
            padding = box(10.px)
            prefWidth = 700.px
            prefHeight = 20.px
        }

        textWrapper{
            backgroundColor += c("#d49942")
            padding = box(10.px)
            wrapText = true
            prefWidth = 700.px
            prefHeight = 300.px
        }
    }



}