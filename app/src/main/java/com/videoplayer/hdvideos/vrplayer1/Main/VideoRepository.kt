package com.videoplayer.hdvideos.vrplayer1.Main

import android.content.Context
import android.util.Log
import com.videoplayer.hdvideos.vrplayer1.utils.UrlUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

object VideoRepository {

    private const val TAG = "VideoRepository"
    private const val SUPABASE_URL =
        "https://jrqmyfznfgoadoerjqco.supabase.co/rest/v1/videos?app=eq.vr2&active=eq.true&order=sort_order.asc"
    private const val API_KEY = "sb_publishable_LZHiU8Ov86ocBHg35ukQSA_5xoC1tHv"

    private val categoryMap = LinkedHashMap<String, MutableList<VideoItem>>()
    private var cachedTrending: List<VideoItem> = emptyList()
    private var cachedCategoryThumbs: Map<String, String> = emptyMap()
    private var isInitialized = false
    private var isFetching = false

    fun isReady(): Boolean = synchronized(categoryMap) { isInitialized && categoryMap.isNotEmpty() }

    fun prewarm(context: Context) {
        if (isReady()) return
        CoroutineScope(Dispatchers.IO).launch {
            synchronized(categoryMap) {
                if (!isInitialized) {
                    loadFromAssetOrCache(context.applicationContext)
                    isInitialized = true
                }
            }
            fetchFromRemote(context.applicationContext, null)
        }
    }

    fun init(context: Context, onUpdated: (() -> Unit)? = null) {
        if (isReady()) {
            onUpdated?.invoke()
            fetchFromRemote(context.applicationContext, onUpdated)
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            synchronized(categoryMap) {
                if (!isInitialized) {
                    loadFromAssetOrCache(context.applicationContext)
                    isInitialized = true
                }
            }
            withContext(Dispatchers.Main) {
                onUpdated?.invoke()
            }
            fetchFromRemote(context.applicationContext, onUpdated)
        }
    }

    private fun loadFromAssetOrCache(context: Context) {
        try {
            val cacheFile = File(context.filesDir, "supabase_videos_cache.json")
            val jsonText = if (cacheFile.exists() && cacheFile.length() > 0) {
                cacheFile.readText()
            } else {
                context.assets.open("videolinks_categorized.json").bufferedReader().use { it.readText() }
            }
            parseCategorizedJson(jsonText)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading local videos: ${e.message}", e)
        }
    }

