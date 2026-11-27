package com.autoclick.pastepro

import android.app.Activity
import android.os.Bundle
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.widget.*
import android.view.Gravity

class MainActivity : Activity() {

    private lateinit var textInput: EditText
    private lateinit var status: TextView
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(
            "AutoPasteSettings", MODE_PRIVATE
        )

        currentIndex = prefs.getInt("index", 0)

        val scroll = ScrollView(this)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 64, 32, 32)
        }

        val title = TextView(this).apply {
            text = "AutoClick Paste Pro"
            textSize = 26f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        }

        layout.addView(title)

        val description = TextView(this).apply {
            text = "Enter each saved message on a separate line."
            textSize = 16f
            setPadding(0, 24, 0, 16)
        }

        layout.addView(description)

        textInput = EditText(this).apply {
            hint = "Message 1\nMessage 2\nMessage 3"
            minLines = 6
            gravity = Gravity.TOP
            setText(prefs.getString("messages", ""))
        }

        layout.addView(textInput)

        status = TextView(this).apply {
            text = "Ready"
            textSize = 16f
            setPadding(0, 24, 0, 24)
        }

        val saveButton = Button(this).apply {
            text = "Save Messages"

            setOnClickListener {
                currentIndex = 0

                prefs.edit()
                    .putString("messages", textInput.text.toString())
                    .putInt("index", 0)
                    .apply()

                status.text = "Messages saved"

                Toast.makeText(
                    this@MainActivity,
                    "Saved successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        layout.addView(saveButton)

        val nextButton = Button(this).apply {
            text = "Copy Next Message"

            setOnClickListener {

                val messages = prefs
                    .getString("messages", "")
                    .orEmpty()
                    .lines()
                    .filter { it.isNotBlank() }

                if (messages.isEmpty()) {
                    status.text = "Save messages first"
                    return@setOnClickListener
                }

                currentIndex %= messages.size

                val clipboard = getSystemService(
                    Context.CLIPBOARD_SERVICE
                ) as ClipboardManager

                clipboard.setPrimaryClip(
                    ClipData.newPlainText(
                        "Saved message",
                        messages[currentIndex]
                    )
                )

                status.text =
                    "Copied message ${currentIndex + 1} of ${messages.size}"

                currentIndex =
                    (currentIndex + 1) % messages.size

                prefs.edit()
                    .putInt("index", currentIndex)
                    .apply()
            }
        }

        layout.addView(nextButton)

        val resetButton = Button(this).apply {
            text = "Reset Sequence"

            setOnClickListener {
                currentIndex = 0
                prefs.edit().putInt("index", 0).apply()
                status.text = "Sequence reset"
            }
        }

        layout.addView(resetButton)
        layout.addView(status)

        scroll.addView(layout)
        setContentView(scroll)
    }
}
