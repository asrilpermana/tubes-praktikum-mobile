package com.unpas.kuliah.ui.dosen

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DosenApi {
    @POST("dosen")
    suspend fun addDosen(@Body dosenData: DosenData): Response<ResponseBody>
}