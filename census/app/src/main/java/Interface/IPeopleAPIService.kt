package Interface

import Entity.DTOPerson
import Entity.PersonGetResponse
import Entity.PersonResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface IPeopleAPIService {
    @GET("/people")
    suspend fun getAll(): PersonGetResponse

    @GET("/people/{id}")
    suspend fun getbyId(@Path("id") personId: String): PersonGetResponse

    @Headers("Content-Type: application/json")
    @POST("/people")
    suspend fun postPerson (@Body person: DTOPerson): PersonResponse

    @Headers("Content-Type: application/json")
    @PUT("/people")
    suspend fun updatePerson (@Body person: DTOPerson): PersonResponse

    @Headers("Content-Type: application/json")
    @HTTP(method = "DELETE", path = "/people", hasBody = true)
    suspend fun deletePerson (@Body person: DTOPerson): PersonResponse
}