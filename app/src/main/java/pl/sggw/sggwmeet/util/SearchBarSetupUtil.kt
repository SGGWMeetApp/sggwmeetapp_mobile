package pl.sggw.sggwmeet.util

import android.graphics.Typeface
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.mancj.materialsearchbar.MaterialSearchBar

class SearchBarSetupUtil {
    companion object {
        fun setFontFamily(searchBar: MaterialSearchBar, typeFace: Typeface?){
            val constraintLayout = ((searchBar.getChildAt(0) as CardView)?.getChildAt(0) as? ConstraintLayout)
            val placeholderTextView = constraintLayout?.getChildAt(1) as AppCompatTextView
            val editTextView = (constraintLayout?.getChildAt(2) as LinearLayout)?.getChildAt(1) as AppCompatEditText
            placeholderTextView.typeface = typeFace
            editTextView.typeface = typeFace
        }
    }
}