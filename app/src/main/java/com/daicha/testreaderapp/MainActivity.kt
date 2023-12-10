package com.daicha.testreaderapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.*
import androidx.viewpager2.widget.ViewPager2
import com.daicha.testreaderapp.databinding.ActivityMainBinding
import com.daicha.testreaderapp.databinding.FragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.Serializable
interface BookRepository{
    suspend fun insert(book: book)
    suspend fun getbooks(): List<book>
    suspend fun update(book: book)
    suspend fun delete(book: book)
    fun getbookslived() : LiveData<List<book>>
}
class repositoryimp (private val bookdbDao: bookdbDao): BookRepository {
    override suspend fun insert(book: book) {
        bookdbDao.insertbook(book)
    }

    override suspend fun update(book: book) {
        bookdbDao.updatebook(book)
    }

    override suspend fun delete(book: book) {
        bookdbDao.deletebook(book)
    }

    override fun getbookslived(): LiveData<List<book>> {
        return bookdbDao.getAllbooksLivedata()
    }

    override suspend fun getbooks() = bookdbDao.getAllbooks()
}
@Dao
interface bookdbDao {
    @Insert
    suspend fun insertbook(book: book)
    @Update
    suspend fun updatebook(book: book)
    @Delete
    suspend fun deletebook(book: book)
    @Query("SELECT * FROM book")
    suspend fun getAllbooks(): List<book>
    @Query("SELECT * FROM book")
    fun getAllbooksLivedata(): LiveData<List<book>>
}
@Database(
    entities = [book::class],
    version = 1,
    exportSchema = true
)
abstract class booksdb : RoomDatabase() {
    abstract fun gdao(): bookdbDao
    companion object{
        private var db : booksdb? = null
        fun bookdbfunc(context: Context): booksdb{
            if (db==null){
                db = Room.databaseBuilder(
                    context.applicationContext,
                    booksdb::class.java,
                    "books.db"
                ).build()
            }
            return db!!
        }
    }
}
@Entity(tableName = "book")
data class book (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name="namebook")
    var name: String = "",
    @ColumnInfo(name="athor")
    var author : String ="",
    @ColumnInfo(name="desc")
    var desc: String = "",
    @ColumnInfo(name="janrs")
    var janrs : String = "",
) : Serializable

