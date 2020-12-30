package com.prateekj.snooper.dbreader

import android.content.Context
import com.prateekj.snooper.dbreader.model.Database
import com.prateekj.snooper.dbreader.view.DbReaderCallback
import com.prateekj.snooper.infra.BackgroundTask
import com.prateekj.snooper.infra.BackgroundTaskExecutor
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.CustomTypeSafeMatcher
import org.hamcrest.Matcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.File

class DatabaseReaderTest {
  private lateinit var context: Context
  private lateinit var applicationContext: Context
  private lateinit var backgroundTaskExecutor: BackgroundTaskExecutor
  private lateinit var dbReaderCallback: DbReaderCallback
  private lateinit var databaseDataReader: DatabaseDataReader
  private lateinit var databaseReader: DatabaseReader

  @Before
  @Throws(Exception::class)
  fun setUp() {
    context = mockk(relaxed = true)
    applicationContext = mockk(relaxed = true)
    backgroundTaskExecutor = mockk(relaxed = true)
    dbReaderCallback = mockk(relaxed = true)
    databaseDataReader = mockk(relaxed = true)
    every { context.applicationContext } returns applicationContext
    databaseReader = DatabaseReader(context, backgroundTaskExecutor, databaseDataReader)
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnEmptyListIfNoDbFilesPresent() {
    val databases = arrayOfNulls<String>(0)
    every { applicationContext.databaseList() } returns databases
    resolveBackgroundTask()

    databaseReader.fetchApplicationDatabases(dbReaderCallback)

    io.mockk.verify { dbReaderCallback.onApplicationDbFetchCompleted(match { it.isEmpty() }) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnEmptyListIfDatabaseListIsNull() {
    every { applicationContext.databaseList() } returns null
    resolveBackgroundTask()

    databaseReader.fetchApplicationDatabases(dbReaderCallback)

    io.mockk.verify { dbReaderCallback.onApplicationDbFetchCompleted(match { it.isEmpty() }) }
  }

  @Test
  @Throws(Exception::class)
  fun shouldFilterOutListOfDbFilesPresent() {
    val databases = arrayOf("app.db", "user.db", "app.journal", "/app.tmp", "snooper.db")
    every { applicationContext.databaseList() } returns databases
    val file1 = getMockFile("/location1/app.db", "app.db")
    val file2 = getMockFile("/location2/user.db", "user.db")

    every { context.getDatabasePath("app.db") } returns file1
    every { context.getDatabasePath("user.db") } returns file2
    resolveBackgroundTask()

    databaseReader.fetchApplicationDatabases(dbReaderCallback)


    io.mockk.verify {
      dbReaderCallback.onApplicationDbFetchCompleted(match {
        assertEquals(it.size, 2)
        assertThat(it[0], isSameAsDatabaseWithParameters("/location1/app.db", "app.db"))
        assertThat(it[1], isSameAsDatabaseWithParameters("/location2/user.db", "user.db"))
        true
      })
    }
  }

  private fun isSameAsDatabaseWithParameters(path: String, name: String): Matcher<Database> {
    return object : CustomTypeSafeMatcher<Database>("a Database with name $name") {
      public override fun matchesSafely(item: Database): Boolean {
        return name == item.name && path == item.path
      }
    }
  }

  private fun getMockFile(path: String, dbName: String): File {
    val file = mockk<File>(relaxed = true)
    every { file.absolutePath } returns path
    every { file.name } returns dbName
    return file
  }

  private fun resolveBackgroundTask() {
    every { backgroundTaskExecutor.execute(any<BackgroundTask<String>>()) } answers {
      val backgroundTask = this.firstArg<BackgroundTask<String>>()
      backgroundTask.onResult(backgroundTask.onExecute())
    }
  }
}