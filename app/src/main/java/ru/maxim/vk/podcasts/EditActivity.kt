package ru.maxim.vk.podcasts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity() {
    
    private var isFadeInOn = false
    private var isFadeOutOn = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        editFadeInBtn.setOnClickListener {
            editFadeInBtn.setBackgroundResource(
                if (isFadeInOn) R.drawable.fade_in_background
                else R.drawable.fade_in_selected_background)
            isFadeInOn = !isFadeInOn
        }
        editFadeOutBtn.setOnClickListener {
            editFadeOutBtn.setBackgroundResource(
                if (isFadeOutOn) R.drawable.fade_out_background
                else R.drawable.fade_out_selected_background)
            isFadeOutOn = !isFadeOutOn
        }
    }
}