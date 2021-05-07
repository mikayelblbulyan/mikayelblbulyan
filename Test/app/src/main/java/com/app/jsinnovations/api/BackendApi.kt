package com.app.jsinnovations.api

import com.app.jsinnovations.models.*
import com.app.jsinnovations.sharing.Constant
import com.app.jsinnovations.utils.ProgressRequestBody
import com.app.jsinnovations.utils.Utils
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.realm.Realm
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class BackendApi {

    companion object{
        private var backendApi: BackendApi? = null
        private var backendApiInterface: BackendApiInterface? = null

        fun getInstance(): BackendApi {
            if (backendApi == null) {
                backendApi = BackendApi()
                init()
            }

            return backendApi as BackendApi
        }

        private fun init() {
            val loggingInterceptor = HttpLoggingInterceptor()
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val client = OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            client.newBuilder().addInterceptor(loggingInterceptor)
            val retrofit = Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            backendApiInterface = retrofit.create(BackendApiInterface::class.java)
        }

    }

    fun addBoard(accessToken: String){
        val hashMap = HashMap<String, String>()
        hashMap["name"] = "a"
        hashMap["user"] = "aaa"
        hashMap["nominal"] = "bbb"
        hashMap["modulesCount"] = "5"
        val call = backendApiInterface?.addBoard("Bearer $accessToken",hashMap)
        call?.enqueue(object: Callback<ResponseBody>{

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()
                println()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println()
            }

        })
    }

    fun login(accessToken: String, email: String, password: String, interference: () -> Unit){
        val hashMap = HashMap<String, String>()
        hashMap["email"] = "lostest@mail.com"
        hashMap["password"] = Utils.encryptThisString("123456")
        val call = backendApiInterface?.login("Bearer $accessToken", hashMap)
        call?.enqueue(object: Callback<ResponseBody>{

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()
                println()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println()
            }

        })
    }

    fun register(phoneNumber: String, password: String){
        val hashMap = HashMap<String, String>()
        hashMap["phoneNumber"] = "+37498888888"
        hashMap["password"] = "123456"
        val call = backendApiInterface?.register(hashMap)
        call?.enqueue(object: Callback<ResponseBody>{

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                println()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println()
            }

        })
    }

    fun getImage(accessToken: String, width: Int, height: Int){
        val hashMap = HashMap<String, Int>()
        hashMap["width"] = width
        hashMap["height"] = height
        val call = backendApiInterface?.getImage("Bearer $accessToken")
        call?.enqueue(object : Callback<ResponseBody>{

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()
                println()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println()
            }
        })
    }

    fun auth(email: String, password: String, interference: (String?) -> Unit){
        val hashMap = HashMap<String, String>()
        hashMap["email"] = "Android@test.com"
        hashMap["password"] = "123456"
        val call = backendApiInterface?.auth(hashMap)
        call?.enqueue(object: Callback<JsonObject>{

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful){
                    val accessToken = response.body()?.getAsJsonPrimitive("accessToken")?.asString
                    interference(accessToken)
                }
                else
                    interference(null)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println()
            }

        })
    }

    fun images(imageFile: File, accessToken: String, requestBody: ProgressRequestBody){
        val part = MultipartBody.Part.createFormData("file", imageFile.name, requestBody)
        val call = backendApiInterface?.images(part, accessToken)
        call?.enqueue(object: Callback<ResponseBody>{

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                println()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println()
            }

        } )
    }

    fun allBoards(accessToken: String, interference: () -> Unit){
        val call = backendApiInterface?.allBoards("3", "1", "Bearer $accessToken")
        call?.enqueue(object: Callback<JsonObject>{

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val responseBody = response.body()
                println()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println()
            }

        })
    }

    fun getBoards(accessToken: String, placeId: String, interference: (ArrayList<Board>?) -> Unit){
        val call = backendApiInterface?.getBoards(placeId, "Bearer $accessToken")
        call?.enqueue(object: Callback<ArrayList<Board>>{

            override fun onResponse(call: Call<ArrayList<Board>>, response: Response<ArrayList<Board>>) {
                val boards = response.body()
                interference(boards!!)
            }

            override fun onFailure(call: Call<ArrayList<Board>>, t: Throwable) {
                println()
            }

        })
    }

    fun getBoardsByDate(accessToken: String, placeId: String, startDate: String, endDate: String, interference: (ArrayList<Board>?) -> Unit){
        val call = backendApiInterface?.getBoardsByDate(placeId,
            startDate, endDate, TimeZone.getDefault(),"Bearer $accessToken")
        call?.enqueue(object: Callback<ArrayList<Board>>{

            override fun onResponse(call: Call<ArrayList<Board>>, response: Response<ArrayList<Board>>) {
                val boards = response.body()
                interference(boards)
            }

            override fun onFailure(call: Call<ArrayList<Board>>, t: Throwable) {
                println()
            }

        })
    }

    fun getBoard() {
        val realm = Realm.getDefaultInstance()
        val user = realm.where(User::class.java).findFirst()!!
        val call = backendApiInterface?.getBoard(user._id!!)
        call?.enqueue(object: Callback<JsonObject>{

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val responseBody = response.body()
                println()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println()
            }

        })
    }

    fun settings(boardId: String, moduleId: String, accessToken: String, response: (ArrayList<Setting>) -> Unit){
        val call = backendApiInterface?.settings(boardId, moduleId, "Bearer $accessToken")
        call?.enqueue(object: Callback<JsonObject>{

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val jsonObject = response.body()?.asJsonObject?.get("settings")
                val settings = ArrayList<Setting>()
                for(i in 1..6) {
                    val settingJsonObject = jsonObject?.asJsonObject?.get(i.toString())?.asJsonObject
                    val value = settingJsonObject?.get("value")?.asInt
                    val mid = settingJsonObject?.get("mid")?.asInt
                    val nameJsonObject = settingJsonObject?.get("name")?.asJsonObject
                    val en = nameJsonObject?.get("en")?.asString
                    val hy = nameJsonObject?.get("hy")?.asString
                    val ru = nameJsonObject?.get("ru")?.asString
                    val name = Name()
                    name.en = en
                    name.hy = hy
                    name.ru = ru
                    val setting = Setting()
                    setting.value = value
                    setting.mid = mid
                    setting.name = name
                    settings.add(setting)
                }
                response(settings)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println()
            }

        })
    }

    fun addUser(accessToken: String,name: String, surname: String, username: String, email: String, password: String){
        val hashMap = HashMap<String, String>()
        hashMap["name"] = name
        hashMap["surname"] = surname
        hashMap["username"] = username
        hashMap["email"] = email
        hashMap["password"] = password
        val call = backendApiInterface?.addUser("Bearer $accessToken", hashMap)
        call?.enqueue(object: Callback<ResponseBody>{

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()
                println()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println()
            }

        })
    }

    fun getUser(accessToken: String, inference: (User) -> Unit){
        val hashMap = HashMap<String, String>()
        val accessToken1 = "Bearer $accessToken"
        hashMap["Authorization"] = accessToken1
        val call = backendApiInterface?.getUser(accessToken1)
        call?.enqueue(object: Callback<User>{

            override fun onResponse(call: Call<User>, response: Response<User>) {
                val responseBody = response.body()
                val realm = Realm.getDefaultInstance()
                if(responseBody != null)
                    realm.executeTransaction {
                        it.copyToRealmOrUpdate(responseBody)
                        val user = realm.where(User::class.java).findFirst()!!
                        inference(user)
                    }

            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                println()
            }

        })
    }

    fun getPlaces(accessToken: String, onResponse: (ArrayList<Place>?) -> Unit){
        val call = backendApiInterface?.getPlaces("Bearer $accessToken")
        call?.enqueue(object: Callback<ArrayList<Place>>{

            override fun onResponse(call: Call<ArrayList<Place>>, response: Response<ArrayList<Place>>) {
                if(response.isSuccessful){
                    val places = response.body()
                    val realm = Realm.getDefaultInstance()
                    realm.executeTransaction {
                        realm.copyToRealmOrUpdate(places!!)
                    }
                    onResponse(places!!)
                }
            }

            override fun onFailure(call: Call<ArrayList<Place>>, t: Throwable) {
                println()
            }

        })
    }

    fun getModules(accessToken: String, boardId: String, response: (ArrayList<Module>, Total) -> Unit){
        val call = backendApiInterface?.getModules(boardId, "Bearer $accessToken")
        call?.enqueue(object: Callback<JsonObject>{

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val totalJsonObject = response.body()?.get("total")?.asJsonObject
                val total = Total()
                total.electricity = totalJsonObject?.get("electricity")!!.asInt
                total.liquid = totalJsonObject.get("liquid")!!.asInt
                total.minutes = totalJsonObject.get("minutes")!!.asInt
                total.water = totalJsonObject.get("water").asInt
                val modulesJsonArray = response.body()?.get("modules")?.asJsonArray
                val modules = ArrayList<Module>()
                for(moduleJsonObject in modulesJsonArray!!){
                    val module = Module()
                    module._id = moduleJsonObject.asJsonObject.get("_id")!!.asString
                    module.editable = moduleJsonObject.asJsonObject.get("editable").asBoolean
                    module.active = moduleJsonObject.asJsonObject.get("active").asBoolean
                    module.name = moduleJsonObject.asJsonObject.get("name").asString
                    module.time = moduleJsonObject.asJsonObject.get("time").asInt
                    module.nominal = moduleJsonObject.asJsonObject.get("nominal").asInt
                    module.waterValue = moduleJsonObject.asJsonObject.get("waterValue").asInt
                    module.electricityValue = moduleJsonObject.asJsonObject.get("electricityValue").asInt
                    module.liquidValue = moduleJsonObject.asJsonObject.get("liquidValue").asInt
                    module.boardId = boardId
                    modules.add(module)
                }
                response(modules, total)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println()
            }

        })
    }



    fun getPlace(accessToken: String, placeId: String){
        val hashMap = HashMap<String, String>()
        hashMap["Authentication"] = "Bearer $accessToken"
        val call = backendApiInterface?.getPlace(placeId,"Bearer $accessToken")
        call?.enqueue(object: Callback<ResponseBody>{

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()
                println()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println()
            }

        })
    }

    fun addPlace(accessToken: String, userId: String){
        val hashMap = HashMap<String, String>()
        hashMap["name"] = "a b"
        hashMap["userId"] = userId
        val call = backendApiInterface?.addPlace("Bearer $accessToken",hashMap)
        call?.enqueue(object: Callback<ResponseBody>{

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()
                println()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println()
            }

        })
    }
}