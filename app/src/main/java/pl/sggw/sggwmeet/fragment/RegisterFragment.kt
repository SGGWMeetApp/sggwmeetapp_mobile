package pl.sggw.sggwmeet.fragment

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import pl.sggw.sggwmeet.R


class RegisterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }
    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        val tosTextView = view.findViewById<View>(R.id.reg_linkTos) as TextView
        textViewOpenLink(view,savedInstanceState,tosTextView)
    }

    /**
     * Pozwala otworzyć linki z TextView w przeglądarce.
     */
    private fun textViewOpenLink(view: View, @Nullable savedInstanceState: Bundle?, textView: TextView){
        super.onViewCreated(view, savedInstanceState)
        textView.movementMethod=LinkMovementMethod.getInstance()
    }
}