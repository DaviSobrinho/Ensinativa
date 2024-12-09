package com.ensinativapackage.ensinativa.viewmodel

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.storage.StorageReference
import java.io.InputStream

class StorageReferenceModelLoader : ModelLoader<StorageReference, InputStream> {

    override fun buildLoadData(
        model: StorageReference,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream> {
        return ModelLoader.LoadData(ObjectKey(model), StorageReferenceFetcher(model))
    }

    override fun handles(model: StorageReference): Boolean {
        return true
    }

    class Factory : ModelLoaderFactory<StorageReference, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<StorageReference, InputStream> {
            return StorageReferenceModelLoader()
        }

        override fun teardown() {
            // Nothing
        }
    }
    private class StorageReferenceFetcher(private val storageReference: StorageReference) :
        DataFetcher<InputStream> {

        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
            storageReference.stream
                .addOnSuccessListener { taskSnapshot ->
                    // Assuming that the InputStream can be obtained from the taskSnapshot
                    val inputStream = taskSnapshot.stream
                    callback.onDataReady(inputStream)
                }
                .addOnFailureListener { e ->
                    callback.onLoadFailed(e)
                }
        }

        override fun cleanup() {
            // Nothing
        }

        override fun cancel() {
            // Nothing
        }

        override fun getDataClass(): Class<InputStream> {
            return InputStream::class.java
        }

        override fun getDataSource(): DataSource {
            return DataSource.REMOTE
        }
    }
}
