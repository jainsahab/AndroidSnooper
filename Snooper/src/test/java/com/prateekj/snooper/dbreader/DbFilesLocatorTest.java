package com.prateekj.snooper.dbreader;

import android.content.Context;

import com.prateekj.snooper.dbreader.model.Database;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DbFilesLocatorTest {
  private Context context;
  private Context applicationContext;
  private DbFilesLocator dbFilesLocator;

  @Before
  public void setUp() throws Exception {
    context = mock(Context.class);
    applicationContext = mock(Context.class);

    when(context.getApplicationContext()).thenReturn(applicationContext);
    dbFilesLocator = new DbFilesLocator(context);
  }

  @Test
  public void shouldReturnEmptyListIfNoDbFilesPresent() throws Exception {
    String[] databases = new String[0];
    when(applicationContext.databaseList()).thenReturn(databases);
    List<Database> applicationDatabases = dbFilesLocator.fetchApplicationDatabases();
    assertEquals(applicationDatabases.size(), 0);
  }


  @Test
  public void shouldFilterOutListOfDbFilesPresent() throws Exception {
    String[] databases = new String[] {"app.db", "user.db", "app.journal", "/app.tmp", "snooper.db"};
    when(applicationContext.databaseList()).thenReturn(databases);
    File file1 = mock(File.class);
    File file2 = mock(File.class);

    when(file1.getAbsolutePath()).thenReturn("/location1/app.db");
    when(file1.getName()).thenReturn("app.db");
    when(file2.getAbsolutePath()).thenReturn("/location2/user.db");
    when(file2.getName()).thenReturn("user.db");
    when(context.getDatabasePath("app.db")).thenReturn(file1);
    when(context.getDatabasePath("user.db")).thenReturn(file2);

    List<Database> applicationDatabases = dbFilesLocator.fetchApplicationDatabases();
    assertEquals(applicationDatabases.size(), 2);
    assertThat(applicationDatabases.get(0).getPath(), is("/location1/app.db"));
    assertThat(applicationDatabases.get(0).getName(), is("app.db"));
    assertThat(applicationDatabases.get(1).getPath(), is("/location2/user.db"));
    assertThat(applicationDatabases.get(1).getName(), is("user.db"));
  }
}