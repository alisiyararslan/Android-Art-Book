package com.alisiyararslan.artbooknavigationkotlin.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.alisiyararslan.artbooknavigationkotlin.R
import com.alisiyararslan.artbooknavigationkotlin.adapter.ArtAdapter
import com.alisiyararslan.artbooknavigationkotlin.databinding.FragmentDetailBinding
import com.alisiyararslan.artbooknavigationkotlin.databinding.FragmentListBinding
import com.alisiyararslan.artbooknavigationkotlin.model.Art
import com.alisiyararslan.artbooknavigationkotlin.roomdb.ArtDao
import com.alisiyararslan.artbooknavigationkotlin.roomdb.ArtDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    private lateinit var db: ArtDatabase
    private lateinit var artDao: ArtDao

    private var compositeDisposible= CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        db= Room.databaseBuilder(requireContext(),ArtDatabase::class.java,"Arts").build()
        artDao=db.artDao()

        compositeDisposible.add(
            artDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )



    }

    fun handleResponse(artList:List<Art>){
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        val adapter=ArtAdapter(artList)
        binding.recyclerView.adapter=adapter

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun aa(view:View){

    }


}