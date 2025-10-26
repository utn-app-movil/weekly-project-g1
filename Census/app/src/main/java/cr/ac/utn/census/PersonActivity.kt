package cr.ac.utn.census

import Controller.PersonController
import Entity.Person
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PersonActivity : AppCompatActivity() {
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFLastName: EditText
    private lateinit var txtSLastName: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtBirthdate: EditText
    private lateinit var txtProvince: EditText
    private lateinit var txtState: EditText
    private lateinit var txtDistrict: EditText
    private lateinit var txtAddress: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_person)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtId= findViewById<EditText>(R.id.txtId_person)
        txtName= findViewById<EditText>(R.id.txtName_person)
        txtFLastName= findViewById<EditText>(R.id.txtFLastName_person)
        txtSLastName= findViewById<EditText>(R.id.txtSLastName_person)
        txtEmail= findViewById<EditText>(R.id.txtEmail_person)
        txtPhone= findViewById<EditText>(R.id.txtPhone_person)
        txtProvince= findViewById<EditText>(R.id.txtProvince_person)
        txtBirthdate= findViewById<EditText>(R.id.txtBirthdate_person)
        txtState= findViewById<EditText>(R.id.txtState_person)
        txtDistrict= findViewById<EditText>(R.id.txtDistrict_person)
        txtAddress= findViewById<EditText>(R.id.txtAddress_person)

        val btnSave = findViewById<Button>(R.id.btnSave_person)
        btnSave.setOnClickListener(View.OnClickListener{view ->
            savePerson()
        })
    }

    fun validationData(person: Person): Boolean{
        return true
    }
    fun savePerson(){
        try {
            val person = Person()
            person.ID = txtId.text.toString()
            person.Name = txtName.text.toString()
            person.FLastName = txtFLastName.text.toString()
            person.SLastName = txtSLastName.text.toString()
            person.Email = txtEmail.text.toString()
            person.Phone = txtPhone.text.toString().toInt()
            //person.Birthday = txtBirthdate.text.toString().
            //person.Province = txtProvince.text.toString()
            person.State = txtState.text.toString()
            person.District = txtDistrict.text.toString()
            person.Address= txtAddress.text.toString()
            if (validationData(person)){
                val personController= PersonController(this)
                personController.addPerson(person)
                Toast.makeText(this, "La persona se guardò"
                    , Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "Datos incompletos"
                    , Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            Toast.makeText(this, e.message.toString()
                , Toast.LENGTH_LONG).show()
        }
    }
}