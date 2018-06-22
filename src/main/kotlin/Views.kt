import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.stage.Screen
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
            val primaryScreenBounds = Screen.getPrimary().getVisualBounds()
            prefWidth = primaryScreenBounds.getWidth()/1.6
            prefHeight = primaryScreenBounds.getHeight()/1.5
        }
    }

}

class TopView: View(){
    // the controller for the application
    private val myController = MyController()
    // A collection to hold the names of all the books of the Bible
    private val books = FXCollections.observableArrayList<String>(myController.getBooks("English"))
    // string property to hold book info
    private var book = SimpleStringProperty("Genesis")
    //var chapters = FXCollections.observableArrayList<String>(arrayListOf("0"))
    private var chapters = FXCollections.observableArrayList<String>(myController.getChapters("Genesis"))
    // string property to hold chapter info
    private var chapter = SimpleStringProperty("1")
    // the available languages
    private val languages = FXCollections.observableArrayList<String>(myController.getLanguages())
    // string property to hold the language info
    private val language = SimpleStringProperty("English")
    // the centerview in the application
    private val centerView = find(CenterView::class)
    // the size of the text
    private val textSize = SimpleIntegerProperty(centerView.getFontSize().toInt())

    override val root = Form()
    // form to allow user to make a selection
    init {
        // adds a listener to update books to be in selected language
        language.addListener { obs, old, new ->
            // empties books and adds new ones
            books.clear()
            books.addAll(myController.getBooks(new))
            book.value = books[0]
        }

        // listener that finds a chapter when called
        book.addListener {  obs, old, new ->
            if(new != null) {
                // clears chapters and adds new ones
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
                            setPrefWidth(90.00)
                            setPrefHeight(50.00)
                            style{
                                fontSize = 16.px
                                backgroundColor += c("#d49942")
                                borderWidth += box(3.px)
                            }
                            action {
                                // checks if the book chapter and language have values the update the text
                                if (book.value != null && chapter.value != null && language.value != null) {
                                    centerView.updateText(
                                            myController.search(book.value, chapter.value))
                                } else {
                                    // else notify user
                                    centerView.updateText("Invalid, try again")
                                }
                            }
                        }

                        addClass(AppStyle.wrapper)
                    }

                    hbox {
                        // field to change the text size
                        field("Text Size") {
                            textfield (textSize)
                        }
                        // field for a button to change text size
                        button("Change font Size"){
                            // when pressed updates font size
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
    // the chapter text
    var bibleText = SimpleStringProperty()
    // the font text
    var bibleFont = SimpleObjectProperty<Font>(Font(15.0))

    // form to allow to read selection
    init {
        with(root) {
            scrollpane(true,false) {
                vbox {
                    // a label the contains the bible text
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

    /**
     * Function that updates the text given new text
     */
    fun updateText(text: String){
        bibleText.value = text
    }

    /**
     * gets the current font size
     */
    fun getFontSize(): Double{
        return bibleFont.value.size
    }

    /**
     * updates the current font size
     */
    fun updateFontSize(size: Double){
        bibleFont.set(Font(size))
    }
}

class MyController: Controller()  {
    val door43Manager: Door43Manager = Door43Manager()
    fun writeToDb(inputValue: String) {
        println("Writing $inputValue to database!")
    }

    /**
     * Searches for the USFM file given a book and chapter
     */
    fun search(book: String, chapter: String): String{
        // makes some function call here
        val text = door43Manager.getUSFM(book)
        return parseUSFM(text!!, chapter)
    }

    /**
     * function that parses the USFM given text and a chapter
     */
    fun parseUSFM(text: String, chapter: String): String{
        // a list of lines in the text
        var lines = ArrayList<String>()
        // the next chapter after the one being searched
        val nextChapter = (chapter.toInt() + 1).toString()
        // the list that conatins the text to be returned
        val selection = arrayListOf<String>()
        text.lines().forEach {
            lines.add(it.trim())
        }

        // gets a sublist up to the next chapter or end of file
        lines = if (lines.indexOf("\\c $nextChapter") > 0) {
            ArrayList(lines.subList(lines.indexOf("\\c $chapter"), lines.indexOf("\\c $nextChapter")))
        } else {
            ArrayList(lines.subList(lines.indexOf("\\c $chapter"), lines.size))
        }


        // looks through each line adding verses
        lines.forEach {
            // checks if line contains a verse
            if(it.contains("\\v")){
                // substring for footmarks
                var substr = ""
                // if found sets substring eqaul to the footmark
                if(it.contains("\\f")){
                    substr = it.substring(it.indexOf("\\f"), it.indexOf("\\f*") + 3)
                }
                // replace the \v and the footmarks
                selection.add(it.replace("\\v", "").replace(substr, ""))
            } else if (it.contains("\\p")){
                // if contains a \p means end of paragraph
                selection.add(System.lineSeparator())
            }
        }

        // returns the full selection
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

