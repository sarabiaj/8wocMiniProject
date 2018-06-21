import javafx.beans.Observable
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
    val myController = MyController()
    // A collection to hold the names of all the books of the Bible
    val books = FXCollections.observableArrayList<String>(getBooks())
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
    // form to allow selection
    init {
        // listener that finds a chapter when called
        book.addListener {  obs, old, new ->
            // resets chapters
            chapters.clear()
            // for each chapter adds a number to chapters
            for (j in 1..getChapters(new.toString())){
                chapters.add(j.toString())
            }
            println(chapters.size)
        }
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
                        combobox(chapter, chapters)
                    }
                    // language field
                    vbox(5) {
                        label("Language:")
                        combobox(language, languages)
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
     * Temp helper Function to get all the books of the Bible
     * Is pulled from books.txt in resources folder
     */
    private fun getBooks(): ArrayList<String>{
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
     * Temp helper function gets the number of chapters a book has
     * returns 0 as default
     */
    private fun getChapters(book: String): Int{
        // whether or not the book has been found
        var found = false
        // the number of chapters
        var num = 0

            // path to books of the Bible
        File(System.getProperty("user.dir") + "/resources/books.txt").forEachLine {
            // if the book is found then the next line is the number of chapters
            if (it == book) {
                found = true
            } else if (found) {
                num = it.toInt()
                found = false
            }
        }

        return num
    }

    /**
     * Temp Help function to get a list of languages
     */

    private fun getLanguages(): List<String>{
        return File(System.getProperty("user.dir") + "/resources/Languages.txt").readLines()
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
    // Test function to see if data is being pulled correctly
    fun Test(book: String, chapter: String, language: String) {
        println(book)
        println(chapter)
        println(language)
    }

    fun search(book: String, chapter: String, language: String): String{
        // makes some function call here
        return "Book: $book Chapter: $chapter Language: $language"
    }
}

