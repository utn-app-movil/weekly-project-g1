package cr.ac.utn.census

import Controller.PersonController
import Entity.Person
import Entity.Province
import Interfaces.IActions
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
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
    private var isEditMode: Boolean=false
    private var year = 0
    private var month = 0
    private var day = 0
    private lateinit var personController: PersonController
    private lateinit var menuitemDelete: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_person)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        personController= PersonController(this)
        txtId= findViewById<EditText>(R.id.txtId_person)
        txtName= findViewById<EditText>(R.id.txtName_person)
        txtFLastName= findViewById<EditText>(R.id.txtFLastName_person)
        txtSLastName= findViewById<EditText>(R.id.txtSLastName_person)
        txtEmail= findViewById<EditText>(R.id.txtEmail_person)
        txtPhone= findViewById<EditText>(R.id.txtPhone_person)
        txtProvince= findViewById<EditText>(R.id.txtProvince_person)
        lbBirthdate= findViewById<EditText>(R.id.lbBirthdfate_person)
        txtState= findViewById<EditText>(R.id.txtState_person)
        txtDistrict= findViewById<EditText>(R.id.txtDistrict_person)
        txtAddress= findViewById<EditText>(R.id.txtAddress_person)

        ResetDate ()

        val btnSelectDate = findViewById<ImageButton>(R.id.btnSelectDate_person)
        btnSelectDate.setOnClickListener(View.OnClickListener{view ->
            showDatePickerDialog()
        })

        val btnSearch = findViewById<ImageButton>(R.id.btnSearchId_person)
        btnSearch.setOnClickListener(View.OnClickListener{view ->
            searchPerson(txtId.text.trim().toString())
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_crud, menu)
        menuitemDelete = menu!!.findItem(R.id.mnuDelete)
        if (isEditMode)
            menuitemDelete.isVisible = true
        else
            menuitemDelete.isVisible =false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.mnuSave -> {
                if (isEditMode){
                    Util.Util.showDialogCondition(this
                            , getString(R.string.TextSaveActionQuestion)
                            , { savePerson() })
                }else
                    savePerson()
                true
            }
            R.id.mnuDelete -> {
                Util.Util.showDialogCondition(this
                    , getString(R.string.TextDeleteActionQuestion)
                    ,{ deleteContact() })
                return true
            }
            R.id.mnuCancel -> {
                cleanScreen()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun searchPerson(id: String){
        try {
            val person = personController.getById(id)
            if (person != null){
                isEditMode = true
                txtId.setText(person.ID.toString())
                txtId.isEnabled=false
                txtName.setText(person.Name)
                txtFLastName.setText(person.FLastName)
                txtSLastName.setText(person.SLastName)
                txtEmail.setText(person.Email)
                txtPhone.setText(person.Phone.toString())
                lbBirthdate.setText(getDateFormatString(person.Birthday.dayOfMonth, person.Birthday.month.value, person.Birthday.year, ))
                txtProvince.setText(person.Province.Name)
                txtState.setText(person.State)
                txtDistrict.setText(person.District)
                txtAddress.setText(person.Address)
                year = person.Birthday.year
                month = person.Birthday.month.value
                day = person.Birthday.dayOfMonth
                menuitemDelete.isVisible = true
            }else
                Toast.makeText(this, R.string.MsgDataNoFound
                    , Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            cleanScreen()
            Toast.makeText(this, e.message.toString(),Toast.LENGTH_LONG).show()
        }
    }

    fun cleanScreen(){
        ResetDate()
        isEditMode = false
        txtId.isEnabled=true
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

    private fun ResetDate(){
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(this, this, year, month, day)
        datePickerDialog.show()
    }

    fun getDateFormatString(dayOfMonth: Int, monthValue: Int, yearValue: Int): String{
        return "${if (dayOfMonth < 10) "0" else "" }$dayOfMonth/${if (monthValue < 10) "0" else "" }$monthValue/$yearValue"
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        // Month is 0-indexed in Calendar, so add 1 for display
        lbBirthdate.text = getDateFormatString(dayOfMonth, month+1, year)
    }

    fun isValidatedData(): Boolean{
        val dateparse= Util.Util.parseStringToDateModern(lbBirthdate.text.toString(), "dd/MM/yyyy")
        return txtId.text.trim().isNotEmpty() && txtName.text.trim().isNotEmpty()
                && txtFLastName.text.trim().isNotEmpty() && txtSLastName.text.trim().isNotEmpty()
                && txtEmail.text.trim().isNotEmpty() && lbBirthdate.text.trim().isNotEmpty()
                && txtProvince.text.trim().isNotEmpty() && txtState.text.trim().isNotEmpty()
                && txtDistrict.text.trim().isNotEmpty() && txtAddress.text.trim().isNotEmpty()
                && (txtPhone.text.trim().isNotEmpty() && txtPhone.text.trim().length >= 8
                    && txtPhone.text.toString()?.toInt()!! != null && txtPhone.text.toString().trim() != "0")
                && dateparse != null
    }

    fun savePerson(){
        try {
            if (isValidatedData()){
                if (personController.getById(txtId.text.toString()) != null && !isEditMode){
                    Toast.makeText(this, R.string.MsgDuplicateData
                        , Toast.LENGTH_LONG).show()
                }else{
                    val person = Person()
                    person.ID = txtId.text.toString()
                    person.Name = txtName.text.toString()
                    person.FLastName = txtFLastName.text.toString()
                    person.SLastName = txtSLastName.text.toString()
                    person.Email = txtEmail.text.toString()
                    person.Phone = txtPhone.text.toString()?.toInt()!!
                    val Bdate2 =Util.Util.parseStringToDateModern(lbBirthdate.text.toString(), "dd/MM/yyyy")
                    person.Birthday = LocalDate.of(Bdate2?.year!!, Bdate2.month.value, Bdate2?.dayOfMonth!!)
                    val province = Province()
                    province.Name = txtProvince.text.toString()
                    person.Province = province
                    person.State = txtState.text.toString()
                    person.District = txtDistrict.text.toString()
                    person.Address= txtAddress.text.toString()

                    if (!isEditMode)
                        personController.addPerson(person)
                    else
                        personController.updatePerson(person)

                    cleanScreen()
                    Toast.makeText(this, R.string.MsgSaveSuccess
                        , Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, R.string.MsgMissingData
                    , Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            Toast.makeText(this, e.message.toString()
                , Toast.LENGTH_LONG).show()
        }
    }

    fun deleteContact(): Unit{
        try {
            personController.removePerson(txtId.text.toString())
            cleanScreen()
            Toast.makeText(this, R.string.MsgDeleteSucess
                , Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Toast.makeText(this, e.message.toString()
                , Toast.LENGTH_LONG).show()
        }
    }
}