import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.layout.VBox
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
            prefWidth = 700.00
            prefHeight = 500.00
        }
    }

}

class TopView: View(){

    val myController = MyController()
    // A collection to hold the names of all the books of the Bible
    val books = FXCollections.observableArrayList<String>(myController.getBooks("English"))
    // string property to hold book info
    var book = SimpleStringProperty("Genesis")
    // string property to hold chapter info
    var chapter = SimpleStringProperty("1")
    // number of chapters a book has
    //var chapters = FXCollections.observableArrayList<String>(arrayListOf("0"))
    var chapters = FXCollections.observableArrayList<String>(myController.getChapters("Genesis"))
    // the available languages
    val languages = FXCollections.observableArrayList<String>(myController.getLanguages())
    // string property to hold the language info
    val language = SimpleStringProperty("English")

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
                    hbox(60) {
                        useMaxWidth = true
                        // book field
                        vbox(3) {
                            label("Book:")
                            combobox(book, books)
                        }
                        // chapter field
                        vbox(3) {
                            label("Chapter:")
                            combobox(chapter, chapters)
                        }
                        // language field
                        vbox(3) {
                            label("Language:")
                            combobox(language, languages)
                        }

                        addClass(AppStyle.wrapper)


                        // search field
                        button("Search") {
                            setPrefWidth(80.00)
                            setPrefHeight(50.00)
                            action {
                                val centerView = find(CenterView::class)
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

    // form to allow to read selection
    init {
        with(root) {
            scrollpane(true,false) {
                vbox {
                    label {
                        alignmentProperty().value = Pos.CENTER
                        textProperty().bind(book)
                        wrapTextProperty().set(true)
                        textAlignmentProperty().value = TextAlignment.CENTER

                    }
                    label {
                        alignmentProperty().value = Pos.CENTER
                        textProperty().bind(chapter)
                        wrapTextProperty().set(true)
                        textAlignmentProperty().value = TextAlignment.CENTER

                    }
                    label {
                        alignmentProperty().value = Pos.CENTER
                        textProperty().bind(bibleText)
                        wrapTextProperty().set(true)
                        textAlignmentProperty().value = TextAlignment.CENTER

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
                selection.add(it.replace("\\v", ""))
            } else if (it.contains("\\p")){
                selection.add(System.lineSeparator())
            }
        }

        return selection.joinToString()


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

