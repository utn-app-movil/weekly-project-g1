package cr.ac.utn.census.domain.model

import android.graphics.Bitmap
import java.time.LocalDate

data class Person(
    val id: String,
    val name: String,
    val firstLastName: String,
    val secondLastName: String,
    val phone: Int,
    val email: String,
    val birthday: LocalDate,
    val province: Province,
    val state: String,
    val district: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val photo: Bitmap?
)