package com.example.homsapp

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var filterTime: EditText
    private lateinit var filterAmount: EditText
    private lateinit var soundGroup: RadioGroup
    private lateinit var startBtn: Button
    private lateinit var stopBtn: Button

    private var mediaPlayer: MediaPlayer? = null
    private var selectedSoundRes = R.raw.bravo_abou_ayham // لازم تضيف ملفات الصوت في res/raw

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        filterTime = findViewById(R.id.filterTime)
        filterAmount = findViewById(R.id.filterAmount)
        soundGroup = findViewById(R.id.soundGroup)
        startBtn = findViewById(R.id.startServiceBtn)
        stopBtn = findViewById(R.id.stopServiceBtn)

        soundGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedSoundRes = when (checkedId) {
                R.id.soundBravo -> R.raw.bravo_abou_ayham
                R.id.soundMa3lem -> R.raw.enta_ma3lem
                else -> R.raw.bravo_abou_ayham
            }
        }

        startBtn.setOnClickListener {
            if (!isAccessibilityServiceEnabled()) {
                Toast.makeText(this, "يرجى تفعيل خدمة الوصول", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                return@setOnClickListener
            }

            // قراءة الفلاتر وحفظها في SharedPreferences أو في متغيرات مشتركة مع AccessibilityService
            val time = filterTime.text.toString().toIntOrNull() ?: 0
            val amount = filterAmount.text.toString().toFloatOrNull() ?: 0f

            MyAccessibilityService.filterTime = time
            MyAccessibilityService.filterAmount = amount
            MyAccessibilityService.soundRes = selectedSoundRes

            Toast.makeText(this, "تم تشغيل الخدمة", Toast.LENGTH_SHORT).show()
            startBtn.isEnabled = false
            stopBtn.isEnabled = true
        }

        stopBtn.setOnClickListener {
            // لإيقاف الخدمة: للأسف لا يمكن إيقاف Accessibility Service برمجيًا، على المستخدم إيقافها من الإعدادات
            Toast.makeText(this, "لإيقاف الخدمة، يرجى إيقافها من إعدادات الوصول", Toast.LENGTH_LONG).show()
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val am = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (service in enabledServices) {
            if (service.resolveInfo.serviceInfo.packageName == packageName
                && service.resolveInfo.serviceInfo.name == MyAccessibilityService::class.java.name) {
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
