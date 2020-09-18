package ir.ham3da.darya

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.downloader.*
import ir.ham3da.darya.adaptors.GDBListAdaptor
import ir.ham3da.darya.adaptors.ScheduleGDB
import ir.ham3da.darya.ganjoor.GDBInfo
import ir.ham3da.darya.ganjoor.GDBList
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser
import ir.ham3da.darya.utility.*
import org.xmlpull.v1.XmlPullParserException
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.math.roundToInt

@Suppress("UNREACHABLE_CODE")
class ActivityCollection : AppCompatActivity() {
    var _DbBrowser: GanjoorDbBrowser? = null
    var GDBListAdaptor1: GDBListAdaptor? = null
    var recyclerViewCollection: RecyclerView? = null
    var simpleSwipeRefreshLayout: SwipeRefreshLayout? = null
    var MyDialogs1: MyDialogs? = null
    var dlPath: String? = null
    var searchView: SearchView? = null
    var searchViewHasFocus = false
    var TAG = "ActivityCollection"
    private var _MixedList: GDBList? = null
    var DlIndex = 0
    var sumDownloaded = 0

    var cancel_downloads: ImageButton? = null
    var progress_text: TextView? = null
    var progress_description: TextView? = null
    var progress_text1: TextView? = null
    var progress_text2: TextView? = null
    var progress_bar: ProgressBar? = null
    var scheduleGDBList: MutableList<ScheduleGDB>? = null
    var actionMode: ActionMode? = null
    fun showActionbar() {
        if (actionMode == null) {
            actionMode = startActionMode(callback)
        }
    }

    fun setActionbarTitle(title: String?) {
        if (actionMode != null) {
            actionMode!!.title = title
        }
    }

