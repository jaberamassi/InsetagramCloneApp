package com.jaber.insetagramcloneapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.jaber.insetagramcloneapp.AccountSettingActivity
import com.jaber.insetagramcloneapp.R
import com.jaber.insetagramcloneapp.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val btnEditAccount:Button = view.findViewById(R.id.btnEditAccount)
        btnEditAccount.setOnClickListener {
            val intent = Intent(requireContext(),AccountSettingActivity::class.java)
            startActivity(intent)
        }
        return view

    }

}