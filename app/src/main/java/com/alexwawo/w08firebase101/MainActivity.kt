package com.alexwawo.w08firebase101

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun StudentRegistrationScreen(viewModel: StudentViewModel = viewModel()) {
    var studentId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var program by remember { mutableStateOf("") }
    var editingStudent by remember { mutableStateOf<Student?>(null) }

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
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = program,
            onValueChange = { program = it },
            label = { Text("Program") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Submit/Update Button
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (editingStudent == null) {
                    viewModel.addStudent(Student(studentId, name, program))
                } else {
                    viewModel.updateStudent(
                        Student(
                            id = studentId,
                            name = name,
                            program = program,
                            docId = editingStudent!!.docId
                        )
                    )
                    editingStudent = null
                }
                studentId = ""
                name = ""
                program = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (editingStudent == null) "Submit" else "Update")
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = 1.dp
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
                        Text(
                            text = "ID: ${student.id}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Name: ${student.name}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Program: ${student.program}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    studentId = student.id
                                    name = student.name
                                    program = student.program
                                    editingStudent = student
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Edit")
                            }

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
fun PreviewStudentRegistrationScreen() {
    W08Firebase101Theme {
        StudentRegistrationScreen()
    }
}