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
    var editingDocId by remember { mutableStateOf<String?>(null) }
    var currentPhone by remember { mutableStateOf("") }
    var phoneList by remember { mutableStateOf(emptyList<String>()) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // Student Information Fields
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

        // Phone Number Management
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

        // Phone Numbers List with Edit/Delete
        if (phoneList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Phone Numbers:",
                style = MaterialTheme.typography.labelLarge
            )
            phoneList.forEachIndexed { index, phone ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phoneList = phoneList.toMutableList().also { list ->
                                list[index] = it
                            }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            phoneList = phoneList.toMutableList().also {
                                it.removeAt(index)
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete phone"
                        )
                    }
                }
            }
        }

        // Submit/Update Button
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                if (editingDocId != null) {
                    viewModel.updateStudent(
                        Student(
                            id = studentId,
                            name = name,
                            program = program,
                            phones = phoneList,
                            docId = editingDocId!!
                        )
                    )
                    editingDocId = null
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
            Text(if (editingDocId != null) "Update" else "Submit")
        }

        Divider(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        )

        // Student List
        Text(
            text = "Student List",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn {
            items(viewModel.students) { student ->
                Card(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("ID: ${student.id}")
                        Text("Name: ${student.name}")
                        Text("Program: ${student.program}")

                        if (student.phones.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Phones:")
                            student.phones.forEach {
                                Text("- $it", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    studentId = student.id
                                    name = student.name
                                    program = student.program
                                    phoneList = student.phones
                                    editingDocId = student.docId
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
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudentRegistrationPreview() {
    W08Firebase101Theme {
        StudentRegistrationScreen()
    }
}