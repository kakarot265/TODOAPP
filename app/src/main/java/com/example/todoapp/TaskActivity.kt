package com.example.todoapp

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

// USED CONST SO THAT VALUE REMAINS SAME THROUGHOUT THE PROJECT
const val DB_NAME = "todo.db"
class TaskActivity : AppCompatActivity(), View.OnClickListener {
 lateinit var myCalendar: Calendar
 lateinit var dateSetListener:DatePickerDialog.OnDateSetListener
 lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener
var finalDate = 0L
    var finalTime = 0L

    private val notificationId = 1


 private val labels = arrayListOf<String>("Personal", "Business", "Insurance", "Shopping", "Banking")
    val db by lazy{
        AppDatabse.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

   dateEdt.setOnClickListener(this)
        timeInptLay.setOnClickListener(this)

        saveBtn.setOnClickListener(this)

        setUpSpinner()
    }

    private fun setUpSpinner() {
val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, labels)

        labels.sort()
        spinnerCategory.adapter = adapter
    }


    override fun onClick(v: View) {
        when(v.id){
            R.id.dateEdt->{
                setListener()
            }

            R.id.timeInptLay->{
                setTimeListener()
            }

            R.id.saveBtn->{
                saveTodo()
            }
        }

    }

    private fun saveTodo() {
        val category = spinnerCategory.selectedItem.toString()
        val title = titleInplay.editText?.text.toString()
        val description =  taskInpaly.editText?.text.toString()
        GlobalScope.launch(Dispatchers.Main) {
            val id = withContext(Dispatchers.IO) {
              return@withContext  db.todoDao().insertTask(
                    TodoModel(
                        title,
                        description,
                        category,
                        finalDate,
                        finalTime
                    )
                )
            }

            finish()
        }

    }

  /*  private fun saveTodo() {
        val category = spinnerCategory.selectedItem.toString()
        val title = titleInplay.editText?.text.toString()
        val description =  taskInpaly.editText?.text.toString()
        GlobalScope.launch (Dispatchers.IO) {
            db.todoDao().insertTask(
                    TodoModel(
                            title,
                            description,
                            category,
                            finalDate,
                            finalTime
                    )
            )
        }

        finish()




    }
    */


    private fun setTimeListener() {
myCalendar = Calendar.getInstance()
            timeSetListener = TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay, minute ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                myCalendar.set(Calendar.MINUTE, minute)
                updateTime()
                startAlarm();
            }

        val timePickerDialog = TimePickerDialog(this, timeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY),  myCalendar.get(Calendar.MINUTE), false)
            timePickerDialog.show()
        }

    private fun startAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlertReceiver::class.java)
        // set notification id

        intent.putExtra("notificationId", notificationId);
        val pendingIntent = PendingIntent.getBroadcast(this, 12345, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // set alarm
       alarmManager.set(AlarmManager.RTC_WAKEUP, myCalendar.timeInMillis, pendingIntent)
    }


    private fun updateTime() {
        val myFormat = "h:mm a"
        val sdf = SimpleDateFormat(myFormat)
        finalTime = myCalendar.time.time
        timeEdt.setText(sdf.format(myCalendar.time))

    }


    private fun setListener(){
        //Calendar provides a class method getInstance(), which returns a Calendar object whose calendar fields
        // have been initialised to current date and time
        myCalendar = Calendar.getInstance()
        dateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
          myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate()
        }

        val datePickerDialog= DatePickerDialog(this, dateSetListener, myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH), )
       datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
        val myFormat = "EEE, d MMM yyyy"
        val sdf = SimpleDateFormat(myFormat)
        finalDate = myCalendar.time.time
        dateEdt.setText(sdf.format(myCalendar.time))


        timeInptLay.visibility = View.VISIBLE
    }


}