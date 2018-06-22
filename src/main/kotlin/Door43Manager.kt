import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.http.GET
import java.io.File
import java.net.URL
import java.nio.charset.Charset


/**
 * Alex Liu - Wycliffe Associates - 6/20/2018 - 8wocMiniChallenge
 * this class manages connection with Door43 api
 * the functions here take care of accessing all of the json data
 */
class Door43Manager(private val api: Api = Api()){
    private val callResponse = api.getLanguage("") // gets the main call response
    private val retrieved = callResponse.execute() // executes the call response
    private val selectedBooks: ArrayList<bookResponse> = ArrayList() // and arraylist of all the bookResponses

    /**
     * this function gets the number of languages in the api
     */
    fun getLanguages(): List<String>{
        // list to be returned
        var ret = ArrayList<String>()
        // checks if had be recieved
        if(retrieved.isSuccessful){
            for (language in retrieved.body().languages){
                if (getBooks(language.title) != null) {
                    ret.add(language.title)
                }
            }
        }
        ret.sort()
        return ret
    }

    /**
     * This function gets the books from the door43 api given a language
     * returns as a list of strings
     */
    fun getBooks(language: String): List<String>?{
        // books to be returned
        val books = ArrayList<String>()
        try {
            // finds the correct resource
            var languageResponse: LanguageResponse? = retrieved.body().languages.find { it.title == language }
            var resource: ResourceResponse? = languageResponse!!.resources.find { it.subject == "Bible" }
            // searches each book in the resource
            for (book in resource!!.projects) {
                if (book.formats.find { it.format == "text/usfm" } != null) {
                    // adds to the selected books and the return
                    selectedBooks.add(book)
                    books.add(book.title)
                }
            }
        }catch (e: NullPointerException){
            // in case of null pointer exception returns null
            // TODO dont return null notify user instead
            return null
        }
        return books
    }

    /**
     * this function gets the number of chapters in a given book
     */
    fun getChapters(book: String): ArrayList<String>{
        // chapters that will be returned
        val chapters = ArrayList<String>()
        // the text in the USFM file
        val text = getUSFM(book)!!.lines()
        // start number of chapters
        var numChapters = 1
        // goes through lines
        for(line in text){
            // if line contains a \c then it is a chapter
            if (line.contains("\\c")){
                // adds chapter number to the string
                chapters.add(numChapters.toString())
                numChapters++
            }
        }
        return chapters
    }

    /**
     * gets the USFM file given a book
     * selected books must already have data
     */
    fun getUSFM(book: String): String?{
        try {
            // searches through the formats of the selected books
            for(format in selectedBooks.find { it.title == book }!!.formats){
                // if the format is of type USFM then return
                if(format.format == "text/usfm"){
                    return URL(format.url).readText()
                }
            }
        } catch (e: NullPointerException){
            // todo fix
            return null
        }
        return null

    }

}