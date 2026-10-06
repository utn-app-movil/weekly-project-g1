package cr.ac.utn.census.ui

import cr.ac.utn.census.domain.model.Province
import cr.ac.utn.census.util.EXTRA_MESSAGE_PERSONID
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cr.ac.utn.census.R
import cr.ac.utn.census.data.memory.MemoryPersonRepository
import cr.ac.utn.census.domain.model.Person
import cr.ac.utn.census.util.Util
import cr.ac.utn.census.viewmodel.PersonViewModel
import java.time.LocalDate
import java.util.Calendar
import kotlin.compareTo
import kotlin.text.trim
import kotlin.toString

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
    private lateinit var imgPhoto: ImageView
    private lateinit var personViewModel: PersonViewModel
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

        val repository = MemoryPersonRepository()
        personViewModel = PersonViewModel(repository)

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
        imgPhoto = findViewById<ImageView>(R.id.imgPhoto)

        resetDate()

        val personId = intent.getStringExtra(EXTRA_MESSAGE_PERSONID)
        if (personId != null && personId.trim().length == 0) searchPerson(personId)

        val btnSelectDate = findViewById<ImageButton>(R.id.btnSelectDate_person)
        btnSelectDate.setOnClickListener(View.OnClickListener{view ->
            showDatePickerDialog()
        })

        val btnSearch = findViewById<ImageButton>(R.id.btnSearchId_person)
        btnSearch.setOnClickListener(View.OnClickListener{view ->
            searchPerson(txtId.text.trim().toString())
        })

        val btnSelectPhoto = findViewById<ImageButton>(R.id.btnSelectPicture)
        btnSelectPhoto.setOnClickListener(View.OnClickListener{view ->
            takePhoto()
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
                    Util.showDialogCondition(this
                        , getString(R.string.TextSaveActionQuestion)
                        , { savePerson() })
                }else{
                    savePerson()
                }
                return true
            }
            R.id.mnu_delete ->{
                Util.showDialogCondition(this
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
            val person = personViewModel.getPersonById(id)
            if (person != null){
                isEditMode=true
                txtId.setText(person.id.toString())
                txtId.isEnabled=false
                txtName.setText(person.name)
                txtFLastName.setText(person.firstLastName)
                txtSLastName.setText(person.secondLastName)
                txtEmail.setText(person.email)
                txtPhone.setText(person.phone.toString())
                lbBirthdate.setText(getDateFormatString(person.birthday.dayOfMonth
                    , person.birthday.month.value, person.birthday.year ))
                txtProvince.setText(person.province.name)
                txtState.setText(person.state)
                txtDistrict.setText(person.district)
                txtAddress.setText(person.address)
                year = person.birthday.year
                month = person.birthday.month.value - 1
                day = person.birthday.dayOfMonth
                //menuItemDelete.isVisible = true
                imgPhoto.setImageBitmap(person.photo)
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
        val dateparse = Util.parseStringToDateModern(lbBirthdate.text.toString(), "dd/MM/yyyy")
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
        imgPhoto.setImageBitmap(null)
        invalidateOptionsMenu()
    }

    fun savePerson(){
        try {
            if (isValidationData()){
                val person = personViewModel.getPersonById(txtId.text.toString().trim())
                if (person != null
                    && !isEditMode){
                    Toast.makeText(this, getString(R.string.MsgDuplicateDate)
                        , Toast.LENGTH_LONG).show()
                }else{
                    val bDateParse = Util.parseStringToDateModern(lbBirthdate.text.toString(),
                        "dd/MM/yyyy")
                    val province = Province(txtProvince.text.toString(), listOf<String>())

                    val person = Person(txtId.text.toString(),
                    txtName.text.toString(),
                     txtFLastName.text.toString(),
                     txtSLastName.text.toString(),
                    txtPhone.text.toString().toInt(),
                    txtEmail.text.toString(),
                    LocalDate.of(bDateParse?.year!!, bDateParse.month.value
                            , bDateParse?.dayOfMonth!!),
                    province,
                    txtState.text.toString(),
                    txtDistrict.text.toString(),
                    txtAddress.text.toString(),
                    0.0, 0.0,
                    (imgPhoto?.drawable as BitmapDrawable).bitmap)

                    if (!isEditMode)
                        personViewModel.savePerson(person)
                    else
                        personViewModel.updatePerson(person)

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
            personViewModel.deletePerson(txtId.text.toString())
            cleanScreen()
            Toast.makeText(this, getString(R.string.MsgDeleteSuccess)
                , Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Toast.makeText(this, e.message.toString()
                , Toast.LENGTH_LONG).show()
        }
    }

    //Here start the methods to select a phone from the camera or gallery
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            // Image captured successfully, handle the result (e.g., display it in an ImageView)
            // The image is typically saved to the URI provided in the launch() call.
        } else {
            // Image capture failed or was cancelled
        }
    }

    private val cameraPreviewLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            imgPhoto.setImageBitmap(bitmap)
        } else {
            // Image capture failed or was cancelled
        }
    }

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            // Handle the selected image URI here
            data?.data?.let { imageUri ->
                imgPhoto.setImageURI(imageUri)
            }
        }
    }

    fun takePhoto(){
        cameraPreviewLauncher.launch(null)
    }

    fun selectPhoto(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.type = "image/*"
        selectImageLauncher.launch(intent)
    }
}