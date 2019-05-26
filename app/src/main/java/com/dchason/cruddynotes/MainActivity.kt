package com.dchason.cruddynotes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var noteList = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<FloatingActionButton>(R.id.fabAddNote)
        fab.setOnClickListener {
            val intent = Intent(this, NoteActivity::class.java)
            startActivity(intent)
        }

        loadQueryAll()

        listViewNotes.onItemClickListener = AdapterView.OnItemClickListener {adapterView, view, position, id ->
            Toast.makeText(this, "Clicked on " + noteList[position].title, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadQueryAll()
    }

    private fun loadQueryAll() {

        val noteAdapter = NoteAdapter(this, noteList)
        val dataManager = NoteDatabase(this)
        val cursor = dataManager.queryAll()

        noteList.clear()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("Id"))
                val title = cursor.getString(cursor.getColumnIndex("Title"))
                val content = cursor.getString(cursor.getColumnIndex("Content"))

                noteList.add(Note(id, title, content))
            } while (cursor.moveToNext())
        }

        listViewNotes.adapter = noteAdapter
    }

    private fun updateNote(note: Note) {
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra("MainActId", note.id)
        intent.putExtra("MainActTitle", note.title)
        intent.putExtra("MainActContent", note.content)
        startActivity(intent)
    }

    inner class NoteAdapter(context: Context, private var noteList: ArrayList<Note>) : BaseAdapter() {

        private var context: Context? = context

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val view: View?
            val viewHolder: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.note, parent, false)
                viewHolder = ViewHolder(view)
                view.tag = viewHolder
                Log.i("CruddyNotes", "ViewHolder position: $position")
            } else {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }

            val mNote = noteList[position]

            viewHolder.textViewTitle.text = mNote.title
            viewHolder.textViewContent.text = mNote.content

            viewHolder.imageViewEdit.setOnClickListener {
                updateNote(mNote)
            }

            viewHolder.imageViewDelete.setOnClickListener {
                val dataManager = NoteDatabase(this.context!!)
                val selectionArgs = arrayOf(mNote.id.toString())
                dataManager.delete("Id=?", selectionArgs)
                loadQueryAll()
            }

            return view
        }

        override fun getItem(position: Int): Any {
            return noteList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return noteList.size
        }
    }

    private class ViewHolder(view: View?) {
        val textViewTitle: TextView = view?.findViewById(R.id.textViewTitle) as TextView
        val textViewContent: TextView = view?.findViewById(R.id.textViewContent) as TextView
        val imageViewEdit: ImageView = view?.findViewById(R.id.imageViewEdit) as ImageView
        val imageViewDelete: ImageView = view?.findViewById(R.id.imageViewDelete) as ImageView
    }
}
