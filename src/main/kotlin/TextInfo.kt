class CatalogResponse(val data: Catalogs)

class Catalogs(
        class src(
            val language: String
        ),
        val identifier: String,
        class projects(
                val url: String,
                val identifier: String,
                val sort: Int
        ),
        val body: String
)