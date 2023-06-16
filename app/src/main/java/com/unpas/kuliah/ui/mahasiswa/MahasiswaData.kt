package com.unpas.kuliah.ui.mahasiswa

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MahasiswaData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val npm: String,
    val nama: String,
    val tanggal_lahir: String,
    val jenis_kelamin: String,
    val agama: String,
) {
    enum class JenisKelamin {
        Lelaki,
        Perempuan,
    }
}