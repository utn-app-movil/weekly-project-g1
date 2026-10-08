package cr.ac.utn.census.ui

import cr.ac.utn.census.util.EXTRA_MESSAGE_PERSONID
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cr.ac.utn.census.R
import cr.ac.utn.census.domain.model.Person
import cr.ac.utn.census.util.Util
import cr.ac.utn.census.viewmodel.PersonViewModel

class PeopleListActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var customAdapter: PersonListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_people_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recycler =  findViewById<RecyclerView>(R.id.rvperson)
        val personVM = Util.buildPersonViewModel()
        customAdapter = PersonListAdapter(personVM.getPeople(), this)
        val layoutManager = LinearLayoutManager(applicationContext)
        recycler.layoutManager = layoutManager
        recycler.adapter = customAdapter
        customAdapter.notifyDataSetChanged()
    }

    override fun onItemClicked(person: Person) {
        Util.openActivity(this, PersonActivity::class.java, EXTRA_MESSAGE_PERSONID, person.id)
    }
}