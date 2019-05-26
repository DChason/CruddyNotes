package com.dchason.cruddynotes

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_note.*
import java.lang.Exception

class NoteActivity : AppCompatActivity() {

    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        try {
            val bundle: Bundle = intent.extras
            id = bundle.getInt("MainActId", 0)

            if (id != 0) {
                editTitle.setText(bundle.getString("MainActTitle"))
                editContent.setText(bundle.getString("MainActContent"))
            }
        } catch (ex: Exception) {}

        buttonAdd.setOnClickListener {
            val dataManager =  NoteDatabase(this)
            val values = ContentValues()

            values.put("Title", editTitle.text.toString())
            values.put("Content", editContent.text.toString())

            if (id ==0) {
                val mID = dataManager.insert(values)

                if (mID > 0) {
                    Toast.makeText(this, "Note was added successfully.", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to add note.", Toast.LENGTH_LONG).show()
                }
            } else {
                val selectionArray = arrayOf(id.toString())
                val mID = dataManager.update(values, "Id=?", selectionArray)

                if (mID >0) {
                    Toast.makeText(this, "Note was added successfully.", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to add note.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
