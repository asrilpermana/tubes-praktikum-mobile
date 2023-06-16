package com.unpas.kuliah.ui.mahasiswa

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.unpas.kuliah.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar

class MahasiswaFragment : Fragment() {

    val db by lazy { MahasiswaDatabase(requireContext()) }
    private lateinit var jenisKelaminAdapter: ArrayAdapter<MahasiswaData.JenisKelamin>

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_mahasiswa, container, false)
        val tableLayout: TableLayout = root.findViewById(R.id.tableLayout)
        val horizontalScrollView: HorizontalScrollView = root.findViewById(R.id.horizontalScrollView)

        val refreshButton: FloatingActionButton = root.findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener {
            refreshMahasiswaList()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val mahasiswaList = db.mahasiswaDao().getAllMahasiswas()

            requireActivity().runOnUiThread {
                for (mahasiswa in mahasiswaList) {
                    val tableRow = TableRow(requireContext())

                    val npmCell = createTableCell(mahasiswa.npm)
                    val namaCell = createTableCell(mahasiswa.nama)
                    val tanggalLahirCell = createTableCell(mahasiswa.tanggal_lahir)
                    val jenisKelaminCell = createTableCell(mahasiswa.jenis_kelamin)

                    tableRow.addView(npmCell)
                    tableRow.addView(namaCell)
                    tableRow.addView(tanggalLahirCell)
                    tableRow.addView(jenisKelaminCell)

                    // Add edit icon
                    val editIcon = createEditIcon(mahasiswa)
                    tableRow.addView(editIcon)

                    // Add delete icon
                    val deleteIcon = createDeleteIcon(mahasiswa)
                    tableRow.addView(deleteIcon)

                    tableLayout.addView(tableRow)
                }
            }
        }

        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)
        fab.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val bottomSheetView = inflater.inflate(R.layout.bottom_sheet_mahasiswa, container, false)

            val npmText = bottomSheetView.findViewById<EditText>(R.id.npmText)
            val namaText = bottomSheetView.findViewById<EditText>(R.id.namaText)
            val tanggalLahirText = bottomSheetView.findViewById<EditText>(R.id.tanggalLahirText)
            tanggalLahirText.setOnClickListener(::onTanggalLahirClicked)
            val jenisKelaminText = bottomSheetView.findViewById<Spinner>(R.id.jenisKelaminText)
            val button = bottomSheetView.findViewById<Button>(R.id.mahasiswaButton)

            // Menginisialisasi Spinner
            val jenisKelaminAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                MahasiswaData.JenisKelamin.values().map { it.name }
            )

            jenisKelaminText.adapter = jenisKelaminAdapter

            val retrofit = Retrofit.Builder()
                .baseUrl("https://ppm-api.gusdya.net/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val mahasiswaApi = retrofit.create(MahasiswaApi::class.java)

            button.setOnClickListener {
                val npm = npmText.text.toString()
                val nama = namaText.text.toString()
                val tanggalLahir = tanggalLahirText.text.toString()
                val jenisKelamin = jenisKelaminText.selectedItem.toString()

                val mahasiswaData = MahasiswaData(0, npm, nama, tanggalLahir, jenisKelamin)

                CoroutineScope(Dispatchers.IO).launch {
                    db.mahasiswaDao().insertMahasiswa(mahasiswaData)

                    // add data ke endpoint menggunakan Retrofit
                    try {
                        val response = mahasiswaApi.addMahasiswa(mahasiswaData)
                        if (response.isSuccessful) {
                            bottomSheetDialog.dismiss()
                        } else {
                            showToast("Gagal menambahkan data ke server")
                        }
                    } catch (e: Exception) {
                        showToast("Gagal menambahkan data ke server: ${e.message}")
                    }
                }

                bottomSheetDialog.dismiss()
                showToast("Data berhasil ditambahkan")
            }

            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }

        return root
    }

    fun onTanggalLahirClicked(view: View) {
        val tanggalLahirText = view as TextInputEditText

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            view.context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                tanggalLahirText.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun refreshMahasiswaList() {
        CoroutineScope(Dispatchers.IO).launch {
            val mahasiswaList = db.mahasiswaDao().getAllMahasiswas()

            requireActivity().runOnUiThread {
                val tableLayout: TableLayout = requireView().findViewById(R.id.tableLayout)
                val childCount = tableLayout.childCount

                // Remove all views except the header row
                tableLayout.removeViews(1, childCount - 1)

                for (mahasiswa in mahasiswaList) {
                    val tableRow = TableRow(requireContext())

                    val npmCell = createTableCell(mahasiswa.npm)
                    val namaCell = createTableCell(mahasiswa.nama)
                    val tanggalLahirCell = createTableCell(mahasiswa.tanggal_lahir)
                    val jenisKelaminCell = createTableCell(mahasiswa.jenis_kelamin)

                    tableRow.addView(npmCell)
                    tableRow.addView(namaCell)
                    tableRow.addView(tanggalLahirCell)
                    tableRow.addView(jenisKelaminCell)

                    // Add edit icon
                    val editIcon = createEditIcon(mahasiswa)
                    tableRow.addView(editIcon)

                    // Add delete icon
                    val deleteIcon = createDeleteIcon(mahasiswa)
                    tableRow.addView(deleteIcon)

                    tableLayout.addView(tableRow)
                }
            }
        }
    }

    private fun createTableCell(text: String): TextView {
        val textView = TextView(requireContext())
        textView.text = text
        textView.setPadding(72, 16, 16, 16)
        return textView
    }

    private fun createEditIcon(mahasiswa: MahasiswaData): ImageView {
        val imageView = ImageView(requireContext())
        val layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(16, 16, 16, 16)
        imageView.setImageResource(R.drawable.baseline_edit_24)
        imageView.layoutParams = layoutParams
        imageView.setOnClickListener {
            editMahasiswa(mahasiswa)
        }
        return imageView
    }

    @SuppressLint("MissingInflatedId")
    private fun editMahasiswa(mahasiswa: MahasiswaData) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_mahasiswa, null)

        val npmText = bottomSheetView.findViewById<EditText>(R.id.npmText)
        val namaText = bottomSheetView.findViewById<EditText>(R.id.namaText)
        val tanggalLahirText = bottomSheetView.findViewById<EditText>(R.id.tanggalLahirText)
        tanggalLahirText.setOnClickListener(::onTanggalLahirClicked)
        val jenisKelaminText = bottomSheetView.findViewById<Spinner>(R.id.jenisKelaminText)
        val button = bottomSheetView.findViewById<Button>(R.id.mahasiswaButton)

        npmText.setText(mahasiswa.npm)
        namaText.setText(mahasiswa.nama)
        tanggalLahirText.setText(mahasiswa.tanggal_lahir)
        val jenisKelaminAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            MahasiswaData.JenisKelamin.values().map { it.name }
        )
        jenisKelaminText.adapter = jenisKelaminAdapter
        val jenisKelaminPosition = jenisKelaminAdapter.getPosition(mahasiswa.jenis_kelamin.toString())
        jenisKelaminText.setSelection(jenisKelaminPosition)

        button.text = "Update"

        button.setOnClickListener {
            val updatedInput1 = npmText.text.toString()
            val updatedInput2 = namaText.text.toString()
            val updatedInput3 = tanggalLahirText.text.toString()
            val updatedInput5 = jenisKelaminText.selectedItem.toString()

            CoroutineScope(Dispatchers.IO).launch {
                val updatedMahasiswa = mahasiswa.copy(
                    npm = updatedInput1,
                    nama = updatedInput2,
                    tanggal_lahir = updatedInput3,
                    jenis_kelamin = updatedInput5
                )
                db.mahasiswaDao().updateMahasiswa(updatedMahasiswa)
            }

            bottomSheetDialog.dismiss()
            showToast("Data telah diperbarui")
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun createDeleteIcon(mahasiswa: MahasiswaData): ImageView {
        val imageView = ImageView(requireContext())
        val layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(16, 16, 16, 16)
        imageView.setImageResource(R.drawable.baseline_delete_24)
        imageView.layoutParams = layoutParams
        imageView.setOnClickListener {
            deleteMahasiswa(mahasiswa)
        }
        return imageView
    }

    private fun deleteMahasiswa(mahasiswa: MahasiswaData) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Apakah Anda yakin ingin menghapus data ini?")
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, id ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.mahasiswaDao().deleteMahasiswa(mahasiswa)
                }
                dialog.dismiss()
                showToast("Data telah dihapus") // Custom function to show a toast
            }
            .setNegativeButton("Tidak") { dialog, id ->
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}