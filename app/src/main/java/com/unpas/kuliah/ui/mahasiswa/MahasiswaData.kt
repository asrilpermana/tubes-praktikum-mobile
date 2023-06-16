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
<<<<<<< HEAD
    val jenis_kelamin: String
=======
    val jenis_kelamin: String,
    val agama: String,
>>>>>>> ecefcfdc5787e108ec3aa15c71ee84ce2ac462d9
) {
    enum class JenisKelamin {
        Lelaki,
        Perempuan,
<<<<<<< HEAD
=======
        Lainnya
>>>>>>> ecefcfdc5787e108ec3aa15c71ee84ce2ac462d9
    }
}