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
    // A collection to hold the names of all the books of the Bible
    val books = FXCollections.observableArrayList<String>(getBooks())
    // string property to hold book info
    val book = SimpleStringProperty()
    // string property to hold chapter info
    val chapter = SimpleStringProperty()
    // string property to hold the language info
    val language = SimpleStringProperty()

    override val root = Form()
    // form to allow selction o
    init {
        with(root) {
            fieldset {
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
                        textfield(chapter)
                    }
                    // language field
                    vbox(5) {
                        label("Language:")
                        textfield(language)
                    }
                    // search field
                    button("search") {
                        action {
                            var centerView = find(CenterView::class)
                            centerView.updateText(myController.search(book.value, chapter.value, language.value)) }
                    }
                    addClass(AppStyle.wrapper)
                }
            }
        }

    }

    /**
     * Function to get all the books of the Bible
     * Is pulled from books.txt in resources folder
     */
    fun getBooks(): ArrayList<String>{
        // the arraylist to be returned
        val ret = ArrayList<String>()
        // keeps track of the current line
        var i = 0
        // goes through line by line adding books
        File(System.getProperty("user.dir") + "/resources/books.txt").forEachLine {
            // if i is a multiple of 4 then it is a a book name and should be added
            if (i % 2 == 0) {
                ret.add(it)
            }
            i++
        }
        return ret
    }

    /**
     * This function gets the selection and displays it
     */
    fun getSelection(){
        TODO()
    }
}

class CenterView: View(){
    override val root = VBox()

    var bibleText = SimpleStringProperty()

    // form to allow selection
    init {
        with(root) {
            textarea {
                textProperty().bind(bibleText)

    // form to allow selction o
    init {
        with(root) {
            val tmpFile = File(System.getProperty("user.dir") + "/resources/filler.txt")
            textarea(tmpFile.readText()) {

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

