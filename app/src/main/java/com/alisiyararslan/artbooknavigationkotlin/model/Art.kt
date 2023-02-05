package com.alisiyararslan.artbooknavigationkotlin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Art(
    @ColumnInfo(name = "artName")
    val artName:String,
    @ColumnInfo(name = "artistName")
    val artistName:String,
    @ColumnInfo(name = "year")
    val year:String,
    @ColumnInfo(name = "image")
    val image :ByteArray) {

    @PrimaryKey(autoGenerate = true)
    var id = 0
}