package com.unpas.kuliah.ui.dosen

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DosenData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val nidn: String,
    val nama: String,
    val gelar_depan: String,
    val gelar_belakang: String,
    val pendidikan: String
) {
    enum class Pendidikan {
        S2,
        S3,
    }
}