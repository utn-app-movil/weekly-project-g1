package cr.ac.utn.census.domain.repository

import cr.ac.utn.census.domain.model.Person

interface IPersonRepository {

    fun getPeople(): List<Person>

    fun getById(id:String): Person?

    fun addPerson(person:Person)

    fun updatePerson(person:Person)

    fun deletePerson(id:String)
    //suspend fun getByFullName(fullName: String): Person?

}