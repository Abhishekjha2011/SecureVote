package com.example.securevote

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

data class Party(val name: String, val leader: String, val logoResId: Int)

class VoteActivity : AppCompatActivity() {

    private val partyList = listOf(
        Party("Bhartiya Janta Party", "Narendra Modi", R.drawable.bjp),
        Party("Indian National Congress", "Rahul Gandhi", R.drawable.congr),
        Party("Aam Admi Party", "Arvind Kejriwal", R.drawable.aap),
        Party("Bhartiya Samaj Party", "Mayawati", R.drawable.bsp),
        Party("Samajwadi Party", "Akhilesh Yadav", R.drawable.sp),
        Party("Jharkhand Mukti Morcha", "Hemant Soren", R.drawable.jmm),
        Party("Ajsu", "Sudesh Mahato", R.drawable.ajsu),
        Party("Trinmol Congress", "Mamta Banerjee", R.drawable.tmc),
        Party("National Communist Party", "Sharad Pawar", R.drawable.ncp),
        Party("Shivsena", "Eknath Sinde", R.drawable.ss)
    )

    private var voteCast = false
    private lateinit var votesDatabase: DatabaseReference
    private lateinit var btnSignOut: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vote)

        votesDatabase = FirebaseDatabase.getInstance().getReference("votes")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewParties)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PartyAdapter(partyList) { selectedParty ->
            if (!voteCast) {
                // Get a reference to the vote count for the selected party.
                val partyVoteRef = votesDatabase.child(selectedParty.name)
                partyVoteRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {

                        val currentCount = currentData.getValue(Int::class.java) ?: 0
                        currentData.value = currentCount + 1
                        return Transaction.success(currentData)
                    }
                    override fun onComplete(
                        error: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?
                    ) {
                        if (committed) {
                            Toast.makeText(
                                this@VoteActivity,
                                "Thank you, your vote for ${selectedParty.name} has been recorded.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@VoteActivity,
                                "Vote failed: ${error?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
                voteCast = true
            } else {
                Toast.makeText(this, "Vote already cast", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize the sign-out button.
        btnSignOut = findViewById(R.id.btnSignOut)
        btnSignOut.setOnClickListener {
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
            finishAffinity()  // Closes all activities and exits the app.
        }
    }

    class PartyAdapter(
        private val parties: List<Party>,
        private val onItemClick: (Party) -> Unit
    ) : RecyclerView.Adapter<PartyAdapter.PartyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_party, parent, false)
            return PartyViewHolder(view)
        }

        override fun onBindViewHolder(holder: PartyViewHolder, position: Int) {
            val party = parties[position]
            holder.bind(party)
            holder.itemView.setOnClickListener {
                onItemClick(party)
            }
        }

        override fun getItemCount(): Int = parties.size

        class PartyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvPartyName: TextView = itemView.findViewById(R.id.tvPartyName)
            private val tvPartyLeader: TextView = itemView.findViewById(R.id.tvPartyLeader)
            private val ivPartyLogo: ImageView = itemView.findViewById(R.id.ivPartyLogo)

            fun bind(party: Party) {
                tvPartyName.text = party.name
                tvPartyLeader.text = party.leader
                ivPartyLogo.setImageResource(party.logoResId)
            }
        }
    }
}
