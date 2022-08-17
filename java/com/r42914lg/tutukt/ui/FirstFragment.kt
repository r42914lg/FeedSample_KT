package com.r42914lg.tutukt.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.r42914lg.tutukt.Constants
import com.r42914lg.tutukt.databinding.FragmentFirstBinding
import com.r42914lg.tutukt.domain.Category
import com.r42914lg.tutukt.model.TuTuViewModel

class FirstFragment : Fragment(), IFeedView {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private lateinit var controller: FeedController
    private lateinit var adapter: FeedAdapter
    private val categoryList = ArrayList<Category>(Constants.CATEGORIES_TO_RETURN)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val tuTuViewModel: TuTuViewModel =
            ViewModelProvider(requireActivity())[TuTuViewModel::class.java]

        controller = FeedController(tuTuViewModel, this)
        controller.initFeedView(this)

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        binding.feedRecycler.layoutManager = LinearLayoutManager(container!!.context)
        adapter = FeedAdapter(categoryList)
        binding.feedRecycler.adapter = adapter

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.feedRecycler.addOnItemTouchListener(
            RecyclerItemClickListener(
                requireContext(),
                binding.feedRecycler,
                object : OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        controller.onDetailsRequested(position)
                    }
                    override fun onLongItemClick(view: View?, position: Int) {}
                })
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showFeed(clues: List<Category>) {
        categoryList.clear()
        categoryList.addAll(clues)

        adapter.notifyDataSetChanged()
    }
}