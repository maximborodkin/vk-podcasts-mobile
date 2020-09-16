package ru.maxim.vk.podcasts

import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import kotlinx.android.synthetic.main.activity_new_podcast.*
import java.io.InputStream
import java.util.*


class NewPodcastActivity : AppCompatActivity() {
    
    private val imagePickerRequestCode = 123
    private val audioPickerRequestCode = 124
    private var isPodcastUploaded = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_podcast)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        newPodcastChange.visibility = View.GONE
        
        newPodcastCover.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, imagePickerRequestCode)
        }
        
        val accessibilityTypes = arrayListOf(
            "All users",
            "Only me",
            "Only friends",
            "Friends and friends of friends"
        )
        val accessibilityTypesDescriptions = arrayListOf(
            "When you publish a recording with an episode, it becomes available to all users",
            "When you publish a recording with an episode, it becomes available to you",
            "When you publish a recording with an episode, it becomes available to all your friends",
            "When you publish a recording with an episode, it becomes available to your friends and their friends"
        )
        val arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            accessibilityTypes
        )
        newPodcastAccessibilitySpinner.adapter = arrayAdapter
        newPodcastAccessibilitySpinner.setSelection(0)
        newPodcastAccessibilitySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                newPodcastAccessibilityDescription.text = accessibilityTypesDescriptions[position]
            }
    
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        newPodcastUploadBtn.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            photoPickerIntent.type = "audio/mpeg"
            startActivityForResult(photoPickerIntent, audioPickerRequestCode)
        }
        newPodcastChangeBtn.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            photoPickerIntent.type = "audio/mpeg"
            startActivityForResult(photoPickerIntent, audioPickerRequestCode)
        }
        newPostNextBtn.setOnClickListener {
            newPodcastName.error =
                if (newPodcastName.text.isNullOrBlank()) getString(R.string.this_field_is_required)
                else null
            if (!isPodcastUploaded) Toast.makeText(this, "Please select audio file", Toast.LENGTH_SHORT).show()
            if (newPodcastName.error == null && isPodcastUploaded)
                startActivity(Intent(this, EditActivity::class.java))
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imagePickerRequestCode && resultCode == RESULT_OK) {
            try {
                val imageUri: Uri = data?.data?:return
                val imageStream: InputStream? = contentResolver.openInputStream(imageUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                newPodcastCover.setImageBitmap(selectedImage)
            } catch (ignored: Exception) { }
        }
        if (requestCode == audioPickerRequestCode && resultCode == RESULT_OK) {
            try {
                val audioUri = data?.data ?: return
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(this, audioUri)
                val recordLength = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()?:0L
                val calendar = Calendar.getInstance().apply { timeInMillis = recordLength }
    
                newPodcastChangeName.text = queryName(audioUri)
                val time = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
                newPodcastChangeLength.text = time
    
                newPodcastUpload.visibility = View.GONE
                newPodcastChange.visibility = View.VISIBLE
                isPodcastUploaded = true
            } catch (ignored: Exception) { }
        }
    }
    
    private fun queryName(uri: Uri): String? {
        val returnCursor = contentResolver.query(uri, null, null, null, null)?:return ""
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }
}