package com.dimitriusramos.criminalintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.UUID
import java.util.Locale.getDefault


private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {
    /**
     ** Required interface for hosting activities
     */
    interface Callbacks{
        fun onCrimeSelected(crimeId: UUID)
    }
    private var callbacks: Callbacks? = null

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

   override fun onAttach(context: Context){
       super.onAttach(context)
       callbacks = context as Callbacks? // this means that the hosting activity must implement Callbacks
   }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter

        crimeRecyclerView.addItemDecoration(DividerItemDecoration(crimeRecyclerView.context, DividerItemDecoration.VERTICAL)) //adding a vertical line separator

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer { crimes ->
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)
                }
            })
    }
    override fun onDetach(){
        super.onDetach()
        callbacks = null
    }

    private fun updateUI(crimes: List<Crime>) {
        /*
        if(crimes.isEmpty()) {
            Log.d(TAG, "empty crime list, show empty dialog")
        }*/
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }

    /* Creating a ViewHolder to store references to an item's view.
     * this view holder now has reference to the title and date items
     */
    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        private lateinit var crime: Crime
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            val formatter = SimpleDateFormat("EEEE, MMM dd, yyyy hh:mm a", getDefault())
            dateTextView.text = formatter.format(this.crime.date)
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }

        }

        override fun onClick(v: View) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    /* Expects a list of crimes as an input, then stores this crime list in a property
    *  Implements the Adapter Class*/
    private inner class CrimeAdapter(var crimes: List<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {
        /* Responsible for creating a view to display, then wrapping the view in a view holder and returning the result */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view) // create a new instance of CrimeHolder
        }

        override fun getItemCount() = crimes.size

        //Populate given holder with the crime for a given position
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }
    }
}