package com.example.logitrack.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logitrack.R
import com.example.logitrack.data.Document
import com.example.logitrack.network.RetrofitClient
import kotlinx.coroutines.launch

class DocumentsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_documents, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerDocuments)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getDocuments()
                if (response.isSuccessful) {
                    val documents = response.body() ?: emptyList()
                    recycler.adapter = DocumentsListAdapter(documents) { doc ->
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(doc.url))
                        startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message ?: "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class DocumentsListAdapter(
    private val items: List<Document>,
    private val onDescarregar: (Document) -> Unit
) : RecyclerView.Adapter<DocumentsListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNom: android.widget.TextView = view.findViewById(R.id.tvDocNom)
        val tvTipus: android.widget.TextView = view.findViewById(R.id.tvDocTipus)
        val btnDescarregar: android.widget.Button = view.findViewById(R.id.btnDescarregar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_document, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doc = items[position]
        holder.tvNom.text = doc.nom
        holder.tvTipus.text = doc.tipus
        holder.btnDescarregar.setOnClickListener { onDescarregar(doc) }
    }

    override fun getItemCount() = items.size
}