import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AlertDialog

object ConfirmDialogUtils {

    fun showConfirmDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = "Yes",
        negativeButtonText: String = "No",
        onPositiveAction: (() -> Unit)? = null,
        onNegativeAction: (() -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        // Mengatur warna teks tombol positif
        val positiveText = SpannableString(positiveButtonText)
        positiveText.setSpan(ForegroundColorSpan(Color.BLACK), 0, positiveText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Mengatur warna teks tombol negatif
        val negativeText = SpannableString(negativeButtonText)
        negativeText.setSpan(ForegroundColorSpan(Color.BLACK), 0, negativeText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        builder.setPositiveButton(positiveText) { dialog, _ ->
            onPositiveAction?.invoke()
            dialog.dismiss()
        }
        builder.setNegativeButton(negativeText) { dialog, _ ->
            onNegativeAction?.invoke()
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}
