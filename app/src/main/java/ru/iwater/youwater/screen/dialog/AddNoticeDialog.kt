package ru.iwater.youwater.screen.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import ru.iwater.youwater.R
import ru.iwater.youwater.databinding.AddNoticeDialogBinding

class AddNoticeDialog: DialogFragment() {

    internal lateinit var listener: AddNoticeDialogListener
    internal var notice: String? = ""
    private val binding: AddNoticeDialogBinding by lazy {
        AddNoticeDialogBinding.inflate(LayoutInflater.from(this.context))
    }

    interface AddNoticeDialogListener {
        fun onDialogAddNotice(inputNotice: String, dialog: DialogFragment)
        fun onDialogCancelNotice(dialog: DialogFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        binding.etAddNoticeOrder.setText(notice, TextView.BufferType.EDITABLE)
        binding.btnCancelNotice.setOnClickListener {
            listener.onDialogCancelNotice(this)
        }
        binding.btnAddNotice.setOnClickListener {
            listener.onDialogAddNotice(binding.etAddNoticeOrder.text.toString(), this)
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = this.parentFragment as AddNoticeDialogListener
        } catch (e: ClassCastException) {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.55).toInt()
        dialog?.window?.setLayout(width, height)
    }

    companion object {
        private const val TAG = "AddNotice"
        fun getAddNoticeDialog(chaildFragmentManager: FragmentManager, notice: String?) {
            val dialog = AddNoticeDialog()
            dialog.notice = notice
            dialog.show(chaildFragmentManager, TAG)
        }
    }
}