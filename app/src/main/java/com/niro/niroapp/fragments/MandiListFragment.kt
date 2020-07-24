package com.niro.niroapp.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.niro.niroapp.R
import com.niro.niroapp.viewmodels.MandiListViewModel

class MandiListFragment : Fragment() {

    companion object {
        fun newInstance() = MandiListFragment()
    }

    private lateinit var viewModel: MandiListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mandi_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MandiListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}