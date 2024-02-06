package com.example.playlistmaker1.media.ui.fragment

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import java.util.UUID


open class NewPlaylistFragment: Fragment() {

    open val viewModel by viewModel<NewPlaylistViewModel>()
    val requester = PermissionRequester.instance()
    var _binding: FragmentCreatePlaylistBinding? = null
    val binding get() = _binding!!
    open lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    open var currentUri: Uri? = null
    var imageFilePath: String? = null
    var currentPlaylist = Playlist()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.ivNewPlaylistImage.setImageURI(uri)
                    currentUri = uri
                }
            }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.comlete_playlist_creation)
            .setMessage(R.string.unsaved_data_will_be_lost)
            .setNeutralButton(R.string.cancel) { dialog, which ->
                // ничего не делаем
            }.setPositiveButton(R.string.complete) { dialog, which ->
                findNavController().popBackStack()
            }

        binding.llButtonBack.setOnClickListener {
            showDialog()
        }
        binding.tvTitle.text = resources.getString(R.string.new_playlist)
        binding.bCreatePlaylistButton.text = resources.getString(R.string.create)

        binding.bCreatePlaylistButton.setOnClickListener {
            createPlaylist(currentPlaylist)
            Toast.makeText(requireContext(), "Плейлист ${binding.etNewPlaylistNameInput.text} создан", Toast.LENGTH_LONG)
                .show()
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDialog()
            }
        })

        binding.ivNewPlaylistImage.setOnClickListener {
            lifecycleScope.launch {
                requester.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES).collect { result ->
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
                if (binding.etNewPlaylistNameInput.hasFocus() && s?.isEmpty() == true) markButtonDisable(binding.bCreatePlaylistButton)
                else markButtonAble(binding.bCreatePlaylistButton)
            }
            override fun afterTextChanged(s: Editable?) {
            }
        }
        binding.etNewPlaylistNameInput.addTextChangedListener(simpleTextWatcher)

    }

    fun markButtonDisable(button: Button) {
        button.isEnabled = false
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
    }

    fun markButtonAble(button: Button) {
        button.isEnabled = true
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
    }

    private fun showDialog(){
        if(!TextUtils.isEmpty(binding.etNewPlaylistNameInput.text.toString().trim())||
            !TextUtils.isEmpty(binding.etDescriptionInput.text.toString().trim())||
            binding.ivNewPlaylistImage.drawable != null) {
            confirmDialog.show()
        }else findNavController().popBackStack()
    }

    fun saveImageToPrivateStorage(uri: Uri?) {
        uri ?: return
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        //создаем каталог, если он не создан
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        val file = File(filePath, "${binding.etNewPlaylistNameInput.text}${UUID.randomUUID()}.jpg")
        imageFilePath = file.toString()
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file, false)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    open fun createPlaylist(playlist: Playlist){
        saveImageToPrivateStorage(currentUri)
        viewModel.addPlaylist(playlist.copy(
            playlistName = binding.etNewPlaylistNameInput.text.toString(),
            playlistDescription = binding.etDescriptionInput.text.toString(),
            imageFilePath = imageFilePath ?: setPlaceholder(),
        ) )
    }

    fun setPlaceholder(): String{
        binding.ivNewPlaylistImage.setImageResource(R.drawable.icon_placeholder)
        return ""
    }

}
