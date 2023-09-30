package se.helagro.postmessenger.settings

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings.*
import se.helagro.postmessenger.R


class SettingsActivity: AppCompatActivity() {

    private val storageHandler = StorageHandler.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val actionBar: ActionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        fillInputFields()

        doneBtn.setOnClickListener {
            saveSettings()
            Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show()
        }
    }

    private fun fillInputFields(){
        apiKeyInput.setText(storageHandler.getString(SettingsID.API_KEY)?: "")
    }

    //return-button in actionBar onClick
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private fun saveSettings(){
        storageHandler.setString(SettingsID.API_KEY, apiKeyInput.text.toString())
    }
}