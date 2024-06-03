package com.example.recylerv.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "contacts_table")
@Parcelize
data class Contacts(
    val name: String,
    val phNum: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable