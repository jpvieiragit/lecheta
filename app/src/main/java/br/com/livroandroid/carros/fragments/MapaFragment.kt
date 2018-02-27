package br.com.livroandroid.carros.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.livroandroid.carros.R
import br.com.livroandroid.carros.domain.Carro
import br.com.livroandroid.carros.utils.PermissionUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.jar.Manifest

class MapaFragment: BaseFragment(), OnMapReadyCallback {
    // Objeto que controla o Google Maps
    private var map: GoogleMap? = null
    val carro: Carro by lazy { arguments.getParcelable<Carro>("carro") }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_mapa, container, false)
        // Inicia o mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return view
    }
    // O metedo é chamado quando a inicializaçao do mapa estiver OK
    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        this.map = map
        // Vamos mostrar a localizacao do usuario apenas para carros com lat/lng=0
        if (carro.latitude.toDouble() == 0.0) {
            // Ativa o botao para mostrar minha localizacao
            val ok = PermissionUtils.validate(activity, 1,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
            if (ok) {
                // Somente usa o GPS se a permissao estiver OK
                map.isMyLocationEnabled = true
            }
        } else {
            // Mesmo codigo de antes aqui
            // Cria o objeto lat/lng com a coordenada da fábrica
            val location = LatLng(carro.latitude.toDouble(), carro.longitude.toDouble())
            // Posiciona o mapa na coordenada da fabrica (zoom 13)
            val update = CameraUpdateFactory.newLatLngZoom(location, 13f)
            map.moveCamera(update)
            // Marcador no local da fabrica
            map.addMarker(MarkerOptions()
                    .title(carro.nome)
                    .snippet(carro.desc)
                    .position(location)
            )
        }
        // Tippo do mapa: normal, satelite, terreno ou hibrido
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                // Permissão OK, podemos usar o GPS.
                map?.isMyLocationEnabled = true
                return
            }
        }
    }
}