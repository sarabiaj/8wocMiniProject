import retrofit2.*
class Api(){
    private val door43Api: Door43Api

i   init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.door43.org/v3/catalog.json")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        door43Api = retrofit.create(door43Api::class.java)
    }

    fun getLanguage(lang: String): Call<CatalogResponse> {
        return door43Api.getLang(lang)
    }
}

fun main(args: Array<String>){
    println("a")
}