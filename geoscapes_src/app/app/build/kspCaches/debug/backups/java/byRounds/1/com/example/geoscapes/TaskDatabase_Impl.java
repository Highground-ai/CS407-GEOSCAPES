package com.example.geoscapes;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TaskDatabase_Impl extends TaskDatabase {
  private volatile TaskDao _taskDao;

  private volatile StepDao _stepDao;

  private volatile DeleteDao _deleteDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(5) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `Task` (`taskId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `taskName` TEXT NOT NULL, `taskDescription` TEXT, `taskCompletion` REAL NOT NULL, `location` TEXT NOT NULL, `radius` INTEGER NOT NULL, `storyline` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `Step` (`stepId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `stepName` TEXT NOT NULL, `stepDescription` TEXT, `stepCompletion` INTEGER NOT NULL, `activityId` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `TaskStepRelation` (`taskId` INTEGER NOT NULL, `stepId` INTEGER NOT NULL, PRIMARY KEY(`taskId`, `stepId`), FOREIGN KEY(`taskId`) REFERENCES `Task`(`taskId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`stepId`) REFERENCES `Step`(`stepId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '16ce4d902bb3f5591b7bbf1323ed4dac')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `Task`");
        db.execSQL("DROP TABLE IF EXISTS `Step`");
        db.execSQL("DROP TABLE IF EXISTS `TaskStepRelation`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsTask = new HashMap<String, TableInfo.Column>(7);
        _columnsTask.put("taskId", new TableInfo.Column("taskId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTask.put("taskName", new TableInfo.Column("taskName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTask.put("taskDescription", new TableInfo.Column("taskDescription", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTask.put("taskCompletion", new TableInfo.Column("taskCompletion", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTask.put("location", new TableInfo.Column("location", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTask.put("radius", new TableInfo.Column("radius", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTask.put("storyline", new TableInfo.Column("storyline", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTask = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTask = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTask = new TableInfo("Task", _columnsTask, _foreignKeysTask, _indicesTask);
        final TableInfo _existingTask = TableInfo.read(db, "Task");
        if (!_infoTask.equals(_existingTask)) {
          return new RoomOpenHelper.ValidationResult(false, "Task(com.example.geoscapes.Task).\n"
                  + " Expected:\n" + _infoTask + "\n"
                  + " Found:\n" + _existingTask);
        }
        final HashMap<String, TableInfo.Column> _columnsStep = new HashMap<String, TableInfo.Column>(5);
        _columnsStep.put("stepId", new TableInfo.Column("stepId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStep.put("stepName", new TableInfo.Column("stepName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStep.put("stepDescription", new TableInfo.Column("stepDescription", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStep.put("stepCompletion", new TableInfo.Column("stepCompletion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStep.put("activityId", new TableInfo.Column("activityId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStep = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesStep = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoStep = new TableInfo("Step", _columnsStep, _foreignKeysStep, _indicesStep);
        final TableInfo _existingStep = TableInfo.read(db, "Step");
        if (!_infoStep.equals(_existingStep)) {
          return new RoomOpenHelper.ValidationResult(false, "Step(com.example.geoscapes.Step).\n"
                  + " Expected:\n" + _infoStep + "\n"
                  + " Found:\n" + _existingStep);
        }
        final HashMap<String, TableInfo.Column> _columnsTaskStepRelation = new HashMap<String, TableInfo.Column>(2);
        _columnsTaskStepRelation.put("taskId", new TableInfo.Column("taskId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTaskStepRelation.put("stepId", new TableInfo.Column("stepId", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTaskStepRelation = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysTaskStepRelation.add(new TableInfo.ForeignKey("Task", "CASCADE", "NO ACTION", Arrays.asList("taskId"), Arrays.asList("taskId")));
        _foreignKeysTaskStepRelation.add(new TableInfo.ForeignKey("Step", "CASCADE", "NO ACTION", Arrays.asList("stepId"), Arrays.asList("stepId")));
        final HashSet<TableInfo.Index> _indicesTaskStepRelation = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTaskStepRelation = new TableInfo("TaskStepRelation", _columnsTaskStepRelation, _foreignKeysTaskStepRelation, _indicesTaskStepRelation);
        final TableInfo _existingTaskStepRelation = TableInfo.read(db, "TaskStepRelation");
        if (!_infoTaskStepRelation.equals(_existingTaskStepRelation)) {
          return new RoomOpenHelper.ValidationResult(false, "TaskStepRelation(com.example.geoscapes.TaskStepRelation).\n"
                  + " Expected:\n" + _infoTaskStepRelation + "\n"
                  + " Found:\n" + _existingTaskStepRelation);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "16ce4d902bb3f5591b7bbf1323ed4dac", "09d63d7891e0e50e983404e566327c83");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "Task","Step","TaskStepRelation");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `Task`");
      _db.execSQL("DELETE FROM `Step`");
      _db.execSQL("DELETE FROM `TaskStepRelation`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(TaskDao.class, TaskDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(StepDao.class, StepDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DeleteDao.class, DeleteDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public TaskDao taskDao() {
    if (_taskDao != null) {
      return _taskDao;
    } else {
      synchronized(this) {
        if(_taskDao == null) {
          _taskDao = new TaskDao_Impl(this);
        }
        return _taskDao;
      }
    }
  }

  @Override
  public StepDao stepDao() {
    if (_stepDao != null) {
      return _stepDao;
    } else {
      synchronized(this) {
        if(_stepDao == null) {
          _stepDao = new StepDao_Impl(this);
        }
        return _stepDao;
      }
    }
  }

  @Override
  public DeleteDao deleteDao() {
    if (_deleteDao != null) {
      return _deleteDao;
    } else {
      synchronized(this) {
        if(_deleteDao == null) {
          _deleteDao = new DeleteDao_Impl(this);
        }
        return _deleteDao;
      }
    }
  }
}