    private fun parseCategorizedJson(jsonText: String) {
        val root = JSONObject(jsonText)
        synchronized(categoryMap) {
            categoryMap.clear()
            for (category in root.keys()) {
                val array = root.getJSONArray(category)
                val list = mutableListOf<VideoItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        VideoItem(
                            title = obj.optString("Titles"),
                            thumbnail = UrlUtils.encode(obj.optString("Thumbnails")),
                            videoUrl = UrlUtils.encode(obj.optString("Videos")),
                            timeDuration = obj.optString("Time Duration", "2:00 Min"),
                            category = category,
                            description = obj.optString("Description", "")
                        )
                    )
                }
                categoryMap[category] = list
            }
            injectFigmaShowcaseItems(categoryMap)
            updateCachedViews()
        }
    }

    private fun updateCachedViews() {
        cachedTrending = categoryMap.values.flatten()
            .shuffled()
            .distinctBy { it.title }
        cachedCategoryThumbs = categoryMap.mapValues { (_, vids) ->
            vids.randomOrNull()?.thumbnail ?: ""
        }
    }

    private fun injectFigmaShowcaseItems(map: MutableMap<String, MutableList<VideoItem>>) {
        val targetCatKey = map.keys.firstOrNull { it.contains("roller", ignoreCase = true) } ?: "Roller Coaster"
        val list = map.getOrPut(targetCatKey) { mutableListOf() }
        val figmaItems = listOf(
            VideoItem(
                title = "Rollar Coaster Siren Head",
                thumbnail = "drawable/thumb_roller_coaster_1",
                videoUrl = "https://video.aivigilmsp.co.uk/vr1/17921.mp4",
                timeDuration = "02:35",
                category = targetCatKey,
                description = "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout."
            ),
            VideoItem(
                title = "BUSCH Gardens 360 VR BOX",
                thumbnail = "drawable/thumb_busch_gardens",
                videoUrl = "https://video.aivigilmsp.co.uk/vr1/17921.mp4",
                timeDuration = "2m 15s",
                category = targetCatKey,
                description = "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout."
            ),
            VideoItem(
                title = "Rollar Coaster Siren Head",
                thumbnail = "drawable/thumb_roller_coaster_2",
                videoUrl = "https://video.aivigilmsp.co.uk/vr1/17921.mp4",
                timeDuration = "02:35",
                category = targetCatKey,
                description = "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout."
            ),
            VideoItem(
                title = "Rollar Coaster Siren Head",
                thumbnail = "drawable/thumb_roller_coaster_3",
                videoUrl = "https://video.aivigilmsp.co.uk/vr1/17921.mp4",
                timeDuration = "02:35",
                category = targetCatKey,
                description = "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout."
            )
        )
        val realUrls = list.filter { !it.thumbnail.startsWith("drawable/") }.map { it.videoUrl }
        val shown = figmaItems.mapIndexedNotNull { i, f ->
            realUrls.getOrNull(i)?.let { f.copy(videoUrl = it) }
        }
        list.removeAll { it.thumbnail.startsWith("drawable/") }
        list.addAll(0, shown)

        map.entries.firstOrNull { it.key.contains("jurassic", ignoreCase = true) }
            ?.value?.removeAll { it.thumbnail.startsWith("drawable/") }
    }

    fun fetchFromRemote(context: Context, onUpdated: (() -> Unit)? = null) {
        if (isFetching) return
        isFetching = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url(SUPABASE_URL)
                    .addHeader("apikey", API_KEY)
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string()

                if (response.isSuccessful && !body.isNullOrEmpty()) {
                    val jsonArray = JSONArray(body)
                    val newMap = LinkedHashMap<String, MutableList<VideoItem>>()

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val cat = item.optString("category", "Explore").ifEmpty { "Explore" }
                        val sec = item.optInt("duration_sec", 0)
                        val durationStr = if (sec > 0) {
                            "${sec / 60}:${(sec % 60).toString().padStart(2, '0')} Min"
                        } else {
                            "2:30 Min"
                        }

                        val videoItem = VideoItem(
                            title = item.optString("title"),
                            thumbnail = UrlUtils.encode(item.optString("poster_url")),
                            videoUrl = UrlUtils.encode(item.optString("video_url")),
                            timeDuration = durationStr,
                            category = cat,
                            description = item.optString("description", "")
                        )

                        if (!newMap.containsKey(cat)) {
                            newMap[cat] = mutableListOf()
                        }
                        newMap[cat]?.add(videoItem)
                    }

                    if (newMap.isNotEmpty()) {
                        synchronized(categoryMap) {
                            categoryMap.clear()
                            categoryMap.putAll(newMap)
                            injectFigmaShowcaseItems(categoryMap)
                            updateCachedViews()
                        }

                        // Save cache locally
                        val cacheJson = JSONObject()
                        for ((cat, vids) in newMap) {
                            val arr = JSONArray()
                            for (v in vids) {
                                val o = JSONObject()
                                o.put("Titles", v.title)
                                o.put("Thumbnails", v.thumbnail)
                                o.put("Videos", v.videoUrl)
                                o.put("Time Duration", v.timeDuration)
                                o.put("Description", v.description)
                                arr.put(o)
                            }
                            cacheJson.put(cat, arr)
                        }
                        try {
                            File(context.filesDir, "supabase_videos_cache.json").writeText(cacheJson.toString())
                        } catch (e: Exception) {
                            Log.e(TAG, "Cache write failed: ${e.message}")
                        }

                        withContext(Dispatchers.Main) {
                            onUpdated?.invoke()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "fetchFromRemote failed: ${e.message}", e)
            } finally {
                isFetching = false
            }
        }
    }

    fun getRandomVideos(count: Int = -1): List<VideoItem> {
        return if (count <= 0 || count >= cachedTrending.size) {
            cachedTrending
        } else {
            cachedTrending.take(count)
        }
    }

    fun getAllVideos(): List<VideoItem> = cachedTrending

    private val CATEGORY_ORDER = listOf(
        "animated", "gaming", "roller coaster", "jurassic"
    )

    fun getCategories(): List<String> = synchronized(categoryMap) {
        val keys = categoryMap.keys.toList()
        val priority = keys.filter { key ->
            CATEGORY_ORDER.any { key.lowercase().contains(it) }
        }.sortedBy { key ->
            CATEGORY_ORDER.indexOfFirst { key.lowercase().contains(it) }
        }
        val rest = keys.filter { key ->
            CATEGORY_ORDER.none { key.lowercase().contains(it) }
        }
        priority + rest
    }

    fun getVideosByCategory(category: String): List<VideoItem> = getVideosForCategory(category)

    fun getVideosForCategory(category: String): List<VideoItem> =
        synchronized(categoryMap) {
            categoryMap[category]
                ?: categoryMap.entries.firstOrNull {
                    it.key.equals(category, ignoreCase = true) ||
                    it.key.replace(" ", "_").equals(category, ignoreCase = true) ||
                    it.key.lowercase().contains(category.lowercase().replace("_", " ")) ||
                    category.lowercase().replace("_", " ").contains(it.key.lowercase())
                }?.value
                ?: emptyList()
        }

    fun getFirstThumbnailForCategory(category: String): String =
        cachedCategoryThumbs[category] ?: ""
}
