package cr.ac.utn.census

import Controller.PersonController
import Entity.DTOProvince
import Entity.Person
import Entity.Province
import Util.EXTRA_MESSAGE_PERSONID
import Util.Util
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
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
    private lateinit var imgPhoto: ImageView
    private lateinit var personController: PersonController
    private var isEditMode: Boolean= false
    private var day: Int=0
    private var month: Int=0
    private var year: Int=0
    private lateinit var menuItemDelete: MenuItem
    lateinit var mycontext: Context

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
        imgPhoto = findViewById<ImageView>(R.id.imgPhoto)

        resetDate()

        mycontext = this

        val personId = intent.getStringExtra(EXTRA_MESSAGE_PERSONID)
        if (personId != null && personId.trim().length > 0) searchPerson(personId)

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

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        lbBirthdate.text= Util.getDateFormatString(dayOfMonth, month+1, year)
    }

    private fun searchPerson(id: String){
        lifecycleScope.launch {
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
                    lbBirthdate.setText(Util.getDateFormatString(person.Birthday.dayOfMonth
                        , person.Birthday.month.value, person.Birthday.year ))
                    txtProvince.setText(person.Province.Name)
                    txtState.setText(person.State)
                    txtDistrict.setText(person.District)
                    txtAddress.setText(person.Address)
                    year = person.Birthday.year
                    month = person.Birthday.month.value - 1
                    day = person.Birthday.dayOfMonth
                    menuItemDelete.isVisible = true
                    imgPhoto.setImageBitmap(person.Photo)
                }else{
                    Toast.makeText(mycontext, getString(R.string.MsgDataNoFound),
                        Toast.LENGTH_LONG).show()
                }
            }catch (e: Exception){
                cleanScreen()
                Toast.makeText(mycontext, e.message.toString(),
                    Toast.LENGTH_LONG).show()
            }
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
        lifecycleScope.launch {
            try {
                if (isValidationData()){
                    if (personController.getById(txtId.text.toString().trim()) != null
                        && !isEditMode){
                        Toast.makeText(mycontext, getString(R.string.MsgDuplicateDate)
                            , Toast.LENGTH_LONG).show()
                    }else{
                        val person = Person()
                        person.ID = txtId.text.toString()
                        person.Name = txtName.text.toString()
                        person.FLastName = txtFLastName.text.toString()
                        person.SLastName = txtSLastName.text.toString()
                        person.Email = txtEmail.text.toString()
                        person.Phone = txtPhone.text.toString().toInt()
                        person.Photo = (imgPhoto?.drawable as BitmapDrawable).bitmap
                        val bDateParse = Util.parseStringToDateModern(lbBirthdate.text.toString(),
                            "dd/MM/yyyy")
                        person.Birthday = LocalDate.of(bDateParse?.year!!, bDateParse.month.value
                            , bDateParse?.dayOfMonth!!)
                        val province = DTOProvince()
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

                        Toast.makeText(mycontext, getString(R.string.MsgSaveSuccess)
                            , Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(mycontext, "Datos incompletos"
                        , Toast.LENGTH_LONG).show()
                }
            }catch (e: Exception){
                Toast.makeText(mycontext, e.message.toString()
                    , Toast.LENGTH_LONG).show()
            }
        }
    }

    fun deletePerson(): Unit{
        lifecycleScope.launch {
            try {
                personController.removePerson(txtId.text.toString())
                cleanScreen()
                Toast.makeText(mycontext, getString(R.string.MsgDeleteSuccess)
                    , Toast.LENGTH_LONG).show()
            }catch (e: Exception){
                Toast.makeText(mycontext, e.message.toString()
                    , Toast.LENGTH_LONG).show()
            }
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
        if (result.resultCode == Activity.RESULT_OK) {
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