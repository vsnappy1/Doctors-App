package com.randos.doctorsapp.data.manager

import android.app.DownloadManager
import android.database.Cursor
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.time.Duration.Companion.seconds

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DownloadManagerImplTest {

    @MockK(relaxed = true)
    private lateinit var androidDownloadManager: DownloadManager

    private val dispatcher = Dispatchers.Unconfined

    private lateinit var downloadManager: com.randos.domain.manager.DownloadManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        downloadManager = DownloadManagerImpl(androidDownloadManager, dispatcher)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }


    @Test
    fun download_whenInvoked_shouldEmitDownloadProgress() = runTest {
        // Given
        val totalBytes = 100L
        val downloadedSoFar = 20L

        val cursor: Cursor = mockk()

        // Mock download manager to return cursor
        every { androidDownloadManager.enqueue(any()) } returns 1
        every { androidDownloadManager.query(any()) } returns cursor

        // Mock cursor column indices
        every { cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS) } returns 0
        every { cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES) } returns 1
        every { cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR) } returns 2

        // Mock cursor data
        every { cursor.moveToFirst() } returns true
        every { cursor.getInt(0) } returns DownloadManager.STATUS_RUNNING
        every { cursor.getLong(1) } returns totalBytes
        every { cursor.getLong(2) } returns downloadedSoFar

        // When
        val stream = downloadManager.download("https://www.google.com", "index", "html", {}, {})
        var percentage = 0
        var job: Job? = null
        job = launch {
            stream.collect {
                percentage = it
                job?.cancel()
            }
        }

        job.join()

        // Then
        assertEquals(20, percentage)
    }

    @Test
    fun download_whenDownloadCompletes_shouldInvokeOnDownloadSuccess() = runBlocking {
        // Given
        val cursor: Cursor = mockk()
        val onDownloadSuccess = mockk<(String) -> Unit>()
        val path = "src/file.pdf"

        // Mock download manager to return cursor
        every { androidDownloadManager.enqueue(any()) } returns 1
        every { androidDownloadManager.query(any()) } returns cursor
        every { onDownloadSuccess(any()) } just Runs

        // Mock cursor column indices
        every { cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS) } returns 0
        every { cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI) } returns 1

        // Mock cursor data
        every { cursor.moveToFirst() } returns true
        every { cursor.getInt(0) } returns DownloadManager.STATUS_SUCCESSFUL
        every { cursor.getString(1) } returns path

        // When
        val stream = downloadManager.download("https://www.google.com", "index", "html", onDownloadSuccess, {})
        val percentage = stream.first()

        // Then
        assertEquals(100, percentage)
        verify { onDownloadSuccess(path) }
    }

    @Test
    fun download_whenDownloadFails_shouldInvokeOnDownloadFailed() = runBlocking {
        // Given
        val cursor: Cursor = mockk()
        val onDownloadFailed = mockk<(Exception) -> Unit>()

        // Mock download manager to return cursor
        every { androidDownloadManager.enqueue(any()) } returns 1
        every { androidDownloadManager.query(any()) } returns cursor
        every { onDownloadFailed(any()) } just Runs

        // Mock cursor column indices
        every { cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS) } returns 0

        // Mock cursor data
        every { cursor.moveToFirst() } returns true
        every { cursor.getInt(0) } returns DownloadManager.STATUS_FAILED

        // When
        val stream = downloadManager.download("https://www.google.com", "index", "html", {}, onDownloadFailed)
        val job: Job?
        job = launch {
            stream.collect {}
        }
        delay(1.seconds)
        job.cancel()

        // Then
        verify { onDownloadFailed(any()) }
    }
}