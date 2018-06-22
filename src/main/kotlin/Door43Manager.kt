import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.http.GET
import java.io.File
import java.net.URL
import java.nio.charset.Charset


class Door43Manager(private val api: Api = Api()){
    private val callResponse = api.getLanguage("")
    private val retrieved = callResponse.execute()
    private val selectedBooks: ArrayList<bookResponse> = ArrayList()

    fun getLanguages(): List<String>{
        // list to be returned
        var ret = ArrayList<String>()
        // checks if had be recieved
        if(retrieved.isSuccessful){
            for (language in retrieved.body().languages){
                ret.add(language.title)
            }
        }
        return ret
    }

    fun getBooks(language: String): List<String>?{
        val books = ArrayList<String>()
        try {
            var languageResponse: LanguageResponse? = retrieved.body().languages.find { it.title == language }
            var resource: ResourceResponse? = languageResponse!!.resources.find { it.subject == "Bible" }
            for (book in resource!!.projects) {
                selectedBooks.add(book)
                books.add(book.title)
            }
        }catch (e: NullPointerException){
            return null
        }
        return books
    }

    fun getChapters(book: String): ArrayList<String>{
        val chapters = ArrayList<String>()
        val text = getUSFM(book)!!.lines()
        var start = 1
        for(line in text){
            if (line.contains("\\c")){
                chapters.add(start.toString())
                start++
            }
        }
        return chapters
    }

    fun getUSFM(book: String): String?{
        try {
            for(format in selectedBooks.find { it.title == book }!!.formats){
                if(format.format == "text/usfm"){
                    return URL(format.url).readText()
                }
            }
        } catch (e: NullPointerException){
            return null
        }
        return null

    }

}