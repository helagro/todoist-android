package se.helagro.postmessenger
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import se.helagro.postmessenger.databinding.ActivityMainBinding
import se.helagro.postmessenger.network.NetworkCallback
import se.helagro.postmessenger.network.NetworkHandler
import se.helagro.postmessenger.settings.SettingsActivity
import se.helagro.postmessenger.taskhistory.TaskHistory
import se.helagro.postmessenger.taskitem.Destinations
import se.helagro.postmessenger.taskitem.Task

class MainActivity : AppCompatActivity() {
    private val taskHistory = TaskHistory()
    private var networkHandler: NetworkHandler = NetworkHandler()
    private lateinit var binding: ActivityMainBinding


    //=========== ENTRY POINTS ===========

    private val settingsLauncher = registerForActivityResult(StartActivityForResult()) { _: ActivityResult ->
        setupViews()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        Destinations.load(networkHandler)
        setupViews()
    }

    override fun onResume() {
        super.onResume()

        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        binding.inputField.requestFocus()
        inputMethodManager.showSoftInput(binding.inputField, InputMethodManager.SHOW_IMPLICIT)
    }


    private fun goToSettings(){
        settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
    }


    //========== VIEW SETUP ==========

    private fun setupViews(){
        //INPUT_FIELD
        focusOnInputField()
        binding.inputField.addTextChangedListener(HideCursor(binding.inputField))
        binding.sendBtn.setOnClickListener {
            val textInput = binding.inputField.text.toString()
            val newTask = Task(textInput)
            taskHistory.add(newTask)

            networkHandler.postTask(newTask, object : NetworkCallback {
                override fun onUpdate(code: Int, body: String?) {
                    taskHistory.alertListeners()
                }
            })
            binding.inputField.setText("")
        }

        //POST_LIST
        val postListAdapter = TaskHistoryListAdapter(this, taskHistory)
        binding.postLogList.adapter = postListAdapter
        taskHistory.addListener(postListAdapter)
    }

    private fun focusOnInputField(){
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.inputField, InputMethodManager.SHOW_IMPLICIT)
    }


    //=========== MENU ===========

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) goToSettings()
        else if (item.itemId == R.id.action_clear) taskHistory.clear()
        return true
    }



    override fun onDestroy() {
        super.onDestroy()
        (binding.postLogList.adapter as TaskHistoryListAdapter?)?.let { taskHistory.removeListener(it) }
    }
}