package com.pickfords.surveyorapp.room

import androidx.room.*
import com.pickfords.surveyorapp.model.surveyDetails.CommentsDetailModel

@Dao
interface CommentsDetailDao {

    @Query("Select * from comments_detail")
    fun getCommentsList(): List<CommentsDetailModel>

    @Query("Select * from comments_detail WHERE surveyId = :surveyId")
    fun getCommentsListBySurveyId(surveyId: String): List<CommentsDetailModel>


    @Query("Select * from comments_detail WHERE sequenceId = :surveySequenceId AND surveyId = :surveyId")
    fun getCommentsBySurveySequenceId(
        surveySequenceId: String,
        surveyId: String
    ): List<CommentsDetailModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(commentsList: List<CommentsDetailModel?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(commentsList: CommentsDetailModel?)

    @Insert
    fun insertCopyComment(commentsList: CommentsDetailModel?)

    @Update
    fun updateAll(commentsDataList: List<CommentsDetailModel?>?)

    @Update
    fun update(commentsDataList: CommentsDetailModel?)

    @Query("DELETE FROM comments_detail WHERE surveyCommentId = :surveyCommentId AND surveyId = :surveyId")
    fun delete(surveyCommentId: String, surveyId: String)

    @Query("DELETE FROM comments_detail WHERE  surveyId = :surveyId")
    fun deleteBySurveyID(surveyId: String)
}