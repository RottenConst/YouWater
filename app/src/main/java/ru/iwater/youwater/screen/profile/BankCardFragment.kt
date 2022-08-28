package ru.iwater.youwater.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseFragment
import ru.iwater.youwater.data.BankCard
import ru.iwater.youwater.vm.BankCardViewModel
import ru.iwater.youwater.databinding.FragmentBankCardBinding
import ru.iwater.youwater.screen.adapters.BankCardAdapter
import ru.iwater.youwater.utils.PhoneTextFormatter
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BankCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BankCardFragment : BaseFragment(), BankCardAdapter.OnCardItemListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: BankCardViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()

    private val binding: FragmentBankCardBinding by lazy {
        FragmentBankCardBinding.inflate(LayoutInflater.from(this.context))
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = this
        val adapter = BankCardAdapter(this)
        binding.rvListCard.adapter = adapter
        viewModel.bankCardList.observe(this.viewLifecycleOwner, {
            adapter.submitList(it)
        })
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.addCardItem.addCard)
        bottomSheetBehavior.setPeekHeight(0, true)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.btnAddBankCard.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_EXPANDED
        }
        binding.addCardItem.apply {
            etNumCard.addTextChangedListener(
                PhoneTextFormatter(
                    etNumCard,
                    "#### #### #### ####",
                    binding.btnHelp
                )
            )
            etValidate.addTextChangedListener(
                PhoneTextFormatter(
                    etValidate,
                    "##/##",
                    binding.btnHelp
                )
            )
            etCardCvv.addTextChangedListener(
                PhoneTextFormatter(
                    etCardCvv,
                    "###",
                    btnAddCard
                )
            )

            btnAddCard.setOnClickListener {
                val numberCard = etNumCard.text.toString().replace(" ", "").toLong()
                val bankCard = BankCard(
                    numberCard,
                    etValidate.text.toString(),
                    etCardCvv.text.toString().toInt(),
                    checkThisCard.isChecked
                )
                viewModel.saveBankCard(bankCard)
                etNumCard.text?.clear()
                etCardCvv.text?.clear()
                etValidate.text?.clear()
                etNumCard.clearFocus()
                etValidate.clearFocus()
                etCardCvv.clearFocus()
                checkThisCard.isChecked = false
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.root.isFocusable = true
                }
            }
        }


        return binding.root
    }

    override fun onDeleteCardClick(bankCard: BankCard) {
        viewModel.deleteCard(bankCard)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BankCardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BankCardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}