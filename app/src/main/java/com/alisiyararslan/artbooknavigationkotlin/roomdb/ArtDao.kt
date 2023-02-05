package com.alisiyararslan.artbooknavigationkotlin.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.alisiyararslan.artbooknavigationkotlin.model.Art
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface ArtDao {

    @Query("SELECT * FROM Art")
    fun getAll():Flowable<List<Art>>

    @Insert
    fun insert(art:Art): Completable

    @Delete
    fun delete(art:Art):Completable

    @Query("SELECT * FROM Art WHERE id= :id")
    fun getArtById(id:Int):Flowable<Art>

}