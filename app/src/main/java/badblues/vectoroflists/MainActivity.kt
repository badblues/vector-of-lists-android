package badblues.vectoroflists

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import badblues.vectoroflists.ui.theme.VectorOfListsAndroidTheme
import badblues.vectoroflists.model.DataModel
import badblues.vectoroflists.model.DataTypes
import badblues.vectoroflists.datastructure.Vector2D
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

    override fun onStop() {
        super.onStop()
        handleSaveVector()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppContent() {
        var addInputText by remember { mutableStateOf("") }
        var deleteInputText by remember { mutableStateOf("") }
        var insertInputText by remember { mutableStateOf("") }
        var changeBaseCapacityText by remember { mutableStateOf("") }
        var addRandomInputText by remember { mutableStateOf("") }
        var vectorText by remember { mutableStateOf(if (model.getCurrentType() == DataTypes.Double) model.getDoublesVector().toString() else model.getVectors2DVector().toString()) }
        var currentDataType by remember { mutableStateOf(model.getCurrentType().toString()) }

        fun updateText() {
            vectorText = if (model.getCurrentType() == DataTypes.Double) model.getDoublesVector().toString() else model.getVectors2DVector().toString()
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = vectorText
                )
            }

            item {
                Row {
                    TextField(
                        value = addInputText,
                        onValueChange = {
                            if(isValidElementInput(it))
                                addInputText = it
                        },
                        label = { Text("Add") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ))
                    Button(
                        onClick = {
                            handleAdd(addInputText)
                            addInputText = ""
                            updateText()
                        },
                    ) {
                        Text("ADD")
                    }
                }
            }

            item {
                Row {
                    TextField(
                        value = insertInputText,
                        onValueChange = {
                            if (isValidIntegerInput(it))
                                insertInputText = it
                        },
                        label = { Text("Position") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ))
                    Button(
                        onClick = {
                            handleInsert(insertInputText, addInputText)
                            insertInputText = ""
                            addInputText = ""
                            updateText()
                        },
                    ) {
                        Text("INSERT")
                    }
                }

            }


            item {
                Row {
                    TextField(
                        value = deleteInputText,
                        onValueChange = {
                            if (isValidIntegerInput(it))
                                deleteInputText = it },
                        label = { Text("Position") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ))
                    Button(
                        onClick = {
                            handleDelete(deleteInputText)
                            deleteInputText=""
                            updateText()
                        },
                    ) {
                        Text("DELETE")
                    }
                }

            }

            item {
                Row {
                    TextField(
                        value = addRandomInputText,
                        onValueChange = {
                            if (isValidIntegerInput(it))
                                addRandomInputText = it },
                        label = { Text("N") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ))
                    Button(
                        onClick = {
                            handleAddRandom(addRandomInputText)
                            addRandomInputText = ""
                            updateText()
                        }
                    ) {
                        Text("ADD N")
                    }
                }
            }


            item {
                Row {
                    TextField(
                        value = changeBaseCapacityText,
                        onValueChange = {
                            if (isValidIntegerInput(it))
                                changeBaseCapacityText = it },
                        label = { Text("Capacity") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ))
                    Button(
                        onClick = {
                            handleChangeBaseCapacity(changeBaseCapacityText)
                            updateText()
                        },
                    ) {
                        Text("CHANGE CAPACITY")
                    }
                }

            }


            item {
                Button(
                    onClick = {
                        handleSort()
                        updateText()
                    }
                ) {
                    Text("SORT")
                }
            }

            item {

                Button(
                    onClick = {
                        handleClear()
                        updateText()
                    }
                ) {
                    Text("CLEAR")
                }
            }


            item {
                Row {
                    Text(currentDataType)
                    Button(
                        onClick = {
                            handleChangeDataType()
                            updateText()
                            currentDataType = model.getCurrentType().toString()
                        }
                    ) {
                        Text("CHANGE TYPE")
                    }
                }
            }
        }
    }

    private fun handleAdd(addText: String) {
        if (addText.isBlank())
            return
        try {
            if (model.getCurrentType() == DataTypes.Double)
                model.getDoublesVector().add(addText.toDouble())
            else
                model.getVectors2DVector().add(Vector2D.parseVector2d(addText))
        } catch (e: Exception) {
            e.message?.let { showToast(it) }
        }
    }

    private fun handleInsert(insertText: String, addText: String) {
        if (addText.isBlank() || insertText.isBlank())
            return
        if (model.getCurrentType() == DataTypes.Double)
            model.getDoublesVector().insert(insertText.toInt(), addText.toDouble())
        else
            model.getVectors2DVector().insert(insertText.toInt(), Vector2D.parseVector2d(addText))
    }

    private fun handleDelete(deleteText: String) {
        if (deleteText.isBlank())
            return
        val position = deleteText.toInt()
        if (model.getCurrentType() == DataTypes.Double)
            model.getDoublesVector().delete(position)
        else
            model.getVectors2DVector().delete(position)
    }

    private fun handleAddRandom(addRandomText: String) {
        if (addRandomText.isBlank())
            return
        val n = addRandomText.toInt()
        val random = Random()
        if (model.getCurrentType() == DataTypes.Double)
            for (i in 0 until n)
                model.getDoublesVector().add(random.nextInt(100).toDouble())
        else
            for (i in 0 until n) {
                val vector2d = Vector2D(random.nextInt(100).toDouble(), random.nextInt(100).toDouble())
                model.getVectors2DVector().add(vector2d)
            }
    }

    private fun handleSort() {
        if (model.getCurrentType() == DataTypes.Double)
            model.getDoublesVector().sort()
        else
            model.getVectors2DVector().sort()
    }

    private fun handleClear() {
        model.clearVectors()
    }

    private fun handleChangeBaseCapacity(newCapacity: String) {
        if (newCapacity.isBlank())
            return
        model.changeBaseCapacities(newCapacity.toInt())
    }

    private fun handleChangeDataType() {
        var newType = if (model.getCurrentType() == DataTypes.Double) DataTypes.Vector2D else DataTypes.Double
        model.setCurrentType(newType)
    }

    private fun handleSaveVector() {
        try {
            val jsonString = if (model.getCurrentType() == DataTypes.Double)
                model.getDoublesVector().toString()
            else
                model.getVectors2DVector().toString()

            val type = model.getCurrentType().toString()

            sharedPreferences.edit().putString(KEY_VECTOR_DATA, jsonString).apply()
            sharedPreferences.edit().putString(KEY_VECTOR_TYPE, type).apply()
            showToast("Vector saved successfully")
        } catch (e: Exception) {
            showToast("Error saving vector: ${e.message}")
        }
    }

    private fun handleOpenVector() {
        try {
            val typeString = sharedPreferences.getString(KEY_VECTOR_TYPE, "")
            val jsonString = sharedPreferences.getString(KEY_VECTOR_DATA, "")
            if (jsonString.isNullOrEmpty() || typeString.isNullOrEmpty()) {
                showToast("No saved vector found")
                return
            }
            val type = DataTypes.valueOf(typeString)
            model.setCurrentType(type)
            if (type == DataTypes.Double) {
                val elements = jsonString
                    .replace("\n", ", ")
                    .trim('[', ']')
                    .split(", ")
                    .map { it.trim().toDouble() }
                for (elem in elements)
                    model.getDoublesVector().add(elem)
            } else {
                val elements = jsonString
                    .replace("\n", ", ")
                    .trim('[', ']')
                    .split("),")
                    .map {
                        val coords = it.replace(" ", "").trim('(', ')').split(",")
                        Vector2D(coords[0].toDouble(), coords[1].toDouble())
                    }
                for (elem in elements)
                    model.getVectors2DVector().add(elem)
            }

            showToast("Vector loaded successfully")
        } catch (e: Exception) {
            showToast("Error loading vector: ${e.message}")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun loadVectorData() {
        // Load saved vector data on app startup
        handleOpenVector()
    }

    private fun isValidElementInput(inputText: String): Boolean {
        val regex = if (model.getCurrentType() == DataTypes.Double) {
            Regex("-?\\d*\\.?\\d*")
        } else {
            Regex("-?\\d*(\\.)?(\\d+)?(,)?(-?\\d+(\\.)?(\\d+)?)?")
        }
        return regex.matches(inputText)
    }

    private fun isValidIntegerInput(inputText: String): Boolean {
        val regex = Regex("\\d*")
        return regex.matches(inputText)
    }

    companion object {
        const val KEY_VECTOR_DATA = "vector_data"
        const val KEY_VECTOR_TYPE = "vector_type"
    }
}
