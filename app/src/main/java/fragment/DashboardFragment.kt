package fragment

import adapter.DashboardRecyclerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.bookhub.R
import model.Book
import org.json.JSONException
import util.ConnectionManager
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment() {
    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter:DashboardRecyclerAdapter
    lateinit var btnDInternet:Button
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar:ProgressBar

    val bookInfoList = arrayListOf<Book>()

    var ratingComparator=Comparator<Book>{book1, book2 ->
        if (book1.bookRating.compareTo(book2.bookRating,true)==0)
        {
            book1.bookName.compareTo(book2.bookName,true)
        }
        else
        {
            book1.bookRating.compareTo(book2.bookRating,true)
        }

    }

    /*
        val booklist= arrayListOf(
            "P.S I Love You",
            "The Great Gatsby",
            "Madame Broary",
            "War and Peace",
            "Lolita",
            "Middlemarch",
            "The adventures",
            "Moby-Dick",
            "The Lord of the Rings"
        )



        val bookInfoList = arrayListOf<Book>(
            Book("P.S. I love You", "Cecelia Ahern", "Rs. 299", "4.5", R.drawable.ps_ily),
            Book("The Great Gatsby", "F. Scott Fitzgerald", "Rs. 399", "4.1", R.drawable.great_gatsby),
            Book("Anna Karenina", "Leo Tolstoy", "Rs. 199", "4.3", R.drawable.anna_kare),
            Book("Madame Bovary", "Gustave Flaubert", "Rs. 500", "4.0", R.drawable.madame),
            Book("War and Peace", "Leo Tolstoy", "Rs. 249", "4.8", R.drawable.war_and_peace),
            Book("Lolita", "Vladimir Nabokov", "Rs. 349", "3.9", R.drawable.lolita),
            Book("Middlemarch", "George Eliot", "Rs. 599", "4.2", R.drawable.middlemarch),
            Book("The Adventures of Huckleberry Finn", "Mark Twain", "Rs. 699", "4.5", R.drawable.adventures_finn),
            Book("Moby-Dick", "Herman Melville", "Rs. 499", "4.5", R.drawable.moby_dick),
            Book("The Lord of the Rings", "J.R.R Tolkien", "Rs. 749", "5.0", R.drawable.lord_of_rings)
        )

    */


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        //containter is a view group
        //fragment_dashboard->container

        //set the Menu item
        setHasOptionsMenu(true)

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)

        layoutManager = LinearLayoutManager(activity)

        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)

        //show the progeess Layout
        progressLayout.visibility=View.VISIBLE

    /*
        //check Internet Connection
        btnDInternet = view.findViewById(R.id.btnDInternet)

        btnDInternet.setOnClickListener {
            if (ConnectionManager().checkConnetivity(activity as Context)) {
                val dialog = AlertDialog.Builder(activity as Context)

                dialog.setTitle("Success")
                dialog.setMessage("Internet Connection Found")

                dialog.setPositiveButton("Ok") { text, Listner ->
                    //do Somethings
                }
                dialog.setNegativeButton("cancel") { text, Listner ->

                }

                dialog.create()
                dialog.show()
            } else {
                val dialog = AlertDialog.Builder(activity as Context)

                dialog.setTitle("Failure")
                dialog.setMessage("Internet Connection not Found")

                dialog.setPositiveButton("Ok") { text, Listner ->
                    //do Somethings
                }
                dialog.setNegativeButton("cancel") { text, Listner ->

                }

                dialog.create()
                dialog.show()
            }
        }
*/
        //netork settings
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"

        //creating object this way ,to Headers
        if (ConnectionManager().checkConnetivity(activity as Context)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    //here we will handle the response

                    try {
                        progressLayout.visibility=View.GONE
                            val success = it.getBoolean("success")

                            if (success) {
                                //if get Response is success fetch the data from JSONArray
                                val data = it.getJSONArray("data")

                                for (i in 0 until data.length()) {
                                    val bookJsonObject = data.getJSONObject(i)

                                    val bookObject = Book(
                                        bookJsonObject.getString("book_id"),
                                        bookJsonObject.getString("name"),
                                        bookJsonObject.getString("author"),
                                        bookJsonObject.getString("rating"),
                                        bookJsonObject.getString("price"),
                                        bookJsonObject.getString("image")
                                    )

                                    bookInfoList.add(bookObject)

                                        recyclerAdapter =
                                            DashboardRecyclerAdapter(
                                                activity as Context,
                                                bookInfoList
                                            )

                                    recyclerDashboard.layoutManager = layoutManager
                                    recyclerDashboard.adapter = recyclerAdapter

                                    /*
                                    //add revider
                                    recyclerDashboard.addItemDecoration(
                                        DividerItemDecoration(
                                            recyclerDashboard.context,
                                            (layoutManager as LinearLayoutManager).orientation
                                        )
                                    ) */

                                }
                            } else {
                                Toast.makeText(
                                    activity as Context,
                                    "some error occoured",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    catch (e:JSONException)
                    {
                        Toast.makeText(activity as Context, "some Unexpected error occoured", Toast.LENGTH_SHORT).show()
                    }

                },
                    Response.ErrorListener {
                        //here we will handle the errors
                       // println("error is $it")
                        if (activity!=null) {
                            Toast.makeText(
                                activity as Context,
                                "Volley error occoured",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

                //implements gerHeaders Method
                {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()

                        headers["Context-type"] = "application/json"
                        headers["token"] = "4caf407c2bc15a"

                        return headers
                    }
                }

            queue.add(jsonObjectRequest)

        }
        else
        {
            val dialog = AlertDialog.Builder(activity as Context)

            dialog.setTitle("Failure")
            dialog.setMessage("Internet Connection not Found")

            dialog.setPositiveButton("Open Settings") { text, Listner ->
                //do Somethings
                val settingIntent=Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, Listner ->
                ActivityCompat.finishAffinity(activity as Activity)
            }

            dialog.create()
            dialog.show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dashboard,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //id of the all items in Menu
        val id=item?.itemId
        if (id==R.id.action_sort)
        {
            Collections.sort(bookInfoList,ratingComparator)
            bookInfoList.reverse()
        }

        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}
