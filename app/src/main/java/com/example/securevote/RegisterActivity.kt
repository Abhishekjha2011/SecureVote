package com.example.securevote

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

data class User(
    val name: String = "",
    val fatherName: String = "",
    val gender: String = "",
    val dob: String = "",
    val voterId: String = "",
    val username: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val address: String = ""
)

class RegisterActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        database = FirebaseDatabase.getInstance().getReference("Users")

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextFatherName = findViewById<EditText>(R.id.editTextFatherName)
        val radioGroupGender = findViewById<RadioGroup>(R.id.radioGroupGender)
        val editTextDOB = findViewById<EditText>(R.id.editTextDOB)
        val editTextVoterId = findViewById<EditText>(R.id.editTextVoterId)
        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val editTextPhoneNumber = findViewById<EditText>(R.id.editTextPhoneNumber)
        val editTextAddress = findViewById<EditText>(R.id.editTextAddress)
        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit)

        editTextDOB.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this,
                { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->

                    val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                    editTextDOB.setText(formattedDate)
                }, year, month, day)
            datePickerDialog.show()
        }

        buttonSubmit.setOnClickListener {

            val name = editTextName.text.toString().trim()
            val fatherName = editTextFatherName.text.toString().trim()
            val dob = editTextDOB.text.toString().trim()
            val voterId = editTextVoterId.text.toString().trim()
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val phoneNumber = editTextPhoneNumber.text.toString().trim()
            val address = editTextAddress.text.toString().trim()

            if (name.isEmpty() || fatherName.isEmpty() || dob.isEmpty() ||
                voterId.isEmpty() || username.isEmpty() || password.isEmpty() ||
                phoneNumber.isEmpty() || address.isEmpty()
            ) {
                Toast.makeText(this, "Fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidVoterId(voterId)) {
                Toast.makeText(
                    this,
                    "Invalid Voter ID. It should be 10 characters: first 3 letters and 7 digits.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!isValidPhoneNumber(phoneNumber)) {
                Toast.makeText(
                    this,
                    "Invalid Phone Number. It should be 10 digits.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (!isValidUsername(username)) {
                Toast.makeText(
                    this,
                    "Invalid Username. It should be exactly 6 characters and contain both letters and numbers.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (!isValidPassword(password)) {
                Toast.makeText(
                    this,
                    "Invalid Password. It should be exactly 6 characters and contain both letters and numbers.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val selectedGenderId = radioGroupGender.checkedRadioButtonId
            if (selectedGenderId == -1) {
                Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val selectedGender = findViewById<RadioButton>(selectedGenderId).text.toString()

            val user = User(name, fatherName, selectedGender, dob, voterId, username, password, phoneNumber, address)

            database.child(voterId).setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Registration Failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isValidVoterId(voterId: String): Boolean {
        val regex = "^[A-Za-z]{3}\\d{7}$".toRegex()
        return voterId.matches(regex)
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        val regex = "^\\d{10}$".toRegex()
        return phone.matches(regex)
    }

    private fun isValidUsername(username: String): Boolean {
        val regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6}$".toRegex()
        return username.matches(regex)
    }

    private fun isValidPassword(password: String): Boolean {
        val regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6}$".toRegex()
        return password.matches(regex)
    }
}
