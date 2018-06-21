import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory

class Api(){
    private val door43Api: Door43Api

   init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.door43.org/v3/catalog.json/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        door43Api = retrofit.create(Door43Api::class.java)
    }

    fun getLanguage(lang: String): Call<CatalogResponse> {
        return door43Api.getLang(lang)
    }
}

