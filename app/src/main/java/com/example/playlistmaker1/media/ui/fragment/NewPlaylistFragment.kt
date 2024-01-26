package com.example.playlistmaker1.media.ui.fragment

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker1.R
import com.example.playlistmaker1.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker1.media.domain.model.Playlist
import com.example.playlistmaker1.media.ui.view_model.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream


class NewPlaylistFragment: Fragment() {

    private val newPlaylistViewModel by viewModel<NewPlaylistViewModel>()
    val requester = PermissionRequester.instance()
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var buttonBack: LinearLayout
    private lateinit var newPlaylistImage: ImageView
    private lateinit var newPlaylistNameInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var createPlaylistButton: Button
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var currentUri: Uri? = null
    private var imageFilePath: String? = null
    private var currentPlaylist = Playlist()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonBack = binding.buttonBack
        newPlaylistImage = binding.newPlaylistImage
        newPlaylistNameInput = binding.newPlaylistNameInput
        descriptionInput = binding.descriptionInput
        createPlaylistButton = binding.createPlaylistButton

        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    newPlaylistImage.setImageURI(uri)
                    currentUri = uri
                }
            }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNeutralButton("Отмена") { dialog, which ->
                // ничего не делаем
            }.setPositiveButton("Завершить") { dialog, which ->
                findNavController().popBackStack()
            }

        buttonBack.setOnClickListener {
            showDialog()
        }

        createPlaylistButton.setOnClickListener {
            createPlaylist(currentPlaylist)
            Toast.makeText(requireContext(), "Плейлист ${newPlaylistNameInput.text} создан", Toast.LENGTH_LONG)
                .show()
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDialog()
            }
        })

        newPlaylistImage.setOnClickListener {
            lifecycleScope.launch {
                requester.request(Manifest.permission.READ_MEDIA_IMAGES).collect { result ->
                    when (result) {
                        is PermissionResult.Granted -> {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                        is PermissionResult.Denied -> {
                            return@collect
                        }
                        is PermissionResult.Cancelled -> {
                            return@collect
                        }
                    }
                }
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (newPlaylistNameInput.hasFocus() && s?.isEmpty() == true) markButtonDisable(createPlaylistButton)
                else markButtonAble(createPlaylistButton)
            }
            override fun afterTextChanged(s: Editable?) {
            }
        }
        newPlaylistNameInput.addTextChangedListener(simpleTextWatcher)

    }

    private fun markButtonDisable(button: Button) {
        button.isEnabled = false
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
    }

    private fun markButtonAble(button: Button) {
        button.isEnabled = true
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
    }

    private fun showDialog(){
        if(!TextUtils.isEmpty(newPlaylistNameInput.text.toString().trim())||
            !TextUtils.isEmpty(descriptionInput.text.toString().trim())||
            newPlaylistImage.drawable != null) {
            confirmDialog.show()
        }else findNavController().popBackStack()
    }

    private fun saveImageToPrivateStorage(uri: Uri?) {
        uri ?: return
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        //создаем каталог, если он не создан
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, "${newPlaylistNameInput.text}.jpg")
        imageFilePath = file.toString()
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    private fun createPlaylist(playlist: Playlist){
        saveImageToPrivateStorage(currentUri)
        newPlaylistViewModel.addPlaylist(playlist.copy(
            playlistName = newPlaylistNameInput.text.toString(),
            playlistDescription = descriptionInput.text.toString(),
            imageFilePath = imageFilePath ?: setPlaceholder(),
        ) )
    }

    private fun setPlaceholder(): String{
        newPlaylistImage.setImageResource(R.drawable.icon_placeholder)
        return ""
    }

}
