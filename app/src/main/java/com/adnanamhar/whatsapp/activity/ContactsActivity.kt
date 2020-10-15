package com.adnanamhar.whatsapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.adnanamhar.whatsapp.R
import com.adnanamhar.whatsapp.adapter.ContactAdapter
import com.adnanamhar.whatsapp.listener.ContacsClickListener
import com.adnanamhar.whatsapp.util.Contact
import kotlinx.android.synthetic.main.activity_contacs.*

class ContactsActivity : AppCompatActivity(), ContacsClickListener {

    private val contactList = ArrayList<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacs)

        setUpList()
        getContact()
    }

    private fun getContact() {
        progress_layout_contact.visibility = View.VISIBLE
        contactList.clear()
        val newList  = ArrayList<Contact>()
        val phone = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null
        )
        while (phone!!.moveToNext()) {
            val name = phone.getString(
                phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            )
            val phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            newList.add(Contact(name, phoneNumber))
        }
        progress_layout_contact.visibility = View.GONE

        contactList.addAll(newList)
        phone.close()
    }

    private fun setUpList() {
        progress_layout_contact.visibility = View.GONE
        val contactsAdapter = ContactAdapter(contactList)
        contactsAdapter.setOnItemClickListener(this)
        rv_contacs.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = contactsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onContactClicked(name: String?, phone: String?) {
        val intent = Intent()
        intent.putExtra(MainActivity.PARAM_NAME, name)
        intent.putExtra(MainActivity.PARAM_PHONE, phone)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}