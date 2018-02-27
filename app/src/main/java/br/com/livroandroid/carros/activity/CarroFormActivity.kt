package br.com.livroandroid.carros.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import br.com.livroandroid.carros.R
import br.com.livroandroid.carros.domain.Carro
import br.com.livroandroid.carros.domain.CarroService
import br.com.livroandroid.carros.domain.TipoCarro
import br.com.livroandroid.carros.extensions.*
import kotlinx.android.synthetic.main.activity_carro_form.*
import kotlinx.android.synthetic.main.activity_carro_form_contents.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class CarroFormActivity : AppCompatActivity() {
    // O carro pode ser nulo no caso de um Novo Carro
    val carro: Carro? by lazy { intent.getParcelableExtra<Carro>("carro") }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carro_form)
        // Título da Toolbar (Nome do carro ou Novo Carro)
        setupToolbar(R.id.toolbar, carro?.nome?: getString(R.string.novo_carro))
        // Atualiza os dados do formulário
        initViews()
    }
    // Inicializa as views
    fun initViews() {
        // A função apply somente é executada se o objeto não for nulo
        carro?.apply {
            // Foto do carro
            appBarImg.loadUrl(carro?.urlFoto)
            // Dados do carro
            tDesc.string = desc
            tNome.string = nome
            // Tipo do carro
            when (tipo) {
                "classicos" -> radioTipo.check(R.id.tipoClassico)
                "esportivos" -> radioTipo.check(R.id.tipoEsportivo)
                "luxo" -> radioTipo.check(R.id.tipoLuxo)
            }
        }
    }
    // Adiciona as opções Salvar e Deletar no menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_form_carro, menu)
        return true
    }
    // Trata os eventos do menu
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_salvar -> taskSalvar()
        }
        return super.onOptionsItemSelected(item)
    }
    // Salva o carro no ws
    fun taskSalvar() {
        if (tNome.isEmpty()) {
            // Valida se o campo descrição foi preenchido
            tNome.error = getString(R.string.msg_error_form_nome)
            return
        }
        if (tDesc.isEmpty()) {
            // Valida se o campo descricao foi preenchido
            tDesc.error = getString(R.string.msg_error_form_desc)
            return
        }
        doAsync {
            // Cria um carro para salvar/atualizar
            val c = carro?: Carro()
            // Copia valores do form para o Carro
            c.nome = tNome.string
            c.desc = tDesc.string
            c.tipo = when (radioTipo.checkedRadioButtonId) {
                R.id.tipoClassico -> TipoCarro.classicos.name
                R.id.tipoEsportivo -> TipoCarro.esportivos.name
                else -> TipoCarro.luxo.name
            }
            // Salva o carro no servidor
            val response = CarroService.save(c)
            uiThread {
                // Mensagem com a resposta do servidor
                toast(response.msg)
                finish()
            }
        }
    }
}
