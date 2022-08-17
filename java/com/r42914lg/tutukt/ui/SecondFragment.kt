package com.r42914lg.tutukt.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.r42914lg.tutukt.R
import com.r42914lg.tutukt.databinding.FragmentSecondBinding
import com.r42914lg.tutukt.domain.CategoryDetailed
import com.r42914lg.tutukt.model.TuTuViewModel

class SecondFragment : Fragment(), IDetailsView {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val tuTuViewModel: TuTuViewModel =
            ViewModelProvider(requireActivity())[TuTuViewModel::class.java]

        val controller = DetailsController(tuTuViewModel, this)
        controller.initDetailsView(this)

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("DefaultLocale")
    override fun showDetails(categoryDetailed: CategoryDetailed) {
        binding.detailCategoryId.text = String.format(
                "%s%d",
                getString(R.string.category_id_prefix),
                categoryDetailed.id)

        binding.detailCategoryTitle.text = String.format(
            "%s%s",
            getString(R.string.category_title_prefix),
            categoryDetailed.title)

        binding.detailCluesCount.text = String.format(
            "%s%d",
            getString(R.string.category_clue_count),
            categoryDetailed.cluesCount)

        val buffer = StringBuffer()
        categoryDetailed.clues.forEach {
                clue -> buffer.append(clue.asString())
        }
        binding.detailClues.text = buffer
    }
}