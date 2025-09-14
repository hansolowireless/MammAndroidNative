
package com.mamm.mammapps.ui.component.player.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.DialogFragment
import kotlin.math.abs


class PINDialogFragment : DialogFragment() {

    private lateinit var mDetector: GestureDetectorCompat

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Gesture detector for swiping and zapping on Mobile
        this.context?.let {
            mDetector = GestureDetectorCompat(it, MyGestureListener())
        }
        dialog?.window?.decorView?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, me: MotionEvent?): Boolean {
                v?.onTouchEvent(me)
                print("Touched Dialog")
                if (me != null) {
                    mDetector.onTouchEvent(me)
                }
                return true
            }
        })
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    interface PINDialogListener {
        fun onDialogPositiveClick(pin: String?)
        fun onDialogSWIPEDUP()
        fun onDialogSWIPEDDOWN()
    }

    // Use this instance of the interface to deliver action events
    private var mListener: PINDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            parentFragment as PINDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity.toString() + " must implement PINDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val input = EditText(requireContext())
        input.hint = ""
        input.inputType = InputType.TYPE_CLASS_NUMBER
        val dialogBuilder = AlertDialog.Builder(requireContext())
                .setMessage("Introducir PIN")
                .setView(input)
                .setPositiveButton(getString(android.R.string.yes), DialogInterface.OnClickListener() {
                    _, _ ->  mListener?.onDialogPositiveClick(input.text.toString());
                })

        return dialogBuilder.create()
    }

    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }

    //Gestures for Mobile
    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD: Int = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onShowPress(e: MotionEvent) {
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return false
        }

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(downEvent: MotionEvent?, moveEvent: MotionEvent, velocityX: Float, velocityY: Float): Boolean {

            if (downEvent == null) return false

            var result = false
            val diffY = moveEvent.y - downEvent.y
            val diffX = moveEvent.x - downEvent.x
            // which was greater?  movement across Y or X?
            if (abs(diffX) > abs(diffY)) {
                // right or left swipe
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        print("swiped up")
                        mListener?.onDialogSWIPEDUP()
                    } else {
                        mListener?.onDialogSWIPEDDOWN()
                    }
                    result = true
                }
            } else {
                // up or down swipe
                if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        print("swiped up")
                        mListener?.onDialogSWIPEDDOWN()

                    } else {
                        mListener?.onDialogSWIPEDUP()
                    }
                    result = true
                }
            }
            return result
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            return false
        }

        override fun onLongPress(e: MotionEvent) {
        }
    }

}

class ShouldEnterPINDialogFragment : DialogFragment() {

    private val keyEventListener = DialogInterface.OnKeyListener { dialog, keyCode, event ->
        Log.i(javaClass.name, "onKey() keyCode: $keyCode")
        //Register only down press of the button, not "up press", i.e., user releasing the button
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP && KeyEvent.ACTION_DOWN == event.action) {
            mListener?.onShouldPINDialogUPClick()
            //Dismissed in Player View
            //dialog?.dismiss()
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && KeyEvent.ACTION_DOWN == event.action) {
            mListener?.onShouldPINDialogDOWNClick()
            //dialog?.dismiss()
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            mListener?.onShouldPINDialogOKClick()
            dialog?.dismiss()
        }
        true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setOnKeyListener(keyEventListener)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        dialog?.setOnKeyListener(null)
        super.onDestroyView()
    }

    interface ShouldEnterPINDialogListener {
        fun onShouldPINDialogOKClick()
        fun onShouldPINDialogUPClick()
        fun onShouldPINDialogDOWNClick()
    }

    // Use this instance of the interface to deliver action events
    private var mListener: ShouldEnterPINDialogListener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            //activity as ShouldEnterPINDialogListener
            parentFragment as ShouldEnterPINDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity.toString() + " must implement ShouldGoToPINDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
                .setMessage("Pulse las teclas Arriba/Abajo para seguir cambiando de canal. Pulse OK en el mando para introducir el PIN de Control Parental")

        /*.setPositiveButton(getString(android.R.string.yes), DialogInterface.OnClickListener() {
            _, _ ->  mListener?.onShouldPINDialogPositiveClick();
        })*/


        return dialogBuilder.create()
    }

    companion object {
        const val TAG = "ShouldEnterPINDialog"
    }



}
