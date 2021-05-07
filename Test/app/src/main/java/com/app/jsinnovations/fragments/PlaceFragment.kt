package com.app.jsinnovations.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.jsinnovations.R
import com.app.jsinnovations.activities.StationsActivity
import com.app.jsinnovations.adapters.PlaceAdapter
import com.app.jsinnovations.api.BackendApi
import com.app.jsinnovations.models.Place
import com.app.jsinnovations.sharing.LoginHelper
import com.app.jsinnovations.utils.ItemOffsetDecorationVertical
import com.app.jsinnovations.utils.Utils
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_place.*
import java.util.*
import kotlin.math.roundToInt


class PlaceFragment : Fragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = activity?.findViewById<TextView>(R.id.title)
        title?.text = getString(R.string.choose_base)
        val realm = Realm.getDefaultInstance()

        placesRecyclerView.layoutManager = LinearLayoutManager(context)
        placesRecyclerView.addItemDecoration(
            ItemOffsetDecorationVertical(
                Utils.convertDpToPixel(5).roundToInt()
            )
        )
        val placeAdapter = PlaceAdapter(context!!)
        placeAdapter.listener = ({ placeId, address ->
            val intent = Intent(activity, StationsActivity::class.java)
            intent.putExtra("placeId", placeId)
            intent.putExtra("address", address)
            startActivity(intent)
        })
        if (Utils.isNetworkConnected(context!!))
            BackendApi.getInstance().getPlaces(LoginHelper.getAccessToken(context!!)!!) { places ->
                realm.executeTransaction {
                    realm.copyToRealmOrUpdate(places!!)
                }
                placeAdapter.places = places!!
                placesRecyclerView.adapter = placeAdapter
            }
        else{
            val places = ArrayList<Place>()
            places.addAll(
                realm.copyFromRealm(
                    realm.where(Place::class.java).findAll()
                )
            )
            placeAdapter.places = places
            placesRecyclerView.adapter = placeAdapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_place, container, false)


}