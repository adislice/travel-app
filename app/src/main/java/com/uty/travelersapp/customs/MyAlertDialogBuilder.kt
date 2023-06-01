package com.uty.travelersapp.customs

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uty.travelersapp.R

enum class MyAlertDialogType {
    SUCCESS,
    WARNING,
    ERROR
}

class MyAlertDialogBuilder(context: Context, type: MyAlertDialogType) :
    MaterialAlertDialogBuilder(context) {
    private var mContext: Context = context
    private var dialogType: MyAlertDialogType = type
    private lateinit var layoutView: View
    private lateinit var dialog: AlertDialog
    private var res: Int = R.layout.dialog_success

    init {
        setLayout()
    }

    fun setLayout() {
        this.res = when(this.dialogType) {
            MyAlertDialogType.SUCCESS -> {
                R.layout.dialog_success
            }
            MyAlertDialogType.WARNING -> {
                R.layout.dialog_warning
            }
            MyAlertDialogType.ERROR -> {
                R.layout.dialog_error
            }
        }
        val view = LayoutInflater.from(mContext).inflate(res, null)
        layoutView = view
        this.setView(view)
    }

    fun setConfirmButton(
        text: CharSequence?,
        listener: OnClickListener
    ): MyAlertDialogBuilder {
        val btn = layoutView.findViewById<Button>(R.id.btn_positive)
        btn.text = text
        btn.visibility = View.VISIBLE
        btn.setOnClickListener(listener)
        return this
    }

    fun setCancelButton(
        text: CharSequence?,
        listener: OnClickListener?,
    ): MyAlertDialogBuilder {
        val btn = layoutView.findViewById<Button>(R.id.btn_negative)
        btn.text = text
        btn.visibility = View.VISIBLE
        btn.setOnClickListener(listener)
        return this
    }

    override fun setTitle(title: CharSequence?): MyAlertDialogBuilder {
        layoutView.findViewById<TextView>(R.id.text_title).text = title
        return this
    }

    fun setBody(title: CharSequence?): MyAlertDialogBuilder {
        layoutView.findViewById<TextView>(R.id.text_body).text = title
        return this
    }

    override fun show(): AlertDialog {
        this.dialog = create()
        this.dialog.show();
        return this.dialog;
    }

    fun close() {
        this.dialog.dismiss()
    }
}

