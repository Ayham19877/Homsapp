package com.example.homsapp

import android.accessibilityservice.AccessibilityService
import android.media.MediaPlayer
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MyAccessibilityService : AccessibilityService() {

    companion object {
        var filterTime: Int = 0
        var filterAmount: Float = 0f
        var soundRes: Int = 0
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return

        if (packageName.contains("shipt") || packageName.contains("zifty")) {
            val rootNode = rootInActiveWindow ?: return

            // مثال: البحث عن زر "Claim this task"
            val claimButtons = rootNode.findAccessibilityNodeInfosByText("Claim this task")
            if (claimButtons.isNotEmpty()) {
                // هنا ممكن تضيف فلترة الوقت والمبلغ حسب النصوص المعروضة، لو في طريقة لقراءة السعر والوقت
                // اذا تحقق الشرط:
                claimButtons[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                playNotificationSound()
            }
        }
    }

    private fun playNotificationSound() {
        val mediaPlayer = MediaPlayer.create(this, soundRes)
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
        mediaPlayer.start()
    }

    override fun onInterrupt() {}
}
