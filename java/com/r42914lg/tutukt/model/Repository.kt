package com.r42914lg.tutukt.model

import android.app.Application
import android.content.Context
import com.r42914lg.tutukt.Constants
import com.r42914lg.tutukt.domain.Category
import com.r42914lg.tutukt.domain.CategoryDetailed
import com.r42914lg.tutukt.service.RestClient
import kotlinx.coroutines.*
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*

interface Repository {
    suspend fun getCategories() : List<Category>?
    suspend fun getCategoryDetails(id: Int): CategoryDetailed?
    suspend fun saveCategoriesToFile(list: List<Category>)
    suspend fun saveDetailsToFile(categoryDetailed: CategoryDetailed)
}

class RepoWrapper(private val vm: TuTuViewModel,
                  private val api: Repository,
                  private val local: Repository) : Repository {

    override suspend fun getCategories(): List<Category>? {
        return if (vm.checkIfOnline)
            api.getCategories()
        else
            local.getCategories()
    }

    override suspend fun getCategoryDetails(id: Int): CategoryDetailed? {
        return if (vm.checkIfOnline)
            api.getCategoryDetails(id)
        else
            local.getCategoryDetails(id)
    }

    override suspend fun saveCategoriesToFile(list: List<Category>) {
        local.saveCategoriesToFile(list)
    }

    override suspend fun saveDetailsToFile(categoryDetailed: CategoryDetailed) {
        local.saveDetailsToFile(categoryDetailed)
    }
}

class APIRepository : Repository {

    override suspend fun getCategories(): List<Category>? {
        return RestClient.getApi()
                .getCategories(Constants.CATEGORIES_TO_RETURN,
                    Random().nextInt(Constants.OFFSET_MAX)).body()
    }

    override suspend fun getCategoryDetails(id: Int): CategoryDetailed? {
        return RestClient.getApi()
            .getDetailedCategory(id).body()
    }

    override suspend fun saveCategoriesToFile(list: List<Category>) {}
    override suspend fun saveDetailsToFile(categoryDetailed: CategoryDetailed) {}
}

class LocalRepository(private val app: Application) : Repository {

    override suspend fun getCategories(): List<Category>? {
        return coroutineScope {

            var categoryList: List<Category>?

            try {
                val jsonString = readFileFromAsset(Constants.LOCAL_FILE_NAME_FEED)
                val jsonArray = JSONArray(jsonString)

                categoryList = ArrayList<Category>(Constants.CATEGORIES_TO_RETURN)
                for (i in 0 until jsonArray.length()) {
                    val c: Category = RestClient.gson().fromJson(
                        jsonArray.getJSONObject(i).toString(),
                        Category::class.java
                    )
                    categoryList.add(c)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                categoryList = null
            }

            categoryList
        }
    }

    override suspend fun getCategoryDetails(id: Int): CategoryDetailed? {
        return coroutineScope {
            var categoryDetailed: CategoryDetailed? = null
            try {
                val jsonString = readFileFromAsset(Constants.LOCAL_FILE_NAME_DETAILS)
                categoryDetailed = RestClient.gson().fromJson(jsonString, CategoryDetailed::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            categoryDetailed
        }
    }

    override suspend fun saveCategoriesToFile(list: List<Category>) {
        coroutineScope {
            val buffer = StringBuffer("[")

            for (i in list.indices) {
                if (i > 0) {
                    buffer.append(",")
                }
                buffer.append(RestClient.gson().toJson(list[i]))
            }

            buffer.append("]")

            try {
                val fos = app.openFileOutput(Constants.LOCAL_FILE_NAME_FEED, Context.MODE_PRIVATE)
                fos.write(buffer.toString().toByteArray())
                fos.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun saveDetailsToFile(categoryDetailed: CategoryDetailed) {
        try {
            val fos = app.openFileOutput(Constants.LOCAL_FILE_NAME_DETAILS, Context.MODE_PRIVATE)
            fos.write(RestClient.gson().toJson(categoryDetailed).toByteArray())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun readFileFromAsset(fullPath: String): String {
        val inputStream: InputStream = app.openFileInput(fullPath)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)

        inputStream.read(buffer)

        return String(buffer, StandardCharsets.UTF_8)
    }
}