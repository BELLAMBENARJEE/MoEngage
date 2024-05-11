package  com.example.myapplication.data

import com.google.gson.annotations.SerializedName


data class ArticleResponse(
    @SerializedName("status") var status: String? = null,
    @SerializedName("articles") var articles: ArrayList<Articles> = arrayListOf()
)

data class Articles(
    @SerializedName("source") var source: Source? = Source(),
    @SerializedName("author") var author: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("urlToImage") var urlToImage: String? = null,
    @SerializedName("publishedAt") var publishedAt: String? = null,
    @SerializedName("content") var content: String? = null
)

data class Source(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null
)

data class ApiResponse<T>(
    val responseCode: Int,
    val response: T
)