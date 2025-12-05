package com.aquaspoof.unified.toolkit.mcpe
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
@Parcelize
data class ConfigItem(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("version") val version: String,
    @SerializedName("variableVersion") val variableVersion: String? = null,
    @SerializedName("authors") val authors: List<String>? = emptyList(),
    @SerializedName("github") val github: String?,
    @SerializedName("websites") val websites: List<String>? = emptyList(),
    @SerializedName("description") val description: String,
    @SerializedName("license") val license: String,
    @SerializedName("filename") val filename: String,
    @SerializedName("downloadCount") val downloadCount: Int,
    @SerializedName("fileSize") val fileSize: Long,
    @SerializedName("downloadUrl") val downloadUrl: String,
    @SerializedName("md5") val md5: String?,
    @SerializedName("sha1") val sha1: String?,
    @SerializedName("sha256") val sha256: String?,
    @SerializedName("crc32") val crc32: String?
) : Parcelable