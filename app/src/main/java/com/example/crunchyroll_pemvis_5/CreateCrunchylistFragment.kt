package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment

class CreateCrunchylistFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_crunchylist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnClose = view.findViewById<ImageView>(R.id.btn_close)
        val edtListName = view.findViewById<EditText>(R.id.edt_list_name)
        val btnCreate = view.findViewById<Button>(R.id.btn_create)

        btnClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        edtListName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s?.toString()?.trim() ?: ""
                btnCreate.isEnabled = input.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btnCreate.setOnClickListener {
            val listName = edtListName.text.toString().trim()
            if (listName.isEmpty()) {
                Toast.makeText(context, "Nama daftar tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (MockData.createdCrunchylists.any { it.name.equals(listName, ignoreCase = true) }) {
                Toast.makeText(context, "Nama daftar sudah ada, gunakan nama lain", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "guest"

            if (userId != "guest") {
                FirestoreHelper().saveCrunchylist(userId, listName) { success, listId ->
                    if (success) {
                        MockData.createdCrunchylists.add(CrunchylistModel(id = listId, name = listName))
                    }
                    if (success) {
                        Toast.makeText(context, "Daftar '$listName' berhasil disimpan online", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Daftar '$listName' gagal sinkron online", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                MockData.createdCrunchylists.add(
                    CrunchylistModel(
                        id = "local_${listName.lowercase().replace(" ", "_")}_${System.currentTimeMillis()}",
                        name = listName
                    )
                )
                Toast.makeText(context, "Daftar '$listName' berhasil dibuat secara lokal", Toast.LENGTH_SHORT).show()
            }
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}
