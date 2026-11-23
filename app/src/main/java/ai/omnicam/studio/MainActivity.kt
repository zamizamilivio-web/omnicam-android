package ai.omnicam.studio

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.widget.*
import java.io.File
import java.io.FileOutputStream

class MainActivity : Activity() {
    private lateinit var preview: FrameLayout
    private lateinit var picture: ImageView
    private lateinit var video: VideoView
    private lateinit var caption: TextView
    private lateinit var status: TextView
    private var currentBitmap: Bitmap? = null
    private var selectedUri: Uri? = null
    private var videoMode = false
    private val PICK_IMAGE = 101
    private val PICK_VIDEO = 102
    private val TAKE_PHOTO = 103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 24, 20, 12)
            setBackgroundColor(Color.rgb(12, 18, 34))
        }
        val title = TextView(this).apply {
            text = "OmniCam AI 5.0"
            textSize = 27f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        root.addView(title)
        status = TextView(this).apply {
            text = "Android camera studio • Local-only preview"
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }
        root.addView(status)
        preview = FrameLayout(this).apply { setBackgroundColor(Color.BLACK) }
        root.addView(preview, LinearLayout.LayoutParams(-1, 0, 1f))
        picture = ImageView(this).apply { scaleType = ImageView.ScaleType.FIT_CENTER }
        video = VideoView(this)
        preview.addView(picture, FrameLayout.LayoutParams(-1, -1))
        preview.addView(video, FrameLayout.LayoutParams(-1, -1))
        caption = TextView(this).apply {
            text = "OMNICAM AI"
            textSize = 24f
            setTextColor(Color.WHITE)
            setShadowLayer(5f, 2f, 2f, Color.BLACK)
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            setPadding(10, 10, 10, 24)
        }
        preview.addView(caption, FrameLayout.LayoutParams(-1, -1))
        video.visibility = View.GONE
        fun row(vararg names: Pair<String, () -> Unit>) {
            val line = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            names.forEach { (name, action) ->
                val b = Button(this).apply { text = name; textSize = 12f; setOnClickListener { action() } }
                line.addView(b, LinearLayout.LayoutParams(0, -2, 1f))
            }
            root.addView(line)
        }
        row("Photo" to { pick("image/*", PICK_IMAGE) }, "Video" to { pick("video/*", PICK_VIDEO) }, "Camera" to {
            startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAKE_PHOTO)
        })
        row("Natural" to { effect(0) }, "Noir" to { effect(1) }, "Vintage" to { effect(2) })
        row("Text overlay" to { editCaption() }, "Save image" to { saveImage() })
        val note = TextView(this).apply {
            text = "Prototype: previews media inside OmniCam. It does NOT replace the camera in other apps."
            setTextColor(Color.LTGRAY)
            textSize = 12f
        }
        root.addView(note)
        setContentView(root)
    }

    private fun pick(type: String, code: Int) {
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            this.type = type
        }, code)
    }

    @Deprecated("Legacy result API for minimal dependency-free prototype")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        if (requestCode == TAKE_PHOTO) {
            val bitmap = data?.extras?.get("data") as? Bitmap ?: return
            currentBitmap = bitmap
            selectedUri = null
            showPicture(bitmap)
            return
        }
        val uri = data?.data ?: return
        selectedUri = uri
        if (requestCode == PICK_IMAGE) {
            try {
                val source = android.graphics.ImageDecoder.createSource(contentResolver, uri)
                val bitmap = android.graphics.ImageDecoder.decodeBitmap(source)
                currentBitmap = bitmap
                showPicture(bitmap)
            } catch (e: Exception) { status.text = "Unable to open image: ${e.message}" }
        } else if (requestCode == PICK_VIDEO) {
            currentBitmap = null
            videoMode = true
            picture.visibility = View.GONE
            video.visibility = View.VISIBLE
            video.setVideoURI(uri)
            video.setOnPreparedListener { player -> player.isLooping = true; video.start() }
            status.text = "Video source loaded"
        }
    }

    private fun showPicture(bitmap: Bitmap) {
        video.stopPlayback()
        video.visibility = View.GONE
        picture.visibility = View.VISIBLE
        picture.setImageBitmap(bitmap)
        videoMode = false
        status.text = "Image source loaded"
    }

    private fun effect(mode: Int) {
        val matrix = ColorMatrix()
        when (mode) {
            1 -> matrix.setSaturation(0f)
            2 -> matrix.set(floatArrayOf(
                .393f,.769f,.189f,0f,0f,
                .349f,.686f,.168f,0f,0f,
                .272f,.534f,.131f,0f,0f,
                0f,0f,0f,1f,0f))
        }
        picture.colorFilter = if (mode == 0) null else ColorMatrixColorFilter(matrix)
        status.text = if (videoMode) "Filters currently apply to images only" else "Image filter changed"
    }

    private fun editCaption() {
        val input = EditText(this).apply { setText(caption.text); setSingleLine() }
        android.app.AlertDialog.Builder(this).setTitle("Overlay text").setView(input)
            .setPositiveButton("Apply") { _, _ -> caption.text = input.text.toString().take(60) }
            .setNegativeButton("Cancel", null).show()
    }

    private fun saveImage() {
        if (videoMode || currentBitmap == null) { status.text = "Select an image first"; return }
        try {
            val image = Bitmap.createBitmap(preview.width, preview.height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(image)
            preview.draw(canvas)
            val file = File(getExternalFilesDir(null), "omnicam-${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { image.compress(Bitmap.CompressFormat.PNG, 100, it) }
            status.text = "Saved in app files: ${file.name}"
        } catch (e: Exception) { status.text = "Save failed: ${e.message}" }
    }

    override fun onPause() { super.onPause(); if (videoMode) video.pause() }
    override fun onResume() { super.onResume(); if (videoMode && selectedUri != null) video.start() }
}
