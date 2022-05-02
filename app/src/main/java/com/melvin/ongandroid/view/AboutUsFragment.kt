package com.melvin.ongandroid.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.res.Configuration
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import com.melvin.ongandroid.R
import com.melvin.ongandroid.databinding.FragmentAboutUsBinding
import com.melvin.ongandroid.model.MemberUI
import com.melvin.ongandroid.utils.convertHtmlToString
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.melvin.ongandroid.model.Members
import com.melvin.ongandroid.services.firebase.FirebaseEvent
import com.melvin.ongandroid.utils.Resource
import com.melvin.ongandroid.utils.gone
import com.melvin.ongandroid.utils.visible
import com.melvin.ongandroid.view.adapters.MemberItemAdapter
import com.melvin.ongandroid.view.adapters.MemberListAdapter
import com.melvin.ongandroid.viewmodel.AboutUsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AboutUsFragment : Fragment() {

    // Properties
    private lateinit var binding: FragmentAboutUsBinding
    private val aboutUsViewModel: AboutUsViewModel by activityViewModels()

    // Recycler View adapter
    private val memberAdapter = MemberItemAdapter()
    private val memberListAdapter:MemberListAdapter by lazy{ MemberListAdapter() }

    // Initialization
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutUsBinding.inflate(layoutInflater, container, false)

        // Change toolbar title
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.title = getString(R.string.nosotros)

        // Configuration of recycler view
        initRecyclerView()
        //setupRecyclerViewMembers()


        return binding.root
    }

    /**
     * Initializes RecyclerView For Members.
     * Sets Grid Layout According Orientation.
     * Then, handles The State of the response showing or hiding elements.
     * When:
     * [Resource.Loading] -> Default behaviour. Hides Sections Abouts Us and shows progress Loader.
     * [Resource.Success] -> Hides progress Loader, submit list to adapter and shows RecyclerView.
     * [Resource.ErrorThrowable] -> Will be implemented in # 47
     * [Resource.ErrorApi] ->  Will be implemented in # 47
     * Logs eventes in case of Get request Success and Error.
     */
    private fun initRecyclerView(){
        val layoutManager =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(context, 3)
            } else {
                GridLayoutManager(context, 2)
            }

        with(binding) {
            sectionAboutUs.rvAboutUs.setHasFixedSize(true)
            sectionAboutUs.rvAboutUs.layoutManager = layoutManager
            sectionAboutUs.rvAboutUs.adapter = memberListAdapter

            aboutUsViewModel.membersState.observe(viewLifecycleOwner){ resourceMembers->
                when(resourceMembers){
                    is Resource.Loading ->{
                        scvAboutUs.gone()
                        progressLoader.root.visible()
                        //Logs Event For Success Get request
                        FirebaseEvent.setEvent(requireContext(),"members_retrieve_success" )
                    }

                    is Resource.Success ->{
                        scvAboutUs.visible()
                        progressLoader.root.gone()
                        memberListAdapter.submitList(resourceMembers.data?.data?.toMutableList())

                        //ClickListener Implemented in Recycler that logs member pressed.
                        memberListAdapter.onItemClicked= {members: Members ->
                            FirebaseEvent.setEvent(requireContext(), "member_pressed")
                        }
                    }

                    is Resource.ErrorApi ->{
                        //Logs Event in case of Error in Get request
                        FirebaseEvent.setEvent(requireContext(), "members_retrieve_error")
                        // Will be implemented in # 47
                    }

                    is Resource.ErrorThrowable ->{
                        //Logs Event in case of Error in Exception
                        FirebaseEvent.setEvent(requireContext(), "members_retrieve_error")
                        // Will be implemented in # 47
                    }
                }
            }
        }
    }

    /**
     * Setup recycler view Members
     * created on 30 April 2022 by Leonel Gomez
     */
    private fun setupRecyclerViewMembers() {
        // Show Dialog with member details
        memberAdapter.onItemClicked = { showMemberDetails(it) }
        // Choose to show 3 or 2 columns depending on whether the screen was rotated
        val layoutManager =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(context, 3)
            } else {
                GridLayoutManager(context, 2)
            }

        // Initial configuration of recycler view
        with(binding) {
            sectionAboutUs.rvAboutUs.setHasFixedSize(true)
            sectionAboutUs.rvAboutUs.layoutManager = layoutManager
            sectionAboutUs.rvAboutUs.adapter = memberAdapter
        }

        // The list is populated from a repository asynchronously and updated in live data
        aboutUsViewModel.memberList.observe(viewLifecycleOwner) { result ->
            if (!result.isNullOrEmpty()) {

                //If data is obtained, it is loaded into the recycler adapter
                memberAdapter.submitList(result)
                binding.sectionAboutUs.rvAboutUs.adapter = memberAdapter

            }
        }
    }

    /**
     * Show Member Details
     *
     * @param member is a object with data of the person of About Us
     */
    @SuppressLint("InflateParams")
    private fun showMemberDetails(member: MemberUI) {
        // Update title of the section
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.title = getString(R.string.details)

        // Show dialog with member details
        val dialog = Dialog(requireContext())
        dialog.setContentView(layoutInflater.inflate(R.layout.dialog_member_detail, null))
        dialog.onBackPressed()
        val image = dialog.findViewById(R.id.member_detail_image) as ImageView
        image.load(member.photo)
        val name = dialog.findViewById(R.id.member_detail_name) as TextView
        name.text = member.name
        val description = dialog.findViewById(R.id.member_detail_description) as TextView
        description.text = member.description.convertHtmlToString().trim()
        val linkFacebook = dialog.findViewById(R.id.member_detail_facebook) as TextView
        linkFacebook.text = member.facebookUrl
        val linkLinkedin = dialog.findViewById(R.id.member_detail_linkedin) as TextView
        linkLinkedin.text = member.linkedinUrl
        dialog.setOnCancelListener{
            // Update title of the previous section
            actionBar?.title = getString(R.string.nosotros)
        }
        dialog.show()
    }
}