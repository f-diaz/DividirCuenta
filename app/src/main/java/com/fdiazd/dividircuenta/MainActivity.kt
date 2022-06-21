package com.fdiazd.dividircuenta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fdiazd.dividircuenta.components.InputField
import com.fdiazd.dividircuenta.ui.theme.DividirCuentaTheme
import com.fdiazd.dividircuenta.util.calculateTotalPerPerson
import com.fdiazd.dividircuenta.util.calculateTotalTip
import com.fdiazd.dividircuenta.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DividirCuentaTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.padding(20.dp),
                    color = MaterialTheme.colors.background) {

                    MainContent()
                }
            }
        }
    }
}

//@Preview
@Composable
fun TopHeader (totalPerPerson: Double = 0.0){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .height(100.dp)
        .clip(shape = RoundedCornerShape(corner = CornerSize(5.dp))),
        color = MaterialTheme.colors.secondary
    ) {
    Column (modifier = Modifier
        .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
            ) {
        val total = "%.2f".format(totalPerPerson)
        Text(text = "Total por persona",
            style = MaterialTheme.typography.h5)
        Text(text = "$total€",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.ExtraBold)
    }
    }
}



@Preview
@Composable
fun MainContent (){

    Column(modifier = Modifier.padding(all = 12.dp)) {
        BillForm()
    }



}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm (modifier: Modifier = Modifier,
                onValChange: (String) -> Unit ={}
              ){
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.value *100).toInt()

    val splitByState = remember {
        mutableStateOf(1)
    }

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    
        TopHeader (totalPerPerson = totalPerPersonState.value)

        Surface(modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(5.dp)),
            border = BorderStroke(1.dp, color = Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start) {
                InputField(
                    valueState = totalBillState,
                    labelId = "Introduce la cantidad",
                    isSingleLine = true,
                    onAction = KeyboardActions{
                        if (!validState) return@KeyboardActions
                        onValChange(totalBillState.value.trim())

                        keyboardController?.hide()
                    })

                Row(modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start){
                    Text(text = "Personas", modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    ))
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End){
                        RoundIconButton(modifier = Modifier,
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                splitByState.value =
                                    if(splitByState.value > 1) splitByState.value -1 else 1
                                totalPerPersonState.value =
                                    calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercentage = tipPercentage)
                            })

                        Text(text = "${splitByState.value}",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp))


                        RoundIconButton(modifier = Modifier,
                            imageVector = Icons.Default.Add,
                            onClick = { splitByState.value = splitByState.value +1

                                totalPerPersonState.value =
                                    calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercentage = tipPercentage)
                            })
                    }
                }

                Row (modifier = Modifier
                    .padding(horizontal = 3.dp, vertical = 12.dp)){
                    Text(text="Propina",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(200.dp))

                    Text(text = "${tipAmountState.value} €")
                }

                Column(verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$tipPercentage %")

                    Spacer(modifier = Modifier.height(14.dp))

                    Slider(value = sliderPositionState.value,
                        onValueChange = {newVal ->
                            sliderPositionState.value = newVal
                            tipAmountState.value =
                                calculateTotalTip(totalBill = totalBillState.value.toDouble(), tipPercentage = tipPercentage)

                            totalPerPersonState.value =
                                calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage)

                        },
                        modifier = Modifier.padding(start= 16.dp, end= 16.dp),
                        steps = 5,
                        onValueChangeFinished = {
                            //TODO
                        }
                    )
                }
            }

        }




    }




//@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DividirCuentaTheme {

    }
}