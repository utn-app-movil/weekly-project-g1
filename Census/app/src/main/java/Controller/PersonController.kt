package Controller

import Entity.DTOPerson
import Entity.Person
import Util.CensusAPIService
import Util.Util
import android.content.Context
import android.util.Log
import cr.ac.utn.census.R
import java.time.LocalDate

class PersonController {
    private  var context: Context

    constructor(context: Context){
        this.context=context
    }

    suspend fun addPerson(person: Person){
        try {
            val response = CensusAPIService.apiPeople.postPerson(convertToDTOPersonObject(person))
            if (response.responseCode != 200)
                throw Exception(response.message)

        }catch (e: Exception){
            Log.e("API_Call", "Error fetching data: ${e.message}")
            throw Exception(context
                .getString(R.string.ErrorMsgAdd))

        }
    }

    suspend fun updatePerson(person: Person){
        try {
            val response = CensusAPIService.apiPeople.updatePerson(convertToDTOPersonObject(person))
            if (response.responseCode != 200)
                throw Exception(response.message)
        }catch (e: Exception){
            throw Exception(context
                .getString(R.string.ErrorMsgUpdate))

        }
    }

    suspend fun getPeople(): List<Person>{
        var people = mutableListOf<Person>()
        try {
            val response = CensusAPIService.apiPeople.getAll()
            if (response.responseCode != 200)
                throw Exception(response.message)

            response.data.forEach { item ->
                people.add(getPersonObject(item))
            }
            Log.d("API_Call", "Success: ${response.data}")
        } catch (e: Exception) {
            // Handle error
            Log.e("API_Call", "Error fetching data: ${e.message}")
            throw Exception(e.message)
        }
        return people
    }

    suspend fun getById(id: String): Person?{
        var person: Person?
        try {
            person = null
            val response = CensusAPIService.apiPeople.getbyId(id)
            if (response.responseCode != 200)
                return person

            Log.d("API_Call", "Success: ${response.data}")
            if (response.data.any()){
                person = getPersonObject(response.data[0])
            }else{
                throw Exception(context
                    .getString(R.string.ErrorMsgGetById))
            }
        }catch (e: Exception){
            Log.e("API_Call", "Error fetching data: ${e.message}")
            throw Exception(context
                .getString(R.string.ErrorMsgGetById))
        }
        return person
    }

    suspend fun removePerson(id: String){
        try{
            val dtoPerson = DTOPerson(id)
            val response = CensusAPIService.apiPeople.deletePerson(dtoPerson)
            if (response.responseCode != 200)
                throw Exception(response.message)
        }catch (e: Exception){
            Log.e("API_Call", "Error fetching data: ${e.message}")
            throw Exception(context
                .getString(R.string.ErrorMsgRemove))
        }
    }

    private fun getPersonObject (item: DTOPerson): Person{
        val person = Person()
        person.ID= item.ID
        person.Name= item.Name
        person.FLastName= item.FLastName
        person.SLastName= item.SLastName
        person.Email= item.Email
        person.Phone= item.Phone
        person.Province= item.Province!!
        person.State= item.State
        person.District=item.District
        person.Address= item.Address
        val bDateParse = Util.parseStringToDateModern(item.Birthday,
            "dd/MM/yyyy")
        person.Birthday = LocalDate.of(bDateParse?.year!!, bDateParse.month.value
            , bDateParse?.dayOfMonth!!)
        //person.Photo= item.Photo
        return person
    }

    private fun convertToDTOPersonObject (person: Person): DTOPerson{
        val strBirthdate = Util.getDateFormatString(person.Birthday.dayOfMonth
            , person.Birthday.month.value, person.Birthday.year)

        return DTOPerson(person.ID, person.Name, person.FLastName
            , person.SLastName, person.Phone, person.Email
            , strBirthdate, person.Province, person.State
            , person.District, person.Address
            , person.Latitude, person.Longitude, "")
    }
}