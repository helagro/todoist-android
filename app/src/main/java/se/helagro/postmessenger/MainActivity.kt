package se.helagro.postmessenger
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import se.helagro.postmessenger.network.NetworkHandler
import se.helagro.postmessenger.taskhistory.TaskHistory
import se.helagro.postmessenger.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    val taskHistory = TaskHistory()
    private var networkHandler: NetworkHandler? = null


    //=========== ENTRY POINTS ===========

    private val settingsLauncher = registerForActivityResult(StartActivityForResult()) { _: ActivityResult ->
        doSetup()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doSetup()
    }


    private fun doSetup(){
        val didSucceed = initPostHandler()
        if(didSucceed){
            setupViews()
        }
    }

    private fun initPostHandler(): Boolean{
        val postHandlerEndpoint = NetworkHandler.getEndpoint()
        if(postHandlerEndpoint == null) {
            goToSettings()
            return false
        } else if(!URLUtil.isValidUrl(postHandlerEndpoint)){
            Toast.makeText(this, "Invalid endpoint URL", Toast.LENGTH_LONG).show()
            goToSettings()
            return false
        }

        networkHandler = NetworkHandler()
        return true
    }

    private fun goToSettings(){
        settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
    }


    //========== VIEW SETUP ==========

    private fun setupViews(){
        //INPUT_FIELD
        focusOnInputField()
        val inputFieldListener = InputFieldListener(networkHandler!!, taskHistory)
        inputField.setOnEditorActionListener(inputFieldListener)

        //POST_LIST
        val postListAdapter = TaskHistoryListAdapter(this, taskHistory)
        postLogList.adapter = postListAdapter
        taskHistory.addListener(postListAdapter)
    }

    private fun focusOnInputField(){
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(inputField, InputMethodManager.SHOW_IMPLICIT)
    }


    //=========== MENU ===========

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        goToSettings()
        return true
    }



    override fun onDestroy() {
        super.onDestroy()
        (postLogList.adapter as TaskHistoryListAdapter?)?.let { taskHistory.removeListener(it) }
    }
}