package com.sample.myweather.ui

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.myweather.R
import com.sample.myweather.adapter.LocationAdapter
import com.sample.myweather.repo.RepositoryImpl
import com.sample.myweather.utils.ExtUtils
import kotlinx.android.synthetic.main.layout_progress.progress_view
import kotlinx.android.synthetic.main.location_fragment.list_recycler_view
import kotlinx.android.synthetic.main.location_fragment.search_city_btn
import kotlinx.android.synthetic.main.location_fragment.search_city_editText


class LocationFragment : BaseFragment() {

    private var locationAdapter: LocationAdapter? = null
    private val viewModel: LocationViewModel by lazy {
        obtainViewModel{
            LocationViewModel(RepositoryImpl())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.location_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initViewModel()

    }

    private fun initViewModel() {
        viewModel.preferredLocation.observe(viewLifecycleOwner, Observer {
            if(it != null && it != 0) {
                viewModel.clearList()
                ExtUtils.saveWeatherLoc(requireContext(), it)
                findNavController().popBackStack()
            }
        })
        viewModel.showProgress.observe(viewLifecycleOwner, Observer {
            it?.let{
                if(it)
                    progress_view.visibility = View.VISIBLE
                else
                    progress_view.visibility = View.GONE
            }
        })
        viewModel.dialog.observe(viewLifecycleOwner, Observer {
            it?.let {
                showMessageDialog(it)
            }
        })
        viewModel.locations.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it.isNullOrEmpty())
                    Toast.makeText(requireContext(), "Sorry. No results available for the query.", Toast.LENGTH_LONG).show()
                if(locationAdapter == null) {
                    list_recycler_view.layoutManager = LinearLayoutManager(requireContext())
                    locationAdapter = LocationAdapter(viewModel, it)
                    list_recycler_view.adapter = locationAdapter
                }
                else{
                    locationAdapter?.updateData(it)
                }
            }
        })
    }

    private fun initUi() {
        search_city_btn.setOnClickListener {
            if(search_city_editText.text.toString().isNotEmpty())
                fetchLocationData(search_city_editText.text.toString())
        }
        search_city_editText.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH &&
                    textView.text.toString().isNotEmpty()) {
                fetchLocationData(textView.text.toString())
                true
            }
            else
                false
        }
    }

    private fun fetchLocationData(location: String) {
        hideKeyboard()
        viewModel.fetchLocation(location)
    }

    private fun hideKeyboard() {
        activity?.currentFocus.let {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(it?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

}
