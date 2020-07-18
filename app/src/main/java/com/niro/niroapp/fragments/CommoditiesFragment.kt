package com.niro.niroapp.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.niro.niroapp.R
import com.niro.niroapp.viewmodels.CommoditiesViewModel

class CommoditiesFragment : Fragment() {

    companion object {
        fun newInstance() = CommoditiesFragment()
    }

    private lateinit var viewModel: CommoditiesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.commodities_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CommoditiesViewModel::class.java)

    }

}