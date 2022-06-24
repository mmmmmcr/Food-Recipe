package com.app.food.ui.fragments.instructions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.app.food.databinding.FragmentInstructionsBinding
import com.app.food.models.Result
import com.app.food.util.Constants

class InstructionsFragment : Fragment() {

    private var initialBinding: FragmentInstructionsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.initialBinding = FragmentInstructionsBinding.inflate(inflater, container, false)

        val args = arguments
        val bundle: Result? = args?.getParcelable(Constants.RECIPE_RESULT_KEY)

        this.initialBinding!!.instructionsWebView.webViewClient = object : WebViewClient() {}
        val websiteUrl: String = bundle!!.sourceUrl
        this.initialBinding!!.instructionsWebView.loadUrl(websiteUrl)

        return this.initialBinding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.initialBinding = null
    }
}