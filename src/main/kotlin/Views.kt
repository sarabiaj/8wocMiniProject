import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.layout.VBox
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

}

class TopView: View(){
    val door43Manager: Door43Manager = Door43Manager()
    val myController = MyController()
    // A collection to hold the names of all the books of the Bible
    val books = FXCollections.observableArrayList<String>(door43Manager.getBooks("English"))
    // string property to hold book info
    var book = SimpleStringProperty()
    // string property to hold chapter info
    var chapter = SimpleStringProperty()
    // number of chapters a book has
    var chapters = FXCollections.observableArrayList<String>(arrayListOf("0"))
    // the available languages
    val languages = FXCollections.observableArrayList<String>(getLanguages())
    // string property to hold the language info
    val language = SimpleStringProperty()

    override val root = Form()
    // form to allow selction o
    init {
        language.addListener { obs, old, new ->
            updateBooks(new)
        }

        // listener that finds a chapter when called
        book.addListener {  obs, old, new ->
            updateChapters(new)
        }
        with(root) {
            fieldset {
                vbox {
                    // displays form horizontally
                    hbox(20) {
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
                    }
                    hbox {
                        // search field
                        button("search") {
                            action {
                                val centerView = find(CenterView::class)
                                centerView.updateText(
                                        myController.search(book.value, chapter.value, language.value))

                            }
                        }
                        addClass(AppStyle.wrapper)
                    }
                }
            }
        }

    }

    /**
     * Function to get all the books of the Bible
     * Is pulled from books.txt in resources folder
     */
    fun updateBooks(language: String){
        books.clear()
        books.addAll(door43Manager.getBooks(language)!!)

    }

    /**
    * helper function gets the number of chapters a book has
    * returns 0 as default
    */
    private fun updateChapters(book: String){
        chapters.clear()
        chapters.addAll(door43Manager.getChapters(book))
    }

    /**
     * Temp Help function to get a list of languages
     */

    private fun getLanguages(): List<String>{
        return door43Manager.getLanguages()
    }
}

class CenterView: View(){
    override val root = VBox()

    var bibleText = SimpleStringProperty()

    // form to allow selction o
    init {
        with(root) {
            textarea() {
                textProperty().bind(bibleText)
                wrapTextProperty().set(true)
                editableProperty().set(false)
                useMaxWidth = true
            }
            addClass(AppStyle.textWrapper)

        }

    }

    fun updateText(text: String){
        bibleText.value = text
    }
}

class MyController: Controller()  {
    fun writeToDb(inputValue: String) {
        println("Writing $inputValue to database!")
    }

    fun search(book: String, chapter: String, language: String): String{
        // makes some function call here
        return "Book: $book Chapter: $chapter Language: $language"
    }
}

