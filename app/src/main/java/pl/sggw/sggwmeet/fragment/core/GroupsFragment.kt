package pl.sggw.sggwmeet.fragment.core

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.sggw.sggwmeet.databinding.FragmentGroupsBinding

class GroupsFragment : Fragment() {

    private lateinit var binding: FragmentGroupsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentGroupsBinding.inflate(inflater, container, false)
        return this.binding.root
    }
}