package activity

import android.app.Activity
import android.app.AlertDialog
import android.app.VoiceInteractor
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.bookhub.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import database.BookDataBase
import database.BookEntity
import kotlinx.android.synthetic.main.activity_descpription.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_about.*
import org.json.JSONObject
import util.ConnectionManager
import java.lang.Exception

class DescpriptionActivity : AppCompatActivity() {

   lateinit var destoolbar:Toolbar
    lateinit var destxtBookName:TextView
    lateinit var destxtBookAuthor:TextView
    lateinit var destxtBookRating:TextView
    lateinit var desimgBookImage:ImageView
    lateinit var destxtBookPrice:TextView
    lateinit var destxtAboutBook:TextView
    lateinit var destxtBookDes:TextView
    lateinit var desbtnAddToFav:Button
    lateinit var desprogressLayout:RelativeLayout
    lateinit var desprogressBar: ProgressBar

    var bookId:String="100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_descpription)

        destoolbar=findViewById(R.id.DesToolbar)
        destxtBookName = findViewById(R.id.DestxtBookName)
        destxtBookAuthor = findViewById(R.id.DestxtBookAuthor)
        destxtBookPrice = findViewById(R.id.DestxtBookPrice)
        destxtBookRating = findViewById(R.id.DestxtBookRating)
        desimgBookImage = findViewById(R.id.DesimgBookImage)
        destxtBookDes = findViewById(R.id.DestxtBookDes)
        desbtnAddToFav = findViewById(R.id.DesbtnAddToFav)
        desprogressBar = findViewById(R.id.DesProgressBar)
        desprogressLayout = findViewById(R.id.DesProgressLayout)

        desprogressLayout.visibility = View.VISIBLE
        desprogressBar.visibility = View.VISIBLE

        //transfer Actionbar to Toolbar
        setSupportActionBar(destoolbar)
        supportActionBar?.title="Book Details"

        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        } else {
            Toast.makeText(this@DescpriptionActivity, "Some unexpected eroor", Toast.LENGTH_SHORT)
                .show()
        }

        if (bookId == "100") {
            Toast.makeText(this@DescpriptionActivity, "Some unexpected eroor", Toast.LENGTH_SHORT)
                .show()
        }


        val queue = Volley.newRequestQueue(this@DescpriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if (ConnectionManager().checkConnetivity(this@DescpriptionActivity))
        {
        val jsonRequest = object : JsonObjectRequest(Method.POST, url, jsonParams,
            Response.Listener {
                try {
                    val success = it.getBoolean("success")
                    if (success)
                    {
                        val bookJsonObject = it.getJSONObject("book_data")

                        //iff success ProgressLayout Gone
                        desprogressLayout.visibility = View.GONE

                        val imageUrl=bookJsonObject.getString("image")

                        Picasso.get().load(bookJsonObject.getString("image"))
                            .error(R.drawable.default_book_cover).into(desimgBookImage)

                        destxtBookName.text = bookJsonObject.getString("name")
                        destxtBookAuthor.text = bookJsonObject.getString("author")
                        destxtBookRating.text = bookJsonObject.getString("rating")
                        destxtBookPrice.text = bookJsonObject.getString("price")
                        destxtBookDes.text = bookJsonObject.getString("description")

                        val bookEntity=BookEntity(
                            bookId.toInt(),
                            destxtBookName.text.toString(),
                            destxtBookAuthor.text.toString(),
                            destxtBookPrice.text.toString(),
                            destxtBookRating.text.toString(),
                            destxtBookDes.text.toString(),
                            imageUrl
                        )

                        val checkFav=DBAsyncTask(applicationContext,bookEntity,1).execute()
                        val isFav=checkFav.get()

                        if (isFav)
                        {
                            desbtnAddToFav.text="Remove from Favourites"
                            val favColor=ContextCompat.getColor(applicationContext,R.color.colorFav)
                            desbtnAddToFav.setBackgroundColor(favColor)
                        }
                        else
                        {
                            desbtnAddToFav.text="Add to Favoirites"
                            val nofavColor=ContextCompat.getColor(applicationContext,R.color.colorNoFav)
                            desbtnAddToFav.setBackgroundColor(nofavColor)
                        }

                        //added OnClickListner on Favourite Button
                        //check DBAsyncTask(applicationContext,bookEntity,1).execute().get()==isFav is not added on
                        // Favortes then add this item

                        desbtnAddToFav.setOnClickListener{

                                if(!DBAsyncTask(applicationContext,bookEntity,1).execute().get())
                                {
                                    val async=DBAsyncTask(applicationContext,bookEntity,2).execute()
                                    val result=async.get()

                                    if (result)
                                    {
                                        Toast.makeText(this@DescpriptionActivity,"Added to Favourites",Toast.LENGTH_SHORT).show()

                                        desbtnAddToFav.text="Remove from Favourites"
                                        val favColor=ContextCompat.getColor(applicationContext,R.color.colorFav)
                                        desbtnAddToFav.setBackgroundColor(favColor)

                                    }
                                    else {
                                        Toast.makeText(this@DescpriptionActivity,"Some error occoured!!",Toast.LENGTH_SHORT).show()
                                    }

                                }
                                else
                                {
                                    val async=DBAsyncTask(applicationContext,bookEntity,3).execute()
                                    val result=async.get()

                                    if (result)
                                    {
                                        Toast.makeText(this@DescpriptionActivity,"Remove from Favourites",Toast.LENGTH_SHORT).show()

                                        desbtnAddToFav.text="Add to Favoirites"
                                        val nofavColor=ContextCompat.getColor(applicationContext,R.color.colorNoFav)
                                        desbtnAddToFav.setBackgroundColor(nofavColor)
                                    }
                                    else
                                    {
                                        Toast.makeText(this@DescpriptionActivity,"Some error occoured!!",Toast.LENGTH_SHORT).show()
                                    }
                                }

                        }
                    }
                    else
                    {
                        Toast.makeText(
                            this@DescpriptionActivity,
                            "Some error 1 ocoured",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@DescpriptionActivity,
                        "Some error 2 ocoured",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    this@DescpriptionActivity,
                    "Volley error  ocoured",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                headers["Context-Type"] = "application/json"
                headers["token"] = "4caf407c2bc15a"

                return headers
            }
        }
            queue.add(jsonRequest)

    }
        else
        {

            val dialog = AlertDialog.Builder(this@DescpriptionActivity)

            dialog.setTitle("Failure")
            dialog.setMessage("Internet Connection not Found")

            dialog.setPositiveButton("Open Settings") { text, Listner ->
                //do Somethings
                val settingIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                this@DescpriptionActivity.finish()
            }
            dialog.setNegativeButton("Exit") { text, Listner ->
                ActivityCompat.finishAffinity(this@DescpriptionActivity)
            }

            dialog.create()
            dialog.show()
        }

    }

    class DBAsyncTask(val context: Context,val bookEntity: BookEntity,val mode:Int):AsyncTask<Void,Void,Boolean>()
    {
        //databaseBuilder is a mandatory fun to create db variable
        val db=Room.databaseBuilder(context,BookDataBase::class.java,"books-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            when(mode)
            {
                1-> {
                    //check DB if the book is added on favourite or not
                    val book:BookEntity?=db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()

                    return book!=null
                }

                2-> {
                    //save the book into DB as favourite
                    db.bookDao().insertBook(bookEntity)
                    return true

                }

                3-> {
                    //remove the book from DB
                    db.bookDao().deleteBook(bookEntity)
                    return true
                }
            }

            return false
        }

    }
}
