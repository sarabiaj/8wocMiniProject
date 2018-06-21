class CatalogResponse(val data: Info)

class Info(
        val languages: List<SubLang>,
        val source: List<SubSrce>
)

class SubLang(
        val identifier: String, //version
        val projects: List<SubProj>
)

class SubProj(
        val formats: List<SubForm>,
        val identifier: String,//book
        val sort: Int //book number
)

class SubForm (
     val url: String //usfm file
)

class SubSrce (
        val lang: String //language
)