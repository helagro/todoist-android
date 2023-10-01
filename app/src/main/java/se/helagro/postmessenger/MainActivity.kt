package se.helagro.postmessenger
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.inputField
import kotlinx.android.synthetic.main.activity_main.postLogList
import se.helagro.postmessenger.network.NetworkHandler
import se.helagro.postmessenger.network.NetworkHandlerListener
import se.helagro.postmessenger.settings.SettingsActivity
import se.helagro.postmessenger.taskhistory.TaskHistory

class MainActivity : AppCompatActivity() {
    private val taskHistory = TaskHistory()
    private var networkHandler: NetworkHandler = NetworkHandler()


    //=========== ENTRY POINTS ===========

    private val settingsLauncher = registerForActivityResult(StartActivityForResult()) { _: ActivityResult ->
        setupViews()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        networkHandler.getProjects(object : NetworkHandlerListener {
            override fun onUpdate(code: Int) {

            }
        }) */

        setupViews()
    }

    override fun onResume() {
        super.onResume()

        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputField.requestFocus()
        inputMethodManager.showSoftInput(inputField, InputMethodManager.SHOW_IMPLICIT)
    }


    private fun goToSettings(){
        settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
    }


    //========== VIEW SETUP ==========

    private fun setupViews(){
        //INPUT_FIELD
        focusOnInputField()
        val inputFieldListener = InputFieldListener(networkHandler, taskHistory)
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