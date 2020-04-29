package fragment

import adapter.FavouriteRecyclerAdapter
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.bookhub.R
import database.BookDataBase
import database.BookEntity

/**
 * A simple [Fragment] subclass.
 */
class FavoritesFragment : Fragment() {

    lateinit var recyclerFavorite:RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar:ProgressBar
    lateinit var recyclerAdapter:FavouriteRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    var dbBookList= listOf<BookEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_favorites, container, false)

        recyclerFavorite=view.findViewById(R.id.recyclerFavourite)
        progressLayout=view.findViewById(R.id.progressLayoutFav)
        progressBar=view.findViewById(R.id.progressBarfav)
        progressLayout.visibility=View.VISIBLE

        //intialize the LayoutManager as GirdLAyout
        layoutManager= GridLayoutManager(activity as Context,2)

        //Get the List_of_Books from the DataBase
        dbBookList=RetrieveFavourites(activity as Context).execute().get()

        //Check then Situation if(Activity and ListOf_BOOk is not Empty)
        if (activity!=null)
        {
            progressLayout.visibility=View.GONE

            //call the Recycler_Adapter for Favourite Fragment
            recyclerAdapter= FavouriteRecyclerAdapter(activity as Context,dbBookList)

            //set the Adapter function for an xml file,Here recyclerFavourite is An XML Page
            recyclerFavorite.adapter=recyclerAdapter

            //set the LayoutManager for Favourite_Fragment(XML)
            recyclerFavorite.layoutManager=layoutManager
        }

        return view
    }

    //This is an AsyncTask Class for Retrive/Fetch the Data/ListofFavourite Books from Database
    class RetrieveFavourites(val context: Context):AsyncTask<Void,Void,List<BookEntity>>()
    {
        override fun doInBackground(vararg p0: Void?): List<BookEntity> {
            val db=Room.databaseBuilder(context,BookDataBase::class.java,"books-db").
            build()

            return db.bookDao().getAllBook()
        }

    }


}
