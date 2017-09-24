package app.note.simple.brulinski.sebastian.com.simplenoteapp.Editor

import android.util.Log
import app.note.simple.brulinski.sebastian.com.simplenoteapp.Interfaces.BlockUndo
import app.note.simple.brulinski.sebastian.com.simplenoteapp.databinding.CreateNoteFragmentBinding

/**
Class to handle undo and redo operations
 */
class UndoRedo(var binding: CreateNoteFragmentBinding) : BlockUndo {

    override fun block(lock: Boolean) {
        undoUnlocked = !lock
    }

    private var undoArray = ArrayList<Pair<String, String>>()
    private var undoArraySize = -1
    private val maxUndoArraySize = 2000
    private var from = "title"
    private val fromTitle = "title"
    private val fromNote = "note"
    private var undoUnlocked = true

    fun addUndo(text: String?) {
        if (undoUnlocked && text != null) {
            try {
                if (binding.createNoteTitleField.isFocused)
                    from = fromTitle
                else if (binding.createNoteNoteField.isFocused)
                    from = fromNote

                if (undoArray.size < maxUndoArraySize) {
                    undoArray.add(Pair<String, String>(from, text))
                    undoArraySize = undoArray.size - 1
                } else {
                    undoArray.clear()
                    undoArraySize = undoArray.size - 1
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else undoUnlocked = true

        Log.i("array", "--------------------------------")
        for (x in 0..undoArraySize) {
            Log.i("array", "${undoArray[x].first}, ${undoArray[x].second}")
        }
    }

    fun getUndo() {
        val pair: Pair<String, String>

        if (undoArraySize != -1) {
            pair = undoArray.removeAt(undoArraySize)
            undoArraySize = undoArray.size - 1

            when (pair.first) {
                fromTitle -> {
                    binding.createNoteTitleField.setText(pair.second)
                }
                fromNote -> {
                    binding.createNoteNoteField.setText(pair.second)
                }
            }
        } else {
            binding.createNoteTitleField.setText(null)
            binding.createNoteNoteField.setText(null)
        }
    }

    fun afterDeleteAll() {

        undoArray.removeAt(undoArraySize)
        undoArraySize = undoArray.size - 1
        undoArray.removeAt(undoArraySize)
        undoArraySize = undoArray.size - 1

        for (x in 0..undoArraySize) {
            Log.i("array", "${undoArray[x].first}, ${undoArray[x].second}")
        }
    }
}