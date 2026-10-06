package cr.ac.utn.census.data.memory

import cr.ac.utn.census.domain.model.Person
import cr.ac.utn.census.domain.repository.IPersonRepository

class MemoryPersonRepository: IPersonRepository{
    private val people = mutableListOf<Person>()
    override fun addPerson(person: Person) {
        people.add(person)
    }

    override fun deletePerson(id: String) {
        people.removeIf { it.id.trim() == id.trim() }
    }

    override fun updatePerson(person: Person) {
        deletePerson(person.id)
        addPerson(person)
    }

    override fun getPeople()= people

    override fun getById(id: String): Person? {
        val result = people.
            filter { it.id.trim() == id.trim()}
        return if(result.any()) result[0] else null
    }

    /*fun getByFullName(fullName: String): Person? {
        val result = people.
        filter { it.FullName() == fullName.trim()}
        return if(result.any()) result[0] else null
    }*/

}