class BookBottomSheetFragment(private val r: FragmentActivity, private val x:book) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    private val bd by lazy { booksdb.bookdbfunc(r).gdao() }
    private val bookrep: BookRepository by lazy { repositoryimp(bd) }
    @SuppressLint("UseCompatLoadingForDrawables", "IntentReset", "SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply{
            button2.visibility = View.GONE
            namef.setText(x.name)
            athorf.setText(x.author)
            descf.setText(x.desc)
            janrf.setText(x.janrs)
            if (x.name != "") {
                namef.addTextChangedListener(object : TextWatcher {

                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        // Этот метод вызывается перед изменением текста
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        var bool = false
                        if (namef.text.toString() != x.name)
                            bool = true
                        if (athorf.text.toString() != x.author)
                            bool = true
                        if (descf.text.toString() != x.desc)
                            bool = true
                        if (janrf.text.toString() != x.janrs)
                            bool = true
                        if (bool) {
                            button2.visibility = View.VISIBLE
                            button2.setText("Обновить")
                        }
                        else
                            button2.visibility = View.GONE
                    }

                    override fun afterTextChanged(s: Editable) {
                        // Этот метод вызывается после того, как текст был изменен
                    }
                })
                athorf.addTextChangedListener(object : TextWatcher {

                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        // Этот метод вызывается перед изменением текста
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        var bool = false
                        if (namef.text.toString() != x.name)
                            bool = true
                        if (athorf.text.toString() != x.author)
                            bool = true
                        if (descf.text.toString() != x.desc)
                            bool = true
                        if (janrf.text.toString() != x.janrs)
                            bool = true
                        if (bool) {
                            button2.visibility = View.VISIBLE
                            button2.setText("Обновить")
                        }
                        else
                            button2.visibility = View.GONE
                    }

                    override fun afterTextChanged(s: Editable) {
                        // Этот метод вызывается после того, как текст был изменен
                    }
                })
                descf.addTextChangedListener(object : TextWatcher {

                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        // Этот метод вызывается перед изменением текста
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        var bool = false
                        if (namef.text.toString() != x.name)
                            bool = true
                        if (athorf.text.toString() != x.author)
                            bool = true
                        if (descf.text.toString() != x.desc)
                            bool = true
                        if (janrf.text.toString() != x.janrs)
                            bool = true
                        if (bool) {
                            button2.visibility = View.VISIBLE
                            button2.setText("Обновить")
                        }
                        else
                            button2.visibility = View.GONE
                    }

                    override fun afterTextChanged(s: Editable) {
                        // Этот метод вызывается после того, как текст был изменен
                    }
                })
                janrf.addTextChangedListener(object : TextWatcher {

                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        // Этот метод вызывается перед изменением текста
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        var bool = false
                        if (namef.text.toString() != x.name)
                            bool = true
                        if (athorf.text.toString() != x.author)
                            bool = true
                        if (descf.text.toString() != x.desc)
                            bool = true
                        if (janrf.text.toString() != x.janrs)
                            bool = true
                        if (bool) {
                            button2.visibility = View.VISIBLE
                            button2.setText("Обновить")
                        }
                        else
                            button2.visibility = View.GONE
                    }

                    override fun afterTextChanged(s: Editable) {
                        // Этот метод вызывается после того, как текст был изменен
                    }
                })
            }
            else{
                button2.visibility = View.VISIBLE
                button2.setText("Сохранить")
            }
            button2.setOnClickListener {
                if (button2.text.toString() == "Сохранить"){
                    if (namef.text.toString() != "" && athorf.text.toString() != "" && descf.text.toString() != "" && janrf.text.toString() != "")
                        r.lifecycleScope.launch(Dispatchers.IO) {
                        bookrep.insert(book(name = namef.text.toString(), author = athorf.text.toString(),desc = descf.text.toString(), janrs = janrf.text.toString()))
                        withContext(Dispatchers.Main){
                            button2.visibility = View.GONE
                            namef.addTextChangedListener(object : TextWatcher {

                                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                                    // Этот метод вызывается перед изменением текста
                                }

                                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                                    var bool = false
                                    if (namef.text.toString() != x.name)
                                        bool = true
                                    if (athorf.text.toString() != x.author)
                                        bool = true
                                    if (descf.text.toString() != x.desc)
                                        bool = true
                                    if (janrf.text.toString() != x.janrs)
                                        bool = true
                                    if (bool) {
                                        button2.visibility = View.VISIBLE
                                        button2.setText("Обновить")
                                    }
                                    else
                                        button2.visibility = View.GONE
                                }

                                override fun afterTextChanged(s: Editable) {
                                    // Этот метод вызывается после того, как текст был изменен
                                }
                            })
                            athorf.addTextChangedListener(object : TextWatcher {

                                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                                    // Этот метод вызывается перед изменением текста
                                }

                                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                                    var bool = false
                                    if (namef.text.toString() != x.name)
                                        bool = true
                                    if (athorf.text.toString() != x.author)
                                        bool = true
                                    if (descf.text.toString() != x.desc)
                                        bool = true
                                    if (janrf.text.toString() != x.janrs)
                                        bool = true
                                    if (bool) {
                                        button2.visibility = View.VISIBLE
                                        button2.setText("Обновить")
                                    }
                                    else
                                        button2.visibility = View.GONE
                                }

                                override fun afterTextChanged(s: Editable) {
                                    // Этот метод вызывается после того, как текст был изменен
                                }
                            })
                            descf.addTextChangedListener(object : TextWatcher {

                                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                                    // Этот метод вызывается перед изменением текста
                                }

                                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                                    var bool = false
                                    if (namef.text.toString() != x.name)
                                        bool = true
                                    if (athorf.text.toString() != x.author)
                                        bool = true
                                    if (descf.text.toString() != x.desc)
                                        bool = true
                                    if (janrf.text.toString() != x.janrs)
                                        bool = true
                                    if (bool) {
                                        button2.visibility = View.VISIBLE
                                        button2.setText("Обновить")
                                    }
                                    else
                                        button2.visibility = View.GONE
                                }

                                override fun afterTextChanged(s: Editable) {
                                    // Этот метод вызывается после того, как текст был изменен
                                }
                            })
                            janrf.addTextChangedListener(object : TextWatcher {

                                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                                    // Этот метод вызывается перед изменением текста
                                }

                                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                                    var bool = false
                                    if (namef.text.toString() != x.name)
                                        bool = true
                                    if (athorf.text.toString() != x.author)
                                        bool = true
                                    if (descf.text.toString() != x.desc)
                                        bool = true
                                    if (janrf.text.toString() != x.janrs)
                                        bool = true
                                    if (bool) {
                                        button2.visibility = View.VISIBLE
                                        button2.setText("Обновить")
                                    }
                                    else
                                        button2.visibility = View.GONE
                                }

                                override fun afterTextChanged(s: Editable) {
                                    // Этот метод вызывается после того, как текст был изменен
                                }
                            })
                        }
                    }
                }
                else{
                    r.lifecycleScope.launch(Dispatchers.IO) {
                        x.name = namef.text.toString()
                        x.author = athorf.text.toString()
                        x.desc = descf.text.toString()
                        x.janrs = janrf.text.toString()
                        bookrep.update(x)
                        withContext(Dispatchers.Main){
                            button2.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}
class recycleview() :
    RecyclerView.Adapter<recycleview.MyViewHolder>() {
    private val array = mutableListOf<book>()
    @SuppressLint("NotifyDataSetChanged")
    fun update(newlist : MutableList<book>){
        array.clear()
        array.addAll(newlist)
        notifyDataSetChanged()
    }
    var onbuttonclick : ((book) -> Unit)? = null
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout = itemView.findViewById<LinearLayout>(R.id.item)
        val athor = itemView.findViewById<TextView>(R.id.athor)
        val name = itemView.findViewById<TextView>(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.book, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("ResourceAsColor", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
        holder.apply {
            name.setText(array[i].name)
            athor.setText(array[i].author)
            layout.setOnClickListener {
                onbuttonclick?.invoke(array[i])
            }
        }
    }

    override fun getItemCount(): Int {
        return array.size

    }
}
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val bd by lazy { booksdb.bookdbfunc(application).gdao() }
    private val bookrep: BookRepository by lazy { repositoryimp(bd) }
    private val booksdata by lazy { bookrep.getbookslived() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val books = mutableListOf<book>()

        binding.apply {
            button.setText("Добавить книгу")
            recyclerviewitem.layoutManager = LinearLayoutManager(application)
            recyclerviewitem.adapter = recycleview()
            val adapter = recyclerviewitem.adapter as recycleview
            editTextTextPersonName.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    // Этот метод вызывается перед изменением текста
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val x = mutableListOf<book>()
                    for (i in books)
                        if (s.toString() in i.name || s.toString()in i.author || s.toString() in i.desc || s.toString() in i.janrs)
                            x.add(i)
                    if (s.toString() != "")
                        adapter.update(x)
                    else
                        adapter.update(books)
                }

                override fun afterTextChanged(s: Editable) {
                    // Этот метод вызывается после того, как текст был изменен
                }
            })
            booksdata.observe(this@MainActivity){
                books.clear()
                books.addAll(it)
                adapter.update(books)
            }
            adapter.onbuttonclick= {x ->
                val b = BookBottomSheetFragment(this@MainActivity,x)
                b.show(supportFragmentManager,"")
            }
            button.setOnClickListener {
                val b = BookBottomSheetFragment(this@MainActivity,book())
                b.show(supportFragmentManager,"")
            }
        }

    }
}