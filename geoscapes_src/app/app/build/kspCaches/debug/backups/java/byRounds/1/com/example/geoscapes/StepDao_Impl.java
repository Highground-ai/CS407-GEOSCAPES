package com.example.geoscapes;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class StepDao_Impl implements StepDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TaskStepRelation> __insertionAdapterOfTaskStepRelation;

  private final EntityUpsertionAdapter<Step> __upsertionAdapterOfStep;

  public StepDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTaskStepRelation = new EntityInsertionAdapter<TaskStepRelation>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `TaskStepRelation` (`taskId`,`stepId`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TaskStepRelation entity) {
        statement.bindLong(1, entity.getTaskId());
        statement.bindLong(2, entity.getStepId());
      }
    };
    this.__upsertionAdapterOfStep = new EntityUpsertionAdapter<Step>(new EntityInsertionAdapter<Step>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `Step` (`stepId`,`stepName`,`stepDescription`,`stepCompletion`,`activityId`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Step entity) {
        statement.bindLong(1, entity.getStepId());
        statement.bindString(2, entity.getStepName());
        if (entity.getStepDescription() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getStepDescription());
        }
        final int _tmp = entity.getStepCompletion() ? 1 : 0;
        statement.bindLong(4, _tmp);
        if (entity.getActivityId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getActivityId());
        }
      }
    }, new EntityDeletionOrUpdateAdapter<Step>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `Step` SET `stepId` = ?,`stepName` = ?,`stepDescription` = ?,`stepCompletion` = ?,`activityId` = ? WHERE `stepId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Step entity) {
        statement.bindLong(1, entity.getStepId());
        statement.bindString(2, entity.getStepName());
        if (entity.getStepDescription() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getStepDescription());
        }
        final int _tmp = entity.getStepCompletion() ? 1 : 0;
        statement.bindLong(4, _tmp);
        if (entity.getActivityId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getActivityId());
        }
        statement.bindLong(6, entity.getStepId());
      }
    });
  }

  @Override
  public Object insertRelation(final TaskStepRelation stepAndTask,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTaskStepRelation.insert(stepAndTask);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertStep(final Step step, final int taskId,
      final Continuation<? super Integer> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> StepDao.DefaultImpls.upsertStep(StepDao_Impl.this, step, taskId, __cont), $completion);
  }

  @Override
  public Object upsert(final Step step, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __upsertionAdapterOfStep.upsertAndReturnId(step);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final int id, final Continuation<? super Step> $completion) {
    final String _sql = "SELECT * FROM step WHERE stepId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Step>() {
      @Override
      @NonNull
      public Step call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfStepId = CursorUtil.getColumnIndexOrThrow(_cursor, "stepId");
          final int _cursorIndexOfStepName = CursorUtil.getColumnIndexOrThrow(_cursor, "stepName");
          final int _cursorIndexOfStepDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "stepDescription");
          final int _cursorIndexOfStepCompletion = CursorUtil.getColumnIndexOrThrow(_cursor, "stepCompletion");
          final int _cursorIndexOfActivityId = CursorUtil.getColumnIndexOrThrow(_cursor, "activityId");
          final Step _result;
          if (_cursor.moveToFirst()) {
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
            _result = new Step(_tmpStepId,_tmpStepName,_tmpStepDescription,_tmpStepCompletion,_tmpActivityId);
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
  public Object getByRowId(final long rowId, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT stepId FROM step WHERE rowid = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, rowId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _result;
          if (_cursor.moveToFirst()) {
            _result = _cursor.getInt(0);
          } else {
            _result = 0;
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
  public Object taskStepCount(final int taskId, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM Task, Step, TaskStepRelation\n"
            + "            WHERE Task.taskId = ?\n"
            + "                AND TaskStepRelation.taskId = Task.taskId\n"
            + "                AND Step.stepId = TaskStepRelation.stepId";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, taskId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
