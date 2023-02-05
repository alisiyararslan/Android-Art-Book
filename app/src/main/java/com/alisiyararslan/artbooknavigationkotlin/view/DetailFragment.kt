package com.alisiyararslan.artbooknavigationkotlin.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.room.Room
import com.alisiyararslan.artbooknavigationkotlin.R
import com.alisiyararslan.artbooknavigationkotlin.databinding.FragmentDetailBinding
import com.alisiyararslan.artbooknavigationkotlin.model.Art
import com.alisiyararslan.artbooknavigationkotlin.roomdb.ArtDao
import com.alisiyararslan.artbooknavigationkotlin.roomdb.ArtDatabase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.security.Permission


class DetailFragment : Fragment() {


    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap:Bitmap?=null
    private lateinit var db:ArtDatabase
    private lateinit var artDao: ArtDao

    private var compositeDisposible=CompositeDisposable()


    private var _binding: FragmentDetailBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        registerLauncher()

        db= Room.databaseBuilder(requireContext(),ArtDatabase::class.java,"Arts").build()
        artDao=db.artDao()

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            val action=DetailFragmentDirections.actionDetailFragmentToListFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        compositeDisposible.clear()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.imageView.setOnClickListener{
            selectImage(it)
        }

        binding.saveButton.setOnClickListener{
            save(it)
        }

        binding.deleteButton.setOnClickListener{
            delete(it)
        }


        arguments?.let {

            val info=DetailFragmentArgs.fromBundle(it).info
            if (info.equals("new")){
                binding.saveButton.visibility=View.VISIBLE
                binding.deleteButton.visibility=View.GONE

                binding.editTextArtName.setText("")
                binding.editTextArtistName.setText("")
                binding.editTextYear.setText("")

                val selectedImageBackground=BitmapFactory.decodeResource(context?.resources,R.drawable.selectimage)
                binding.imageView.setImageBitmap(selectedImageBackground)

            }else{
                val id=DetailFragmentArgs.fromBundle(it).id

                binding.saveButton.visibility=View.GONE
                binding.deleteButton.visibility=View.VISIBLE

                compositeDisposible.add(
                    artDao.getArtById(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponseOldArt)

                )

            }
        }



    }


    fun handleResponseOldArt(art:Art){
        binding.editTextArtName.setText(art.artName)
        binding.editTextArtistName.setText(art.artistName)
        binding.editTextYear.setText(art.year)

        art.image?.let{
            val bitmap=BitmapFactory.decodeByteArray(it,0,it.size)
            binding.imageView.setImageBitmap(bitmap)

        }

    }












    fun selectImage(view :View){//selectImage
        activity?.let {
        if (ContextCompat.checkSelfPermission(requireActivity().applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PackageManager.PERMISSION_GRANTED)
                }).show()
            }else{
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                //requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PackageManager.PERMISSION_GRANTED)
            }

        }else{
            val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }}
            }



    fun save(view : View){
        val artName=binding.editTextArtName.text.toString()
        val artistName=binding.editTextArtistName.text.toString()
        val year=binding.editTextYear.text.toString()

        if (selectedBitmap!=null){
            val smallBitmap=makeSmallerBitmap(selectedBitmap!!,300)

            val outputStream=ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray=outputStream.toByteArray()

            try {
                val art=Art(artName,artistName,year,byteArray)
                compositeDisposible.add(
                    artDao.insert(art)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse)
                )

            }catch (e:Exception){
                e.printStackTrace()
            }

        }



    }

    fun handleResponse(){
        val action=DetailFragmentDirections.actionDetailFragmentToListFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }

    fun makeSmallerBitmap(image: Bitmap, maximumSize:Int): Bitmap {
        var width=image.width
        var height=image.height

        val bitmapRatio:Double=width.toDouble()/height.toDouble()

        if (bitmapRatio>1){
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        }else{
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }

        return Bitmap.createScaledBitmap(image,width,height,true)
    }

    fun delete(view:View){

    }



    fun registerLauncher(){

        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->

            if (result.resultCode==AppCompatActivity.RESULT_OK){
                val intentFromResult=result.data
                if (intentFromResult!=null){
                    val imageData=intentFromResult.data
                    if (imageData!=null){

                        try {

                            if (Build.VERSION.SDK_INT>=28){
                                val source=ImageDecoder.createSource(requireActivity().contentResolver,imageData)//this.contentResolver not working in fragment
                                selectedBitmap=ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }else{
                                selectedBitmap=MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,imageData)
                                binding.imageView.setImageBitmap(selectedBitmap)

                            }

                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                    }
                }


            }

        }


        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if(result){
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }else{
                Toast.makeText(requireContext(),"Permission needed!!!",Toast.LENGTH_LONG).show()//???
            }

        }




    }






}