package org.hyperskill.simplebankmanager

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import java.io.ByteArrayInputStream

import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.Base64

@RequiresApi(Build.VERSION_CODES.O)
fun serializeMap(map: Map<String, Map<String, Double>>): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
    objectOutputStream.writeObject(map)
    objectOutputStream.flush()
    return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
}

@RequiresApi(Build.VERSION_CODES.O)
fun serializeBillInfoMap(map: Map<String, Triple<String, String, Double>>): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
    objectOutputStream.writeObject(map)
    objectOutputStream.flush()
    return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
}


@RequiresApi(Build.VERSION_CODES.O)
fun deserializeMap(serialized: String): Map<String, Map<String, Double>>? {
    val byteArray = Base64.getDecoder().decode(serialized)
    val byteArrayInputStream = ByteArrayInputStream(byteArray)
    val objectInputStream = ObjectInputStream(byteArrayInputStream)
    return objectInputStream.readObject() as? Map<String, Map<String, Double>>
}

@RequiresApi(Build.VERSION_CODES.O)
fun deserializeBillInfoMap(serialized: String): Map<String, Triple<String, String, Double>>? {
    val byteArray = Base64.getDecoder().decode(serialized)
    val byteArrayInputStream = ByteArrayInputStream(byteArray)
    val objectInputStream = ObjectInputStream(byteArrayInputStream)
    return objectInputStream.readObject() as? Map<String, Triple<String, String, Double>>
}


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
            ?: throw IllegalStateException("NavHostFragment with ID 'nav_host_fragment' not found")

        navController = navHostFragment.navController

        val username = intent.extras?.getString("username") ?: "Lara"
        val password = intent.extras?.getString("password") ?: "1234"
        val balance = intent.extras?.getDouble("balance")?.toString() ?: "100.0"


        val defaultExchangeMap = mapOf(
            "EUR" to mapOf(
                "GBP" to 0.5,
                "USD" to 2.0
            ),
            "GBP" to mapOf(
                "EUR" to 2.0,
                "USD" to 4.0
            ),
            "USD" to mapOf(
                "EUR" to 0.5,
                "GBP" to 0.25
            )
        )

        val defaultBillInfoMap = mapOf(
            "ELEC" to Triple("Electricity", "ELEC", 45.0),
            "GAS" to Triple("Gas", "GAS", 20.0),
            "WTR" to Triple("Water", "WTR", 25.5)
        )



        // Retrieve exchangeMap from Intent extras
        val retrievedMap = intent.extras?.getSerializable("exchangeMap") as? Map<String, Map<String, Double>>

        // Use the retrieved map if available, otherwise use the default
        val exchangeMap = retrievedMap ?: defaultExchangeMap
        val exchangeMapString = serializeMap(exchangeMap)



        // Retrieve exchangeMap from Intent extras
        val retrievedMapBill = intent.extras?.getSerializable("billInfo") as? Map<String, Triple<String, String, Double>>

        // Use the retrieved map if available, otherwise use the default
        val billMap = retrievedMapBill ?: defaultBillInfoMap
        val billMapString = serializeBillInfoMap(billMap)



        // Create a Bundle and set it as arguments to the LoginFragment
        val bundle = Bundle().apply {
            putString("username", username)
            putString("password", password)
            putString("balance", balance)
            putString("exchangeMap", exchangeMapString)
            putString("billMap", billMapString)
        }

        // Navigate to LoginFragment with arguments
        navController.navigate(R.id.loginFragment, bundle)
    }
}
