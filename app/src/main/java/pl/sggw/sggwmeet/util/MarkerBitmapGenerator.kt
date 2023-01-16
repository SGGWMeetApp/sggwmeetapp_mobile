package pl.sggw.sggwmeet.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.domain.PlaceMarkerData
import pl.sggw.sggwmeet.domain.UserData

class MarkerBitmapGenerator(
    private val context : Context
) {

    fun generateUserBitmap(user: UserData): Bitmap {
        val markerView = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.marker_place, null)
        val markerImage = markerView.findViewById<AppCompatImageView>(R.id.marker_IV)
        val markerTitle = markerView.findViewById<TextView>(R.id.marker_TV)
        markerImage.setImageResource(R.drawable.asset_marker_app_user)
        markerTitle.text = "${user.firstName} ${user.lastName}"

        return this.generateBitmap(markerView)
    }

    fun generatePlaceBitmap(place : PlaceMarkerData) : Bitmap {
        val markerView = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.marker_place, null)
        val markerImage = markerView.findViewById<AppCompatImageView>(R.id.marker_IV)
        val markerTitle = markerView.findViewById<TextView>(R.id.marker_TV)
        setDrawableBasedOnCategory(markerImage, place.category)
        markerTitle.text = place.name

        return this.generateBitmap(markerView)
    }

    private fun generateBitmap(markerView: View): Bitmap {
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight);
        val bitmap = Bitmap.createBitmap(markerView.measuredWidth, markerView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        markerView.draw(canvas)
        return bitmap
    }

    private fun setDrawableBasedOnCategory(image : AppCompatImageView,  category : PlaceCategory) {
        when(category) {
            PlaceCategory.ROOT_LOCATION -> {
                image.setImageResource(R.drawable.asset_marker_root)
            }
            PlaceCategory.RESTAURANT -> {
                image.setImageResource(R.drawable.asset_marker_restaurant)
            }
            PlaceCategory.GYM -> {
                image.setImageResource(R.drawable.asset_marker_gym)
            }
            PlaceCategory.BAR -> {
                image.setImageResource(R.drawable.asset_marker_pub)
            }
            else -> {
                image.setImageResource(R.drawable.asset_marker_other)
            }
        }
    }
}