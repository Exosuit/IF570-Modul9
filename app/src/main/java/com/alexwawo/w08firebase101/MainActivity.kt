package com.alexwawo.w08firebase101

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexwawo.w08firebase101.ui.theme.W08Firebase101Theme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContent {
            W08Firebase101Theme {
                StudentRegistrationScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegistrationScreen(viewModel: StudentViewModel = viewModel()) {
    var studentId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var program by remember { mutableStateOf("") }
    var selectedStudentDocId by remember { mutableStateOf<String?>(null) }
    var currentPhone by remember { mutableStateOf("") }
    var phoneList by remember { mutableStateOf(emptyList<String>()) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // Input Fields
        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("Student ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = program,
            onValueChange = { program = it },
            label = { Text("Program") },
            modifier = Modifier.fillMaxWidth()
        )

        // Phone Number Input
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = currentPhone,
                onValueChange = { currentPhone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    if (currentPhone.isNotBlank()) {
                        phoneList = phoneList + currentPhone
                        currentPhone = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Add")
            }
        }

        // Display Phone Numbers with Remove Buttons
        if (phoneList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Phone Numbers:", style = MaterialTheme.typography.labelLarge)
            phoneList.forEachIndexed { index, phone ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("- $phone", modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            phoneList = phoneList.toMutableList().also {
                                it.removeAt(index)
                            }
                        }
                    ) {
                        Text("Remove")
                    }
                }
            }
        }

        // Submit/Update Button
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (selectedStudentDocId != null) {
                    viewModel.updateStudent(
                        Student(
                            docId = selectedStudentDocId!!,
                            id = studentId,
                            name = name,
                            program = program,
                            phones = phoneList
                        )
                    )
                    selectedStudentDocId = null
                } else {
                    viewModel.addStudent(
                        Student(
                            id = studentId,
                            name = name,
                            program = program,
                            phones = phoneList
                        )
                    )
                }
                // Reset form
                studentId = ""
                name = ""
                program = ""
                phoneList = emptyList()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (selectedStudentDocId != null) "Update" else "Submit")
        }

        Divider(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        )

        // Student List
        Text("Student List", style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(viewModel.students) { student ->
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("ID: ${student.id}")
                    Text("Name: ${student.name}")
                    Text("Program: ${student.program}")
                    if (student.phones.isNotEmpty()) {
                        Text("Phones:")
                        student.phones.forEach {
                            Text("- $it", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                studentId = student.id
                                name = student.name
                                program = student.program
                                phoneList = student.phones
                                selectedStudentDocId = student.docId
                            }
                        ) {
                            Text("Edit")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.deleteStudent(student) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete")
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    W08Firebase101Theme {
        StudentRegistrationScreen()
    }
}