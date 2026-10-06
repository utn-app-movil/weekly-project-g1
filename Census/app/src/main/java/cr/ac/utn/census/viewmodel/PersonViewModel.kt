package cr.ac.utn.census.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.census.domain.model.Person
import cr.ac.utn.census.domain.repository.IPersonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PersonViewModel(
    private val repository: IPersonRepository
) : ViewModel() {

    fun getPeople(): List<Person> {
        return repository.getPeople()
    }

    fun getPersonById(id: String): Person? {
        return repository.getById(id)
    }

    fun savePerson(person: Person) {
        repository.addPerson(person)
    }

    fun updatePerson(person: Person) {
        repository.updatePerson(person)
    }

    fun deletePerson(id: String) {
        repository.deletePerson(id)
    }
}