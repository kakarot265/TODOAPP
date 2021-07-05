package com.example.todoapp

import android.app.Application
import android.content.Intent
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.RoomDatabase
import kotlinx.android.synthetic.main.activity_main.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val list = arrayListOf<TodoModel>()
    val adapter = TodoAdapter(list)

    val db by lazy{
        AppDatabse.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setSupportActionBar(toolbar)

todoRv.apply {
    adapter = this@MainActivity.adapter
    layoutManager = LinearLayoutManager(this@MainActivity)
}
//  EVERY FUNCTION WHICH IS NOT BEING OVERRIDE IS NEEDED TO BE CALLED ELSE IT WONT WORk
        initSwipe()

        db.todoDao().getTask().observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                list.clear()
                list.addAll(it)

                //Notifies the attached observers that the underlying data has beem changed and View reflecting
                // the data set should refresh itself

                adapter.notifyDataSetChanged()
            }else{
                list.clear()
                adapter.notifyDataSetChanged()

            }
        })




    }

    fun initSwipe() {
        val simpleItemTouchCallBack = object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean= false


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if(direction==ItemTouchHelper.LEFT){
                    GlobalScope.launch(Dispatchers.IO) {
                    db.todoDao().deleteTask(adapter.getItemId(position))
                }
                }
                else if(direction==ItemTouchHelper.RIGHT){
                    GlobalScope.launch(Dispatchers.IO) {
                    db.todoDao().finishedTask(adapter.getItemId(position))

                }}

            }
// canvas is something on which you will draw
            override fun onChildDraw(canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
              if(actionState==ItemTouchHelper.ACTION_STATE_SWIPE){
                  // After the if condition we get the entire view of that item for which we will draw
                  val itemView = viewHolder.itemView

                  val paint = Paint()
                  val icon:Bitmap
                  if(dX>0){
             icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_check_white_png)
                      paint.color = Color.parseColor("#388E3C")

                      canvas.drawRect(
                              itemView.left.toFloat(), itemView.top.toFloat(), itemView.left.toFloat()+dX, itemView.bottom.toFloat(), paint
                      )

                      canvas.drawBitmap(
                              icon,
                              itemView.left.toFloat(),
                              itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                              paint
                      )


                  }

                  else{
                      icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_delete_white_png)
                      paint.color = Color.parseColor("#D32F2F")


                      canvas.drawRect(
                              itemView.right.toFloat() + dX, itemView.top.toFloat(),
                              itemView.right.toFloat(), itemView.bottom.toFloat(), paint
                      )

                      canvas.drawBitmap(
                              icon,
                              itemView.right.toFloat() - icon.width,
                              itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                              paint
                      )
                  }

                  viewHolder.itemView.translationX = dX


              }
    else {
                  super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
              }
            }

        }
        // Attaching simple call back to Recycler View
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallBack)
        itemTouchHelper.attachToRecyclerView(todoRv)

    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
                // item of MenuItem type
        val item = menu.findItem(R.id.search)
// Casting the itemView in a SearchView
        // actionView returns the present View of the item
        val searchView = item.actionView as SearchView

        // on This Menu item to be notified when the asociated action view is collapsed or expanded
item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        displayTodo()
        return true;
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        displayTodo()
        return true;
    }

})

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
         if(!newText.isNullOrEmpty()){
             displayTodo(newText)
         }
                return true

            }


        })

        return super.onCreateOptionsMenu(menu)
    }

    fun displayTodo(newText:String=""){
        db.todoDao().getTask().observe(this, Observer {
            if(!it.isNullOrEmpty()){
                it.filter {
                    it.title.contains(newText, ignoreCase = true)
                }
            }
            adapter.notifyDataSetChanged()
        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
           R.id.history->{
               startActivity(Intent(this, HistoryActivity::class.java))
           }

        }
        return super.onOptionsItemSelected(item)
    }
// via onClick in xml file
    fun openNewTask(view: View) {
        startActivity(Intent(this, TaskActivity::class.java))
    }
}


