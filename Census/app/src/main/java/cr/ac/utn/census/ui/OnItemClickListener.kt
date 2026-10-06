package cr.ac.utn.census.ui

import cr.ac.utn.census.domain.model.Person

interface OnItemClickListener {
    fun onItemClicked (person: Person)
}