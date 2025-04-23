package com.example.moodtrack.data.repository

import com.example.moodtrack.data.remote.services.SelfAssessmentApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelfAssessmentRepository @Inject constructor(
    private val selfAssessmentApiService: SelfAssessmentApiService
) {

    fun getFormUrl(): String {
        return selfAssessmentApiService.getFormUrl()
    }
}