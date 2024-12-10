package com.example.geoscapes;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.google.android.gms.maps.model.LatLng;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TaskDao_Impl implements TaskDao {
  private final RoomDatabase __db;

  private final EntityUpsertionAdapter<Task> __upsertionAdapterOfTask;

  private final Converters __converters = new Converters();

  public TaskDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__upsertionAdapterOfTask = new EntityUpsertionAdapter<Task>(new EntityInsertionAdapter<Task>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `Task` (`taskId`,`taskName`,`taskDescription`,`taskCompletion`,`location`,`radius`,`storyline`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Task entity) {
        statement.bindLong(1, entity.getTaskId());
        statement.bindString(2, entity.getTaskName());
        if (entity.getTaskDescription() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getTaskDescription());
        }
        statement.bindDouble(4, entity.getTaskCompletion());
        final String _tmp = __converters.fromLatLng(entity.getLocation());
        statement.bindString(5, _tmp);
        statement.bindLong(6, entity.getRadius());
        if (entity.getStoryline() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getStoryline());
        }
      }
    }, new EntityDeletionOrUpdateAdapter<Task>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `Task` SET `taskId` = ?,`taskName` = ?,`taskDescription` = ?,`taskCompletion` = ?,`location` = ?,`radius` = ?,`storyline` = ? WHERE `taskId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Task entity) {
        statement.bindLong(1, entity.getTaskId());
        statement.bindString(2, entity.getTaskName());
        if (entity.getTaskDescription() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getTaskDescription());
        }
        statement.bindDouble(4, entity.getTaskCompletion());
        final String _tmp = __converters.fromLatLng(entity.getLocation());
        statement.bindString(5, _tmp);
        statement.bindLong(6, entity.getRadius());
        if (entity.getStoryline() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getStoryline());
        }
        statement.bindLong(8, entity.getTaskId());
      }
    });
  }

  @Override
  public Object upsert(final Task task, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfTask.upsert(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllTasks(final Continuation<? super List<Task>> $completion) {
    final String _sql = "SELECT * FROM Task";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Task>>() {
      @Override
      @NonNull
      public List<Task> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "taskId");
          final int _cursorIndexOfTaskName = CursorUtil.getColumnIndexOrThrow(_cursor, "taskName");
          final int _cursorIndexOfTaskDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "taskDescription");
          final int _cursorIndexOfTaskCompletion = CursorUtil.getColumnIndexOrThrow(_cursor, "taskCompletion");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfRadius = CursorUtil.getColumnIndexOrThrow(_cursor, "radius");
          final int _cursorIndexOfStoryline = CursorUtil.getColumnIndexOrThrow(_cursor, "storyline");
          final List<Task> _result = new ArrayList<Task>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Task _item;
            final int _tmpTaskId;
            _tmpTaskId = _cursor.getInt(_cursorIndexOfTaskId);
            final String _tmpTaskName;
            _tmpTaskName = _cursor.getString(_cursorIndexOfTaskName);
            final String _tmpTaskDescription;
            if (_cursor.isNull(_cursorIndexOfTaskDescription)) {
              _tmpTaskDescription = null;
            } else {
              _tmpTaskDescription = _cursor.getString(_cursorIndexOfTaskDescription);
            }
            final float _tmpTaskCompletion;
            _tmpTaskCompletion = _cursor.getFloat(_cursorIndexOfTaskCompletion);
            final LatLng _tmpLocation;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfLocation);
            _tmpLocation = __converters.toLatLng(_tmp);
            final int _tmpRadius;
            _tmpRadius = _cursor.getInt(_cursorIndexOfRadius);
            final String _tmpStoryline;
            if (_cursor.isNull(_cursorIndexOfStoryline)) {
              _tmpStoryline = null;
            } else {
              _tmpStoryline = _cursor.getString(_cursorIndexOfStoryline);
            }
            _item = new Task(_tmpTaskId,_tmpTaskName,_tmpTaskDescription,_tmpTaskCompletion,_tmpLocation,_tmpRadius,_tmpStoryline);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTaskById(final int taskId, final Continuation<? super Task> $completion) {
    final String _sql = "SELECT * FROM Task WHERE taskId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, taskId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Task>() {
      @Override
      @Nullable
      public Task call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "taskId");
          final int _cursorIndexOfTaskName = CursorUtil.getColumnIndexOrThrow(_cursor, "taskName");
          final int _cursorIndexOfTaskDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "taskDescription");
          final int _cursorIndexOfTaskCompletion = CursorUtil.getColumnIndexOrThrow(_cursor, "taskCompletion");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfRadius = CursorUtil.getColumnIndexOrThrow(_cursor, "radius");
          final int _cursorIndexOfStoryline = CursorUtil.getColumnIndexOrThrow(_cursor, "storyline");
          final Task _result;
          if (_cursor.moveToFirst()) {
            final int _tmpTaskId;
            _tmpTaskId = _cursor.getInt(_cursorIndexOfTaskId);
            final String _tmpTaskName;
            _tmpTaskName = _cursor.getString(_cursorIndexOfTaskName);
            final String _tmpTaskDescription;
            if (_cursor.isNull(_cursorIndexOfTaskDescription)) {
              _tmpTaskDescription = null;
            } else {
              _tmpTaskDescription = _cursor.getString(_cursorIndexOfTaskDescription);
            }
            final float _tmpTaskCompletion;
            _tmpTaskCompletion = _cursor.getFloat(_cursorIndexOfTaskCompletion);
            final LatLng _tmpLocation;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfLocation);
            _tmpLocation = __converters.toLatLng(_tmp);
            final int _tmpRadius;
            _tmpRadius = _cursor.getInt(_cursorIndexOfRadius);
            final String _tmpStoryline;
            if (_cursor.isNull(_cursorIndexOfStoryline)) {
              _tmpStoryline = null;
            } else {
              _tmpStoryline = _cursor.getString(_cursorIndexOfStoryline);
            }
            _result = new Task(_tmpTaskId,_tmpTaskName,_tmpTaskDescription,_tmpTaskCompletion,_tmpLocation,_tmpRadius,_tmpStoryline);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTaskByName(final String taskName, final Continuation<? super Task> $completion) {
    final String _sql = "SELECT * FROM Task WHERE taskName = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, taskName);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Task>() {
      @Override
      @Nullable
      public Task call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "taskId");
          final int _cursorIndexOfTaskName = CursorUtil.getColumnIndexOrThrow(_cursor, "taskName");
          final int _cursorIndexOfTaskDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "taskDescription");
          final int _cursorIndexOfTaskCompletion = CursorUtil.getColumnIndexOrThrow(_cursor, "taskCompletion");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfRadius = CursorUtil.getColumnIndexOrThrow(_cursor, "radius");
          final int _cursorIndexOfStoryline = CursorUtil.getColumnIndexOrThrow(_cursor, "storyline");
          final Task _result;
          if (_cursor.moveToFirst()) {
            final int _tmpTaskId;
            _tmpTaskId = _cursor.getInt(_cursorIndexOfTaskId);
            final String _tmpTaskName;
            _tmpTaskName = _cursor.getString(_cursorIndexOfTaskName);
            final String _tmpTaskDescription;
            if (_cursor.isNull(_cursorIndexOfTaskDescription)) {
              _tmpTaskDescription = null;
            } else {
              _tmpTaskDescription = _cursor.getString(_cursorIndexOfTaskDescription);
            }
            final float _tmpTaskCompletion;
            _tmpTaskCompletion = _cursor.getFloat(_cursorIndexOfTaskCompletion);
            final LatLng _tmpLocation;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfLocation);
            _tmpLocation = __converters.toLatLng(_tmp);
            final int _tmpRadius;
            _tmpRadius = _cursor.getInt(_cursorIndexOfRadius);
            final String _tmpStoryline;
            if (_cursor.isNull(_cursorIndexOfStoryline)) {
              _tmpStoryline = null;
            } else {
              _tmpStoryline = _cursor.getString(_cursorIndexOfStoryline);
            }
            _result = new Task(_tmpTaskId,_tmpTaskName,_tmpTaskDescription,_tmpTaskCompletion,_tmpLocation,_tmpRadius,_tmpStoryline);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCompletedTasks(final Continuation<? super List<Task>> $completion) {
    final String _sql = "SELECT * FROM Task WHERE taskCompletion = 100";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Task>>() {
      @Override
      @NonNull
      public List<Task> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "taskId");
          final int _cursorIndexOfTaskName = CursorUtil.getColumnIndexOrThrow(_cursor, "taskName");
          final int _cursorIndexOfTaskDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "taskDescription");
          final int _cursorIndexOfTaskCompletion = CursorUtil.getColumnIndexOrThrow(_cursor, "taskCompletion");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfRadius = CursorUtil.getColumnIndexOrThrow(_cursor, "radius");
          final int _cursorIndexOfStoryline = CursorUtil.getColumnIndexOrThrow(_cursor, "storyline");
          final List<Task> _result = new ArrayList<Task>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Task _item;
            final int _tmpTaskId;
            _tmpTaskId = _cursor.getInt(_cursorIndexOfTaskId);
            final String _tmpTaskName;
            _tmpTaskName = _cursor.getString(_cursorIndexOfTaskName);
            final String _tmpTaskDescription;
            if (_cursor.isNull(_cursorIndexOfTaskDescription)) {
              _tmpTaskDescription = null;
            } else {
              _tmpTaskDescription = _cursor.getString(_cursorIndexOfTaskDescription);
            }
            final float _tmpTaskCompletion;
            _tmpTaskCompletion = _cursor.getFloat(_cursorIndexOfTaskCompletion);
            final LatLng _tmpLocation;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfLocation);
            _tmpLocation = __converters.toLatLng(_tmp);
            final int _tmpRadius;
            _tmpRadius = _cursor.getInt(_cursorIndexOfRadius);
            final String _tmpStoryline;
            if (_cursor.isNull(_cursorIndexOfStoryline)) {
              _tmpStoryline = null;
            } else {
              _tmpStoryline = _cursor.getString(_cursorIndexOfStoryline);
            }
            _item = new Task(_tmpTaskId,_tmpTaskName,_tmpTaskDescription,_tmpTaskCompletion,_tmpLocation,_tmpRadius,_tmpStoryline);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getIncompleteTasks(final Continuation<? super List<Task>> $completion) {
    final String _sql = "SELECT * FROM Task WHERE taskCompletion < 100";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Task>>() {
      @Override
      @NonNull
      public List<Task> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "taskId");
          final int _cursorIndexOfTaskName = CursorUtil.getColumnIndexOrThrow(_cursor, "taskName");
          final int _cursorIndexOfTaskDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "taskDescription");
          final int _cursorIndexOfTaskCompletion = CursorUtil.getColumnIndexOrThrow(_cursor, "taskCompletion");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfRadius = CursorUtil.getColumnIndexOrThrow(_cursor, "radius");
          final int _cursorIndexOfStoryline = CursorUtil.getColumnIndexOrThrow(_cursor, "storyline");
          final List<Task> _result = new ArrayList<Task>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Task _item;
            final int _tmpTaskId;
            _tmpTaskId = _cursor.getInt(_cursorIndexOfTaskId);
            final String _tmpTaskName;
            _tmpTaskName = _cursor.getString(_cursorIndexOfTaskName);
            final String _tmpTaskDescription;
            if (_cursor.isNull(_cursorIndexOfTaskDescription)) {
              _tmpTaskDescription = null;
            } else {
              _tmpTaskDescription = _cursor.getString(_cursorIndexOfTaskDescription);
            }
            final float _tmpTaskCompletion;
            _tmpTaskCompletion = _cursor.getFloat(_cursorIndexOfTaskCompletion);
            final LatLng _tmpLocation;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfLocation);
            _tmpLocation = __converters.toLatLng(_tmp);
            final int _tmpRadius;
            _tmpRadius = _cursor.getInt(_cursorIndexOfRadius);
            final String _tmpStoryline;
            if (_cursor.isNull(_cursorIndexOfStoryline)) {
              _tmpStoryline = null;
            } else {
              _tmpStoryline = _cursor.getString(_cursorIndexOfStoryline);
            }
            _item = new Task(_tmpTaskId,_tmpTaskName,_tmpTaskDescription,_tmpTaskCompletion,_tmpLocation,_tmpRadius,_tmpStoryline);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getStepsFromTask(final int id, final Continuation<? super List<Step>> $completion) {
    final String _sql = "SELECT * From Task, Step, TaskStepRelation\n"
            + "                WHERE Task.taskId = ?\n"
            + "                  AND TaskStepRelation.taskId = Task.taskId\n"
            + "                  AND Step.stepId = TaskStepRelation.stepId";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Step>>() {
      @Override
      @NonNull
      public List<Step> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfStepId = CursorUtil.getColumnIndexOrThrow(_cursor, "stepId");
          final int _cursorIndexOfStepName = CursorUtil.getColumnIndexOrThrow(_cursor, "stepName");
          final int _cursorIndexOfStepDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "stepDescription");
          final int _cursorIndexOfStepCompletion = CursorUtil.getColumnIndexOrThrow(_cursor, "stepCompletion");
          final int _cursorIndexOfActivityId = CursorUtil.getColumnIndexOrThrow(_cursor, "activityId");
          final List<Step> _result = new ArrayList<Step>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Step _item;
            final int _tmpStepId;
            _tmpStepId = _cursor.getInt(_cursorIndexOfStepId);
            final String _tmpStepName;
            _tmpStepName = _cursor.getString(_cursorIndexOfStepName);
            final String _tmpStepDescription;
            if (_cursor.isNull(_cursorIndexOfStepDescription)) {
              _tmpStepDescription = null;
            } else {
              _tmpStepDescription = _cursor.getString(_cursorIndexOfStepDescription);
            }
            final boolean _tmpStepCompletion;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfStepCompletion);
            _tmpStepCompletion = _tmp != 0;
            final String _tmpActivityId;
            if (_cursor.isNull(_cursorIndexOfActivityId)) {
              _tmpActivityId = null;
            } else {
              _tmpActivityId = _cursor.getString(_cursorIndexOfActivityId);
            }
            _item = new Step(_tmpStepId,_tmpStepName,_tmpStepDescription,_tmpStepCompletion,_tmpActivityId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
