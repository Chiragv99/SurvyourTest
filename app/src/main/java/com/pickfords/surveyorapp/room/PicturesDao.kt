package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.PicturesModel

@Dao
interface PicturesDao {

    @Query("Select * from pictures")
    fun getPicturesList(): List<PicturesModel>

    @Query("Select * from pictures WHERE surveyId = :surveyId AND isDelete = :isDelete")
    fun getPicturesListBySurveyId(surveyId: String, isDelete: Boolean = false): List<PicturesModel>

    @Query("Select * from pictures WHERE surveyPicId = :picId")
    fun getPicturesListByPicId(picId: String): PicturesModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(picturesListBySurveyIdList: List<PicturesModel?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(picturesListBySurveyIdList: PicturesModel?)

    @Update
    fun update(picturesListBySurveyIdDataList: PicturesModel?)

    @Update
    fun updateAll(picturesListBySurveyIdDataList: List<PicturesModel?>?)

    @Query("DELETE FROM pictures")
    fun deleteAll()

    @Query("DELETE FROM pictures WHERE surveyPicId = :pictureID")
    fun delete(pictureID: String)

    @Query("DELETE FROM pictures WHERE surveyId = :surveyId")
    fun deleteBySurveyID(surveyId: String)
}