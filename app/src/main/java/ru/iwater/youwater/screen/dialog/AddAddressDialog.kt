package ru.iwater.youwater.screen.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import ru.iwater.youwater.R
import ru.iwater.youwater.data.Address
import timber.log.Timber

class AddAddressDialog : DialogFragment() {

    internal lateinit var listAddress: List<Address>
    internal lateinit var addressesString: List<String>
    internal lateinit var listener: ChoiceAddressDialog

    interface ChoiceAddressDialog {
        fun choiceAddress(dialogFragment: DialogFragment, id: Int?, addressString: String?, notice: String?)
        fun cancelClick(dialogFragment: DialogFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            var id: Int? = null
            var addressString = ""
            var notice: String? = ""
            builder.setTitle("Выберете адрес")
                .setIcon(R.drawable.ic_address)
                .setSingleChoiceItems(addressesString.toTypedArray(), -1) {
                    _, witch ->
                    id = listAddress[witch].id
                    addressString = addressesString[witch]
                    notice = listAddress[witch].note
                }
                .setPositiveButton("Выбрать") { _, _ ->
                    listener.choiceAddress(this, id, addressString, notice)
                }
                .setNegativeButton("Отмена") { _,_ ->
                    listener.cancelClick(this)
                }
            builder.create()
        } ?: throw IllegalStateException ("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as ChoiceAddressDialog
        } catch (e: ClassCastException) {
            Timber.e("Error dialog: $e")
        }
    }

    companion object {
        private const val TAG = "AddAddress"
        fun getAddressDialog(childFragmentManager: FragmentManager, listAddress: List<Address>, addressString: List<String>) {
            val dialog = AddAddressDialog()
            dialog.listAddress = listAddress
            dialog.addressesString = addressString
            dialog.show(childFragmentManager, TAG)
        }
    }
}