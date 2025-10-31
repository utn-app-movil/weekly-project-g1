package cr.ac.utn.census

import Controller.PersonController
import Entity.Person
import Entity.Province
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.LocalDate
import java.util.Calendar

class PersonActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFLastName: EditText
    private lateinit var txtSLastName: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPhone: EditText
    private lateinit var lbBirthdate: TextView
    private lateinit var txtProvince: EditText
    private lateinit var txtState: EditText
    private lateinit var txtDistrict: EditText
    private lateinit var txtAddress: EditText
    private lateinit var personController: PersonController
    private var isEditMode: Boolean= false
    private var day: Int=0
    private var month: Int=0
    private var year: Int=0
    private lateinit var menuItemDelete: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_person)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        personController = PersonController(this)

        txtId= findViewById<EditText>(R.id.txtId_person)
        txtName= findViewById<EditText>(R.id.txtName_person)
        txtFLastName= findViewById<EditText>(R.id.txtFLastName_person)
        txtSLastName= findViewById<EditText>(R.id.txtSLastName_person)
        txtEmail= findViewById<EditText>(R.id.txtEmail_person)
        txtPhone= findViewById<EditText>(R.id.txtPhone_person)
        txtProvince= findViewById<EditText>(R.id.txtProvince_person)
        lbBirthdate= findViewById<TextView>(R.id.lbBirthdate_person)
        txtState= findViewById<EditText>(R.id.txtState_person)
        txtDistrict= findViewById<EditText>(R.id.txtDistrict_person)
        txtAddress= findViewById<EditText>(R.id.txtAddress_person)

        resetDate()

        val btnSelectDate = findViewById<ImageButton>(R.id.btnSelectDate_person)
        btnSelectDate.setOnClickListener(View.OnClickListener{view ->
            showDatePickerDialog()
        })

        val btnSearch = findViewById<ImageButton>(R.id.btnSearchId_person)
        btnSearch.setOnClickListener(View.OnClickListener{view ->
            searchPerson(txtId.text.trim().toString())
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_crud, menu)
        menuItemDelete= menu!!.findItem(R.id.mnu_delete)
        menuItemDelete.isVisible = isEditMode
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.mnu_save ->{
                if (isEditMode){
                    Util.Util.showDialogCondition(this
                        , getString(R.string.TextSaveActionQuestion)
                        , { savePerson() })
                }else{
                    savePerson()
                }
                return true
            }
            R.id.mnu_delete ->{
                Util.Util.showDialogCondition(this
                    , getString(R.string.TextDeleteActionQuestion)
                    , { deletePerson() })
                return true
            }
            R.id.mnu_cancel ->{
                cleanScreen()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun resetDate(){
        val calendar = Calendar.getInstance()
        year= calendar.get(Calendar.YEAR)
        month= calendar.get(Calendar.MONTH)
        day= calendar.get(Calendar.DAY_OF_MONTH)
    }

    private fun showDatePickerDialog(){
        val datePickerDialog = DatePickerDialog(this, this
            , year, month, day)
        datePickerDialog.show()
    }

    private fun getDateFormatString(dayOfMonth: Int, monthValue: Int, yearValue: Int): String{
        return "${if (dayOfMonth < 10) "0" else ""}$dayOfMonth/${if (monthValue < 10) "0" else ""}$monthValue/$yearValue"
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        lbBirthdate.text=getDateFormatString(dayOfMonth, month+1, year)
    }

    private fun searchPerson(id: String){
        try {
            val person = personController.getById(id)
            if (person != null){
                isEditMode=true
                txtId.setText(person.ID.toString())
                txtId.isEnabled=false
                txtName.setText(person.Name)
                txtFLastName.setText(person.FLastName)
                txtSLastName.setText(person.SLastName)
                txtEmail.setText(person.Email)
                txtPhone.setText(person.Phone.toString())
                lbBirthdate.setText(getDateFormatString(person.Birthday.dayOfMonth
                    , person.Birthday.month.value, person.Birthday.year ))
                txtProvince.setText(person.Province.Name)
                txtState.setText(person.State)
                txtDistrict.setText(person.District)
                txtAddress.setText(person.Address)
                year = person.Birthday.year
                month = person.Birthday.month.value - 1
                day = person.Birthday.dayOfMonth
                menuItemDelete.isVisible = true
            }else{
                Toast.makeText(this, getString(R.string.MsgDataNoFound),
                    Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            cleanScreen()
            Toast.makeText(this, e.message.toString(),
                Toast.LENGTH_LONG).show()
        }
    }

    fun isValidationData(): Boolean{
        val dateparse = Util.Util.parseStringToDateModern(lbBirthdate.text.toString(), "dd/MM/yyyy")
        return txtId.text.trim().isNotEmpty() && txtName.text.trim().isNotEmpty()
                && txtFLastName.text.trim().isNotEmpty() && txtSLastName.text.trim().isNotEmpty()
                && txtEmail.text.trim().isNotEmpty() && lbBirthdate.text.trim().isNotEmpty()
                && txtProvince.text.trim().isNotEmpty() && txtState.text.trim().isNotEmpty()
                && txtDistrict.text.trim().isNotEmpty() && txtAddress.text.trim().isNotEmpty()
                && (txtPhone.text.trim().isNotEmpty() && txtPhone.text.trim().length >= 8
                && txtPhone.text.toString()?.toInt()!! != null && txtPhone.text.toString()?.toInt()!! != 0)
                && dateparse != null


    }

    private fun cleanScreen(){
        resetDate()
        isEditMode=false
        txtId.isEnabled = true
        txtId.setText("")
        txtName.setText("")
        txtFLastName.setText("")
        txtSLastName.setText("")
        txtEmail.setText("")
        txtPhone.setText("")
        lbBirthdate.setText("")
        txtProvince.setText("")
        txtState.setText("")
        txtDistrict.setText("")
        txtAddress.setText("")
        invalidateOptionsMenu()
    }

    fun savePerson(){
        try {
            if (isValidationData()){
                if (personController.getById(txtId.text.toString().trim()) != null
                    && !isEditMode){
                    Toast.makeText(this, getString(R.string.MsgDuplicateDate)
                        , Toast.LENGTH_LONG).show()
                }else{
                    val person = Person()
                    person.ID = txtId.text.toString()
                    person.Name = txtName.text.toString()
                    person.FLastName = txtFLastName.text.toString()
                    person.SLastName = txtSLastName.text.toString()
                    person.Email = txtEmail.text.toString()
                    person.Phone = txtPhone.text.toString().toInt()
                    val bDateParse = Util.Util.parseStringToDateModern(lbBirthdate.text.toString(),
                        "dd/MM/yyyy")
                    person.Birthday = LocalDate.of(bDateParse?.year!!, bDateParse.month.value
                            , bDateParse?.dayOfMonth!!)
                    val province = Province()
                    province.Name= txtProvince.text.toString()
                    person.Province = province
                    person.State = txtState.text.toString()
                    person.District = txtDistrict.text.toString()
                    person.Address= txtAddress.text.toString()

                    if (!isEditMode)
                        personController.addPerson(person)
                    else
                        personController.updatePerson(person)

                    cleanScreen()

                    Toast.makeText(this, getString(R.string.MsgSaveSuccess)
                        , Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "Datos incompletos"
                    , Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            Toast.makeText(this, e.message.toString()
                , Toast.LENGTH_LONG).show()
        }
    }

    fun deletePerson(): Unit{
        try {
            personController.removePerson(txtId.text.toString())
            cleanScreen()
            Toast.makeText(this, getString(R.string.MsgDeleteSuccess)
                , Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Toast.makeText(this, e.message.toString()
                , Toast.LENGTH_LONG).show()
        }
    }
}