import tornadofx.*

class Door43Manager(private val api: Api = Api()){
    fun getLanguage(){
        val language = api.getLanguage("")
        val retrieved = language.execute()

        if(retrieved.isSuccessful){
            println("a")
        }
    }
}

fun main(args: Array<String>){
    val door43 : Door43Manager = Door43Manager()
    val retrieved = door43.getLanguage()
    launch<USFMReader>(args)
}