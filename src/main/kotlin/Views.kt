import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*
import java.io.File

/**
 * Alex Liu - Wycliffe Associates - 6/20/2018 - 8wocMiniChallenge
 * These classes are the views for the application
 * The master view will be embedded with the top and center views
 * The Top view is for searching through the bible
 * THe Center view is for displaying text
 */
class MasterView: View(){
    // Embeds the top view and center view
    override val root = borderpane {
        top<TopView>()
        center<CenterView>()
    }

    init{
        with(root){
            prefWidth = 800.00
            prefHeight = 600.00
        }
    }

}

class TopView: View(){

    private val myController = MyController()
    // A collection to hold the names of all the books of the Bible
    private val books = FXCollections.observableArrayList<String>(myController.getBooks("English"))
    // string property to hold book info
    private var book = SimpleStringProperty("Genesis")
    // string property to hold chapter info
    private var chapter = SimpleStringProperty("1")
    // number of chapters a book has
    //var chapters = FXCollections.observableArrayList<String>(arrayListOf("0"))
    private var chapters = FXCollections.observableArrayList<String>(myController.getChapters("Genesis"))
    // the available languages
    private val languages = FXCollections.observableArrayList<String>(myController.getLanguages())
    // string property to hold the language info
    private val language = SimpleStringProperty("English")
    private val centerView = find(CenterView::class)
    private val textSize = SimpleIntegerProperty(centerView.getFontSize().toInt())

    override val root = Form()
    // form to allow selction o
    init {
        language.addListener { obs, old, new ->
            books.clear()
            books.addAll(myController.getBooks(new))
            book.value = books[0]
        }

        // listener that finds a chapter when called
        book.addListener {  obs, old, new ->
            if(new != null) {
                chapters.clear()
                chapters.addAll(myController.getChapters(new))
                chapter.value = "1"
            }
        }

        with(root) {
            fieldset {
                vbox {
                    // displays form horizontally
                    hbox(35) {
                        vbox(0){
                            label ("USFM Reader"){

                                style {
                                    fontWeight = FontWeight.EXTRA_BOLD
                                    fontSize = 25.px
                                }
                                wrapTextProperty().set(true)
                            }
                        }
                        // book field
                        vbox(5) {
                            label("Book:")
                            combobox(book, books)
                        }
                        // chapter field
                        vbox(5) {
                            label("Chapter:")
                            combobox(chapter, chapters)
                        }
                        // language field
                        vbox(5) {
                            label("Language:")
                            combobox(language, languages)
                        }

                        addClass(AppStyle.wrapper)


                        // search field
                        button("Search") {
                            setPrefWidth(80.00)
                            setPrefHeight(50.00)
                            action {

                                if (book.value != null && chapter.value != null && language.value != null) {
                                    centerView.updateText(
                                            myController.search(book.value, chapter.value), book.value, chapter.value)
                                } else {
                                    centerView.updateText("Invalid, try again")
                                }
                            }
                        }

                        addClass(AppStyle.wrapper)
                    }

                    hbox {
                        field("Text Size") {
                            val centerView = find(CenterView::class)
                            textfield (textSize){
                                useMaxWidth = true
                            }
                        }
                        button("Change font Size"){
                            action {
                                if(textSize.value != null){
                                    centerView.updateFontSize(textSize.doubleValue())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


class CenterView: View(){
    override val root = VBox()

    var book = SimpleStringProperty()
    var chapter = SimpleStringProperty()
    var bibleText = SimpleStringProperty()
    var bibleFont = SimpleObjectProperty<Font>(Font(20.0))

    // form to allow to read selection
    init {
        with(root) {
            scrollpane(true,false) {
                vbox {
                    label {
                        alignmentProperty().value = Pos.CENTER
                        textProperty().bind(bibleText)
                        wrapTextProperty().set(true)
                        textAlignmentProperty().value = TextAlignment.CENTER
                        fontProperty().bind(bibleFont)

                    }
                    //useMaxWidth = true
                    //useMaxHeight = true
                }
                prefHeight = 1000.0
            }
            alignmentProperty().value = Pos.CENTER
            addClass(AppStyle.textWrapper)
        }

    }


    fun updateText(text: String){
        this.book.value = text
    }

    fun updateText(text: String, book: String, chapter: String){
        println(book)
        this.book.value = book
        this.chapter.value = chapter
        bibleText.value = text
    }

    fun getFontSize(): Double{
        return bibleFont.value.size
    }

    fun updateFontSize(size: Double){
        bibleFont.set(Font(size))
    }
}

class MyController: Controller()  {
    val door43Manager: Door43Manager = Door43Manager()
    fun writeToDb(inputValue: String) {
        println("Writing $inputValue to database!")
    }

    fun search(book: String, chapter: String): String{
        // makes some function call here
        val text = door43Manager.getUSFM(book)
        return parseUSFM(text!!, chapter)
    }

    fun parseUSFM(text: String, chapter: String): String{
        var lines = text.lines()
        val nextChapter = (chapter.toInt() + 1).toString()
        val selection = arrayListOf<String>()

        lines = if(lines.indexOf("\\c $nextChapter") > 0) {
            lines.subList(lines.indexOf("\\c $chapter"), lines.indexOf("\\c $nextChapter"))
        } else {
            lines.subList(lines.indexOf("\\c $chapter"), lines.size)
        }

        lines.forEach {
            if(it.contains("\\v")){
                var substr = ""
                if(it.contains("\\f")){
                    substr = it.substring(it.indexOf("\\f"), it.indexOf("\\f*") + 3)
                }
                selection.add(it.replace("\\v", "").replace(substr, ""))
            } else if (it.contains("\\p")){
                selection.add(System.lineSeparator())
            }
        }

        return selection.joinToString("")


    }
    /**
     * Function to get all the books of the Bible
     * Is pulled from books.txt in resources folder
     */
    fun getBooks(language: String): List<String>{
        return door43Manager.getBooks(language)!!

    }

    /**
     * helper function gets the number of chapters a book has
     * returns 0 as default
     */
    fun getChapters(book: String): List<String>{
        return door43Manager.getChapters(book)!!
    }

    /**
     * Temp Help function to get a list of languages
     */

    fun getLanguages(): List<String>{
        return door43Manager.getLanguages()
    }
}

