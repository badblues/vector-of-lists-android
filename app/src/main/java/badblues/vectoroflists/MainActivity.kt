package badblues.vectoroflists

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import badblues.vectoroflists.ui.theme.VectorOfListsAndroidTheme
import badblues.vectoroflists.model.DataModel
import badblues.vectoroflists.model.DataTypes
import badblues.vectoroflists.datastructure.Vector2D
import badblues.vectoroflists.datastructure.VectorOfLists
import androidx.compose.ui.unit.dp
import java.io.*
import java.util.*

class MainActivity : ComponentActivity() {
    private val model: DataModel = DataModel.getInstance()
    private lateinit var sharedPreferences: SharedPreferences

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VectorOfListsAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent()
                }
            }
        }

        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        loadVectorData()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppContent() {
        var addInputText by remember { mutableStateOf("") }
        var deleteInputText by remember { mutableStateOf("") }
        var insertInputText by remember { mutableStateOf("") }
        var changeBaseCapacityText by remember { mutableStateOf("") }
        var addRandomInputText by remember { mutableStateOf("") }

        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TextField(
                value = addInputText,
                onValueChange = { addInputText = it },
                label = { Text("Add") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        handleAdd(context, addInputText)
                        addInputText = ""
                    }
                )
            )

            // Other UI elements go here...

            Button(
                onClick = { handleAdd(context, addRandomInputText) },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Add Random")
            }

            Button(
                onClick = { handleDelete(context, deleteInputText) },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Delete")
            }

            Button(
                onClick = { handleSort() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Sort")
            }

            Button(
                onClick = { handleClear() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Clear")
            }

            Button(
                onClick = { handleChangeBaseCapacity(changeBaseCapacityText) },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Change Base Capacity")
            }

            Button(
                onClick = { handleSaveVector() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Save Vector")
            }

            Button(
                onClick = { handleOpenVector() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Open Vector")
            }

        }
    }

    private fun handleAdd(context: Context, inputText: String) {
        // Handle add logic here
        // You can use the 'model' instance to interact with your existing data model
    }

    private fun handleDelete(context: Context, inputText: String) {
        // Handle delete logic here
    }

    private fun handleSort() {
        if (model.getCurrentType() == DataTypes.Double)
            model.getDoublesVector().sort()
        else
            model.getVectors2DVector().sort()
        updateText()
    }

    private fun handleClear() {
        model.clearVectors()
        updateText()
    }

    private fun handleChangeBaseCapacity(newCapacity: String) {
        if (newCapacity.isNotEmpty()) {
            model.changeBaseCapacities(newCapacity.toInt())
            updateText()
        }
    }

    private fun handleSaveVector() {
        try {
            val jsonString = if (model.getCurrentType() == DataTypes.Double)
                model.getDoublesVector().toString()
            else
                model.getVectors2DVector().toString()

            sharedPreferences.edit().putString(KEY_VECTOR_DATA, jsonString).apply()
            showToast("Vector saved successfully")
        } catch (e: Exception) {
            showToast("Error saving vector: ${e.message}")
        }
    }

    private fun handleOpenVector() {
        try {
            val jsonString = sharedPreferences.getString(KEY_VECTOR_DATA, "")
            if (jsonString.isNullOrEmpty()) {
                showToast("No saved vector found")
                return
            }

            if (model.getCurrentType() == DataTypes.Double) {
                // Parse and set Double vector
                // Example: "[1.0, 2.0, 3.0]"
                val elements = jsonString
                    .trim('[', ']')
                    .split(",")
                    .map { it.trim().toDouble() }
                val doubleVector = VectorOfLists<Double>(2)
                model.setDoublesVector(doubleVector)
            } else {
                // Parse and set Vector2D vector
                // Example: "[(1.0, 2.0), (3.0, 4.0)]"
                val elements = jsonString
                    .trim('[', ']')
                    .split("),")
                    .map {
                        val coords = it.trim('(', ')').split(",")
                        Vector2D(coords[0].toDouble(), coords[1].toDouble())
                    }
                val vector2DVector = VectorOfLists<Vector2D>(2)
                model.setVectors2DVector(vector2DVector)
            }

            showToast("Vector loaded successfully")
            updateText()
        } catch (e: Exception) {
            showToast("Error loading vector: ${e.message}")
        }
    }

    private fun showToast(message: String) {
        // Implement your showToast logic here
    }

    private fun loadVectorData() {
        // Load saved vector data on app startup
        handleOpenVector()
    }

    private fun updateText() {
        // Implement your updateText logic here
    }

    // ... (Other methods remain the same)

    companion object {
        const val KEY_VECTOR_DATA = "vector_data"
    }
}
