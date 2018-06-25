import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Alex Liu, Jennifer Huang - Wycliffe Associates - 6/20/2018 - 8wocMiniChallenge
 * This class holds information for the door43 api
 */
class Api(){
    private val door43Api: Door43Api

   init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.door43.org/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        door43Api = retrofit.create(Door43Api::class.java)
    }

    fun getLanguage(lang: String): Call<CatalogResponse> {
        return door43Api.getLang(lang)
    }
}

