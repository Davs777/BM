package org.hyperskill.simplebankmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class LoginFragment : Fragment() {

    private var username: String? = null
    private var password: String? = null
    private var balance: String? = null
    private var exchangeMap: String? = null
    private var billMap: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString("username")
            password = it.getString("password")
            balance = it.getString("balance")
            exchangeMap = it.getString("exchangeMap")
            billMap = it.getString("billMap")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val loginUsername = view.findViewById<EditText>(R.id.loginUsername)
        val loginPassword = view.findViewById<EditText>(R.id.loginPassword)
        val loginButton = view.findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val enteredUsername = loginUsername.text.toString()
            val enteredPassword = loginPassword.text.toString()

            if (enteredUsername == username && enteredPassword == password) {
                Toast.makeText(activity, "logged in", Toast.LENGTH_SHORT).show()

                val bundle = Bundle().apply {
                    putString("username", username)
                    putString("balance", balance)
                    putString("exchangeMap", exchangeMap)
                    putString("billMap", billMap)
                }

                findNavController().navigate(R.id.action_loginFragment_to_userMenuFragment, bundle)
            } else {
                Toast.makeText(activity, "invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
