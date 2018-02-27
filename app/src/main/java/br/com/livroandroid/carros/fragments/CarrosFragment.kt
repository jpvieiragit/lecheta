package br.com.livroandroid.carros.fragments

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.livroandroid.carros.R
import br.com.livroandroid.carros.activity.CarroActivity
import br.com.livroandroid.carros.adapter.CarroAdapter
import br.com.livroandroid.carros.domain.Carro
import br.com.livroandroid.carros.domain.CarroService
import br.com.livroandroid.carros.domain.TipoCarro
import kotlinx.android.synthetic.main.fragment_carros.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

open class CarrosFragment : BaseFragment() {
    private var tipo = TipoCarro.classicos
    protected var carros = listOf<Carro>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            tipo = arguments.getSerializable("tipo") as TipoCarro
        }
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Retorna a view /res/layout/fragment_carros.xml
        val view = inflater?.inflate(R.layout.fragment_carros, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Views
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.setHasFixedSize(true)
    }

    override fun onResume() {
        super.onResume()
        taskCarros()
    }

    open fun taskCarros() {
        // Abre uma thread
        doAsync {
            // Busca os carros
            carros = CarroService.getCarros(tipo)
            // Atualiza a lista na UI Thread
            uiThread {
                recyclerView.adapter = CarroAdapter(carros) { onClickCarro(it) }
            }
        }
    }

    open fun onClickCarro(carro: Carro) {
        activity.startActivity<CarroActivity>("carro" to carro)
    }
}
