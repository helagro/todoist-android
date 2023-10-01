package se.helagro.postmessenger.settings

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import se.helagro.postmessenger.databinding.SettingsBinding


class SettingsActivity: AppCompatActivity() {

    private lateinit var binding: SettingsBinding
    private val storageHandler = StorageHandler.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val actionBar: ActionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        fillInputFields()

        binding.doneBtn.setOnClickListener {
            saveSettings()
            Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show()
        }
    }

    private fun fillInputFields(){
        binding.apiKeyInput.setText(storageHandler.getString(SettingsID.API_KEY)?: "")
    }

    //return-button in actionBar onClick
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private fun saveSettings(){
        storageHandler.setString(SettingsID.API_KEY, binding.apiKeyInput.text.toString())
    }
}