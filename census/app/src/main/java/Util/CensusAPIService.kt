package Util

import Interface.IPeopleAPIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CensusAPIService {
    // Example Base URL
    private const val BASE_URL = "https://utn-movil-survey-api-cbddgsbqh6aba5cr.eastus-01.azurewebsites.net"

    val apiPeople: IPeopleAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IPeopleAPIService::class.java)
    }
}