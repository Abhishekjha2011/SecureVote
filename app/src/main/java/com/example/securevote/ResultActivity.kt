package com.example.securevote

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ResultActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val rootView: View = findViewById(R.id.resultRoot)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvWinningParty: TextView = findViewById(R.id.tvWinningParty)
        val tvWinnerVotes: TextView = findViewById(R.id.tvWinnerVotes)
        val tvPartyLeader: TextView = findViewById(R.id.tvPartyLeader)
        val ivPartyLogo: ImageView = findViewById(R.id.ivPartyLogo)


        val votesDatabase = FirebaseDatabase.getInstance().getReference("votes")

        votesDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var winningPartyName: String? = null
                    var maxVotes = 0


                    for (child in snapshot.children) {
                        val partyName = child.key
                        val voteCount = child.getValue(Int::class.java) ?: 0
                        if (voteCount > maxVotes) {
                            maxVotes = voteCount
                            winningPartyName = partyName
                        }
                    }

                    if (winningPartyName != null) {

                        val winningParty = partyList.find { it.name == winningPartyName }
                        if (winningParty != null) {
                            tvWinningParty.text = winningParty.name
                            tvWinnerVotes.text = "Total Votes: $maxVotes"
                            tvPartyLeader.text = "Leader: ${winningParty.leader}"
                            ivPartyLogo.setImageResource(winningParty.logoResId)
                        } else {

                            tvWinningParty.text = winningPartyName
                            tvWinnerVotes.text = "Total Votes: $maxVotes"
                            tvPartyLeader.text = "Leader: N/A"
                        }
                    } else {
                        Toast.makeText(this@ResultActivity, "No votes found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ResultActivity, "No vote data available", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ResultActivity, "Error fetching data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
