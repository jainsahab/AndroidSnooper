package com.prateekj.snooper.dbreader;

import android.content.Context;

import com.prateekj.snooper.dbreader.model.Database;
import com.prateekj.snooper.dbreader.view.DbReaderCallback;
import com.prateekj.snooper.infra.BackgroundTask;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseReaderTest {
  @Mock
  private Context context;
  @Mock
  private Context applicationContext;
  @Mock
  private BackgroundTaskExecutor backgroundTaskExecutor;
  @Mock
  private DbReaderCallback dbReaderCallback;
  @Captor
  private ArgumentCaptor<List<Database>> dbCaptor;
  private DatabaseReader databaseReader;

  @Before
  public void setUp() throws Exception {
    when(context.getApplicationContext()).thenReturn(applicationContext);
    databaseReader = new DatabaseReader(context, backgroundTaskExecutor, dbReaderCallback);
  }

  @Test
  public void shouldReturnEmptyListIfNoDbFilesPresent() throws Exception {
    String[] databases = new String[0];
    when(applicationContext.databaseList()).thenReturn(databases);
    resolveBackgroundTask();

    databaseReader.fetchApplicationDatabases();

    verify(dbReaderCallback).onDbFetchCompleted(dbCaptor.capture());
    List<Database> databasesList = dbCaptor.getValue();
    assertEquals(databasesList.size(), 0);
  }

  @Test
  public void shouldReturnEmptyListIfDatabaseListIsNull() throws Exception {
    when(applicationContext.databaseList()).thenReturn(null);
    resolveBackgroundTask();

    databaseReader.fetchApplicationDatabases();

    verify(dbReaderCallback).onDbFetchCompleted(dbCaptor.capture());
    List<Database> databasesList = dbCaptor.getValue();
    assertEquals(databasesList.size(), 0);
  }


  @Test
  public void shouldFilterOutListOfDbFilesPresent() throws Exception {
    String[] databases = new String[]{"app.db", "user.db", "app.journal", "/app.tmp", "snooper.db"};
    when(applicationContext.databaseList()).thenReturn(databases);
    File file1 = mock(File.class);
    File file2 = mock(File.class);

    when(file1.getAbsolutePath()).thenReturn("/location1/app.db");
    when(file1.getName()).thenReturn("app.db");
    when(file2.getAbsolutePath()).thenReturn("/location2/user.db");
    when(file2.getName()).thenReturn("user.db");
    when(context.getDatabasePath("app.db")).thenReturn(file1);
    when(context.getDatabasePath("user.db")).thenReturn(file2);
    resolveBackgroundTask();

    databaseReader.fetchApplicationDatabases();
    verify(dbReaderCallback).onDbFetchCompleted(dbCaptor.capture());
    List<Database> applicationDatabases = dbCaptor.getValue();

    assertEquals(applicationDatabases.size(), 2);
    assertThat(applicationDatabases.get(0).getPath(), is("/location1/app.db"));
    assertThat(applicationDatabases.get(0).getName(), is("app.db"));
    assertThat(applicationDatabases.get(1).getPath(), is("/location2/user.db"));
    assertThat(applicationDatabases.get(1).getName(), is("user.db"));
  }

  private void resolveBackgroundTask() {
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        BackgroundTask<String> backgroundTask = (BackgroundTask<String>) invocation.getArguments()[0];
        backgroundTask.onResult(backgroundTask.onExecute());
        return null;
      }
    }).when(backgroundTaskExecutor).execute(any(BackgroundTask.class));
  }
}