    private val callback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.audio_collection_menu, menu)
            return true
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_select_all -> {
                    if (GDBListAdaptor1 != null) {
                        if (GDBListAdaptor1!!.checkAnyBookIsSelected()) {
                            GDBListAdaptor1!!.selectAllItem(false)
                            item.setIcon(R.drawable.ic_outline_library_add_check_24)
                        } else {
                            GDBListAdaptor1!!.selectAllItem(true)
                            item.setIcon(R.drawable.ic_baseline_library_add_check_24_fill)
                        }
                    }
                    true
                }
                R.id.action_dl -> {
                    downloadMarkedBook()
                    true
                }
                R.id.action_delete -> {
                    deleteMarkedBook()
                    true
                }
                else -> false
            }
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
        }
    }

    private fun reloadRecycleView() {
        if (UtilFunctions.isNetworkConnected(this)) {
            loadItems()
        } else {
            finish()
            Toast.makeText(this, getString(R.string.internet_failed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchInRecycleView(findStr: String) {
        if (!findStr.isEmpty()) {
            if (_MixedList != null) {
                val _MixedListSearch = GDBList(_MixedList)
                _MixedListSearch._Items.clear()
                var Index = 0
                for (gdbInfo in _MixedList!!._Items) {
                    if (gdbInfo._CatName.contains(findStr)) {
                        Index++
                        gdbInfo._Index = Index
                        _MixedListSearch._Items.add(gdbInfo)
                    }
                }
                showGDBList(_MixedListSearch)
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(SetLanguage.wrap(newBase))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UtilFunctions.changeTheme(this, true)
        setContentView(R.layout.activity_collection)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.collections)
        recyclerViewCollection = findViewById(R.id.RecyclerViewCollection)
        _DbBrowser = GanjoorDbBrowser(this)

        simpleSwipeRefreshLayout = findViewById(R.id.simpleSwipeRefreshLayout)

        simpleSwipeRefreshLayout?.let { simpleSwipeRefreshLayout!!.setOnRefreshListener(OnRefreshListener { loadItems() }) }

        val linearLayoutManager = LinearLayoutManager(this)

        recyclerViewCollection?.let { recyclerViewCollection!!.setLayoutManager(linearLayoutManager) }

        var downloadRelativelayout: RelativeLayout? = null
        downloadRelativelayout = findViewById(R.id.download_RelativeLayout)

        if (downloadRelativelayout.isShown) {
            downloadRelativelayout.visibility = View.GONE
        }
        cancel_downloads = findViewById(R.id.cancel_downloads)
        progress_text = findViewById(R.id.progress_text)
        progress_description = findViewById(R.id.progress_description)
        progress_text1 = findViewById(R.id.progress_text1)
        progress_text2 = findViewById(R.id.progress_text2)
        progress_bar = findViewById(R.id.progress_bar)
        reloadRecycleView()
        dlPath = AppSettings.getDownloadPath(this)
        MyDialogs1 = MyDialogs(this)
        val config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build()
        PRDownloader.initialize(applicationContext, config)
    }

    private fun downloadMarkedBook() {
        scheduleGDBList = ArrayList()
        for (i in 0 until GDBListAdaptor1!!.itemCount) {
            if (GDBListAdaptor1!!.isSelected(i) && !GDBListAdaptor1!!.getBookExist(i) || GDBListAdaptor1!!.isSelected(i) && GDBListAdaptor1!!.getBookUpdateAvailable(i)) {
                val scheduleBook = GDBListAdaptor1!!.getScheduleBook(i)
                (scheduleGDBList as ArrayList<ScheduleGDB>).add(scheduleBook)
            }
        }
        sumDownloaded = 0
        downloadBooks(scheduleGDBList)
    }

    fun downloadBooks(scheduleBookList1: List<ScheduleGDB>?) {
        var downloadRelativelayout: RelativeLayout? = null
        downloadRelativelayout = findViewById(R.id.download_RelativeLayout)

        val totalDownloads = scheduleBookList1!!.size
        if (totalDownloads > 0) {
            if (downloadRelativelayout!!.visibility != View.VISIBLE) {
                downloadRelativelayout!!.visibility = View.VISIBLE
            }
            progress_bar!!.progress = 0
            progress_text!!.text = ""
            progress_description!!.text = ""
            progress_text1!!.text = ""
            progress_text2!!.text = ""
            cancel_downloads!!.setOnClickListener { v: View? ->
                PRDownloader.cancelAll()
                downloadRelativelayout.visibility = View.GONE
            }
            if (totalDownloads > sumDownloaded) {
                val scheduleGDB = scheduleBookList1[sumDownloaded]
                sumDownloaded++
                val fileName = scheduleGDB._FileName
                val des = String.format(Locale.getDefault(), "%d", sumDownloaded) + " / " + String.format(Locale.getDefault(), "%d", totalDownloads)
                val downloadRequestBuilder = PRDownloader.download(scheduleGDB._URL, dlPath, scheduleGDB._FileName)
                val finalSum = sumDownloaded
                val downloadRequest = downloadRequestBuilder.build()
                downloadRequest.downloadId = scheduleGDB._Pos
                downloadRequest.setOnProgressListener { progress: Progress ->
                    val percent = Math.round(progress.currentBytes.toDouble() / progress.totalBytes.toDouble() * 100).toInt()
                    if (percent > 0) {
                        val formatBytesCopied = getString(R.string.file_received) + " " + Formatter.formatFileSize(this@ActivityCollection, progress.currentBytes)
                        val formatFileLength = getString(R.string.file_size) + " " + Formatter.formatFileSize(this@ActivityCollection, progress.totalBytes)
                        val progressText = "$formatBytesCopied / $formatFileLength"
                        progress_bar!!.progress = percent
                        val percentStr = String.format(Locale.getDefault(), "%d", percent) + " %"
                        progress_text!!.text = percentStr
                        progress_description!!.text = des
                        progress_text1!!.text = formatBytesCopied
                        progress_text2!!.text = formatFileLength
                    }
                }
                        .start(object : OnDownloadListener {
                            override fun onDownloadComplete() {
                                val file = File("$dlPath/$fileName")
                                if (file.exists()) {
                                    if (scheduleGDB._DoUpdate) {
                                        _DbBrowser!!.DeletePoet(scheduleGDB._PoetID)
                                    }
                                    val imported = _DbBrowser!!.ImportGdb("$dlPath/$fileName", scheduleGDB._Update_info)
                                    if (imported) {
                                        GDBListAdaptor1!!.notifyNewImported(scheduleGDB._Pos, scheduleGDB._PoetID)
                                        try {
                                            val delete = file.delete()
                                        } catch (ex: Exception) {
                                            Log.e(TAG, "onPostExecute: " + ex.message)
                                        }
                                        val globalVariable = applicationContext as App
                                        globalVariable.updatePoetList = true
                                    }
                                    downloadBooks(scheduleGDBList)
                                }
                                if (finalSum >= totalDownloads) {
                                    var multi_dl = false
                                    if (totalDownloads > 1) {
                                        multi_dl = true
                                    }
                                    GDBListAdaptor1!!.notifyDataSetChanged()
                                    ShowSuccessDownloadToast(multi_dl)
                                    downloadRelativelayout.visibility = View.GONE
                                }
                            }

                            override fun onError(error: Error) {
                                Log.e("DownloadAudioTask", "ResponseCode: " +
                                        error.responseCode + ", get ServerError Message: " + error.serverErrorMessage +
                                        ", get Connection Exception:" + error.connectionException.message)
                                downloadFailToast()
                                PRDownloader.cancel(scheduleGDB._Pos)
                                downloadRelativelayout.visibility = View.GONE
                                GDBListAdaptor1!!.notifyDataSetChanged()
                            }
                        })
            }
        }
    }

    private fun deleteMarkedBook() {
        if (!GDBListAdaptor1!!.checkAnyBookIsSelected()) {
            return
        }
        val gdbInfos: MutableList<GDBInfo> = ArrayList()
        for (i in 0 until GDBListAdaptor1!!.itemCount) {
            if (GDBListAdaptor1!!.isSelected(i) && GDBListAdaptor1!!.getBookExist(i)) {
                val gdbInfo = GDBListAdaptor1!!.getGanjoorBookInfo(i)
                gdbInfos.add(gdbInfo)
            }
        }
        val MyDialogs1 = MyDialogs(this)
        val ques = getString(R.string.delete_all_au)
        val yesNoDialog = MyDialogs1.YesNoDialog(ques, getDrawable(R.drawable.ic_delete_white_24dp), true)
        val noBtn = yesNoDialog.findViewById<Button>(R.id.noBtn)
        noBtn.setOnClickListener { view: View? -> yesNoDialog.dismiss() }
        val yesBtn = yesNoDialog.findViewById<Button>(R.id.yesBtn)
        yesBtn.setOnClickListener { view: View? ->
            yesNoDialog.dismiss()
            val deleteBookFilesTask = DeleteBookFilesTask()
            val ganjoorBookInfoListArray = gdbInfos.toTypedArray()
            deleteBookFilesTask.execute(*ganjoorBookInfoListArray)
        }
        yesNoDialog.show()
    }

    private inner class DeleteBookFilesTask : AsyncTask<GDBInfo?, Int?, Int>() {
        override fun onPreExecute() {
            super.onPreExecute()

            var downloadRelativelayout: RelativeLayout? = null
            downloadRelativelayout = findViewById(R.id.download_RelativeLayout)

            downloadRelativelayout!!.visibility = View.VISIBLE
            progress_bar!!.progress = 0
            progress_text!!.text = ""
            progress_description!!.text = ""
            progress_text1!!.text = ""
            progress_text2!!.text = ""
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            val total = values[1]
            val current = values[0]

            val num1 = current?.toDouble()
           val num2 = total?.toDouble()

            val percent = ((num1?.div(num2!!))?.times(100))?.toInt()

            Log.e(TAG, "onProgressUpdate: $percent")

            if (percent != null) {
                if (percent > 0) {
                    progress_bar!!.progress = percent
                }
            }
            val percentStr = String.format(Locale.getDefault(), "%d", percent) + " %"
            progress_text!!.text = percentStr
            progress_description!!.setText(R.string.deleting)
            progress_text1!!.text = ""
            progress_text2!!.text = ""
        }

        override fun onPostExecute(result: Int) {
            var downloadRelativelayout: RelativeLayout? = null
            downloadRelativelayout = findViewById(R.id.download_RelativeLayout)

            downloadRelativelayout!!.visibility = View.GONE
            GDBListAdaptor1!!.notifyDataSetChanged()
        }

        init {

            cancel_downloads!!.setOnClickListener { v: View? -> cancel(true) }
        }

        override fun doInBackground(vararg p0: GDBInfo?): Int {
            return try {
                val count = p0.size
                for (i in 0 until count) {
                    val gdbInfo = p0[i]
                    GDBListAdaptor1!!.deleteItemMarked(gdbInfo)
                    publishProgress(i+1, count)
                }
                1
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("xml_Dl", "doInBackground: " + ex.message)
                -1
            }
        }

    }

    override fun onBackPressed() {
        if (searchViewHasFocus) {
            searchView!!.isIconified = true
        } else {
            super.onBackPressed()
            Bungee.slideDown(this) //fire the slide left animation
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.collection_menu, menu)
        val action_search = menu.findItem(R.id.action_search)
        searchView = action_search.actionView as SearchView
        searchView!!.queryHint = getString(R.string.enter_poet_name)
        try {
            val typeface = ResourcesCompat.getFont(this, R.font.iran_sans_mobile_light)
            val id = searchView!!.context
                    .resources
                    .getIdentifier("android:id/search_src_text", null, null)
            val textView = searchView!!.findViewById<TextView>(id)
            textView.setTypeface(typeface)
        } catch (ex: Exception) {
            Log.e(TAG, "msg: " + ex.message)
        }
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Toast like print
//                Toast.makeText(ActivityCollection.this, "SearchOnQueryTextSubmit: " + query, Toast.LENGTH_SHORT).show();
//                if (!searchView.isIconified()) {
//                    searchView.setIconified(true);
//                }
//                action_search.collapseActionView();
                searchInRecycleView(query.trim { it <= ' ' })
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })
        searchView!!.setOnQueryTextFocusChangeListener { v: View?, hasFocus: Boolean -> searchViewHasFocus = hasFocus }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> if (searchViewHasFocus) {
                searchView!!.isIconified = true
            } else {
                super.onBackPressed()
                Bungee.slideDown(this) //fire the slide left animation
            }
            R.id.reload_all -> reloadRecycleView()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadItems() {
        val downloadXmlTask: DownloadXmlTask = DownloadXmlTask()
        downloadXmlTask.execute(_Lists_Url[0])
    }

    private fun SetLists(XMLString: String?): Int {
        val res = 0
        if (XMLString != null && !XMLString.isEmpty()) {
            val InputStream1: InputStream = ByteArrayInputStream(XMLString.toByteArray(StandardCharsets.UTF_8))
            val lists: MutableList<GDBList> = LinkedList()
            var list: GDBList? = null
            try {
                list = GDBList.Build(0, InputStream1, _DbBrowser)
                if (list != null) {
                    lists.add(list)
                }
            } catch (e: IOException) {
                Log.e("IOException", "SetLists err: " + e.message)
                //  e.printStackTrace();
            } catch (e: XmlPullParserException) {
                Log.e("XmlPullParserException", "SetLists err: " + e.message)
                // e.printStackTrace();
            } catch (ex: Exception) {
                Log.e("Exception", "SetLists err: " + ex.message)
            }
            _MixedList = GDBList.Mix(lists)
        } else {
            Log.e("SetLists_Error", "SetLists err: " + getString(R.string.nothing_found))
        }
        return if (_MixedList != null) {
            1
        } else {
            0
        }
    }

    /**
     * Display a list of downloadable collections
     *
     * @param list GDBList
     */
    protected fun showGDBList(list: GDBList?) {
        GDBListAdaptor1 = GDBListAdaptor(list, this)
        recyclerViewCollection!!.adapter = GDBListAdaptor1
        recyclerViewCollection!!.scrollToPosition(0)
    }

    private inner class DownloadXmlTask : AsyncTask<String?, Int?, Int>() {
        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        override fun onPreExecute() {
            super.onPreExecute()
            if (_MixedList != null) {
                _MixedList = null
            }
            if (GDBListAdaptor1 != null) {
                GDBListAdaptor1!!.notifyDataSetChanged()
            }
            if (!simpleSwipeRefreshLayout!!.isRefreshing) {
                simpleSwipeRefreshLayout!!.isRefreshing = true
            }
        }

        override fun doInBackground(vararg urls: String?): Int {
            return try {
                val result = DownloadFromUrl.downloadDataFromUrl(urls[0], false)
                val res2 = SetLists(result)
                publishProgress(100)
                res2
            } catch (e: Exception) {
                Log.e("DownloadFromUrl", "doInBackground: " + e.message)
                -1
            }
        }


        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: Int) {
            if (simpleSwipeRefreshLayout!!.isRefreshing) {
                simpleSwipeRefreshLayout!!.isRefreshing = false
            }
            if (result == 1) {
                showGDBList(_MixedList)
            } else if (result == -1) {
                Toast.makeText(baseContext, getString(R.string.err_list_audio), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@ActivityCollection, getString(R.string.nothing_found), Toast.LENGTH_SHORT).show()
            }
        }


    }

    fun ShowSuccessDownloadToast(DownloadAll: Boolean) {
        if (DownloadAll) {
            Toast.makeText(this, getString(R.string.selected_collectons_added), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.success_add), Toast.LENGTH_SHORT).show()
        }
    }

    fun downloadFailToast() {
        Toast.makeText(this, getString(R.string.download_failed), Toast.LENGTH_SHORT).show()
    } //Downloader

    companion object {
        /**
         * نام فایلهای مجموعه های پیش فرض
         */
        private val _Lists_Url = arrayOf(
                "http://i.ganjoor.net/android/androidgdbs.xml" /*
            "sitegdbs.xml",
            "newgdbs.xml",
            "programgdbs.xml"*/
        )
    }
}