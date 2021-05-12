package com.github.sdpteam15.polyevents.model.map

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import java.util.*

object GoogleMapHeatmap {
    var HEATMAP_PERIOD = 15L

    var drawHeatmap = false
    var timerHeatmap: Timer? = null

    var lastOverlay: TileOverlay? = null

    /**
     * add HeatMap to the map
     * @param latLngs list of points
     */
    fun addHeatMap(latLngs: List<LatLng>) {
        if (latLngs.isNotEmpty()) {
            // Create a heat map tile provider, passing it the latlngs of the police stations.
            val provider = HeatmapTileProvider.Builder()
                .data(latLngs)
                .build()

            lastOverlay?.remove()
            // Add a tile overlay to the map, using the heat map tile provider.
            lastOverlay =
                GoogleMapHelper.map!!.addTileOverlay(TileOverlayOptions().tileProvider(provider))
        }
    }

    /**
     * Draws the heatmap
     */
    fun heatmap() {
        drawHeatmap = !drawHeatmap
        if (drawHeatmap) {
            timerHeatmap = Timer("SettingUp", false)
            val task = object : TimerTask() {
                override fun run() {
                    val locations = ObservableList<LatLng>()
                    Database.currentDatabase.heatmapDatabase!!.getLocations(locations)
                    locations.observeOnce {
                        addHeatMap(it.value)
                    }
                }
            }
            timerHeatmap?.schedule(task, 0, HEATMAP_PERIOD * 1000)
        } else {
            lastOverlay?.remove()
            timerHeatmap?.cancel()
            timerHeatmap = null
        }
    }

    /**
     * Undraws the heatmap
     */
    fun resetHeatmap() {
        timerHeatmap?.cancel()
        drawHeatmap = false
        lastOverlay?.remove()
    }
}