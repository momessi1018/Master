class MainActivity : AppCompatActivity() {
    private lateinit var alarmSpinner: Spinner
    private lateinit var btnAddDevice: Button
    private lateinit var btnArm: Button
    private lateinit var btnDisarm: Button

    private val deviceList = mutableListOf<AlarmDevice>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        alarmSpinner = findViewById(R.id.alarmSpinner)
        btnAddDevice = findViewById(R.id.btnAddDevice)
        btnArm = findViewById(R.id.btnArm)
        btnDisarm = findViewById(R.id.btnDisarm)

        // Setup spinner adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf<String>())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        alarmSpinner.adapter = adapter

        // Load saved devices
        loadDevices()

        // Button click listeners
        btnAddDevice.setOnClickListener {
            val intent = Intent(this, AddDeviceActivity::class.java)
            startActivityForResult(intent, ADD_DEVICE_REQUEST)
        }

        btnArm.setOnClickListener {
            sendCommand("ARM")
        }

        btnDisarm.setOnClickListener {
            sendCommand("DISARM")
        }
    }

    private fun loadDevices() {
        // Here you would load from SharedPreferences or Database
        // This is a simplified version
        deviceList.clear()
        adapter.clear()

        // Example device (remove in production)
        deviceList.add(AlarmDevice("دزدگیر من", "09123456789", "1234"))
        adapter.add("دزدگیر من - 09123456789")
    }

    private fun sendCommand(command: String) {
        if (deviceList.isEmpty()) {
            Toast.makeText(this, "لطفاً ابتدا یک دستگاه اضافه کنید", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedDevice = deviceList[alarmSpinner.selectedItemPosition]
        val message = "${selectedDevice.password}$command"

        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(
                selectedDevice.phoneNumber,
                null,
                message,
                null,
                null
            )
            Toast.makeText(this, "دستور ارسال شد", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "خطا در ارسال پیام: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_DEVICE_REQUEST && resultCode == RESULT_OK) {
            loadDevices() // Refresh the list
        }
    }

    companion object {
        const val ADD_DEVICE_REQUEST = 1
    }
}

data class AlarmDevice(
    val name: String,
    val phoneNumber: String,
    val password: String
)