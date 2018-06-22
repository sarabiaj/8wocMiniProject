class CatalogResponse(val languages: List<LanguageResponse>)

class LanguageResponse(
        val title: String,
        val resources: List<ResourceResponse>
)

class ResourceResponse(
        val projects: List<bookResponse>,
        val subject: String
)

class bookResponse(
        val formats: List<formatResponse>,
        val title: String
)

class formatResponse(
        val format: String,
        val url: String
)

