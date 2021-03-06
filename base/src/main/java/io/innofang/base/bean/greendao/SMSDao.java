package io.innofang.base.bean.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SMS".
*/
public class SMSDao extends AbstractDao<SMS, Long> {

    public static final String TABLENAME = "SMS";

    /**
     * Properties of entity SMS.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Time = new Property(1, String.class, "time", false, "TIME");
        public final static Property Address = new Property(2, String.class, "address", false, "ADDRESS");
        public final static Property Content = new Property(3, String.class, "content", false, "CONTENT");
        public final static Property Probability = new Property(4, double.class, "probability", false, "PROBABILITY");
    }


    public SMSDao(DaoConfig config) {
        super(config);
    }
    
    public SMSDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SMS\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TIME\" TEXT," + // 1: time
                "\"ADDRESS\" TEXT," + // 2: address
                "\"CONTENT\" TEXT," + // 3: content
                "\"PROBABILITY\" REAL NOT NULL );"); // 4: probability
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SMS\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SMS entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(2, time);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(3, address);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(4, content);
        }
        stmt.bindDouble(5, entity.getProbability());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SMS entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(2, time);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(3, address);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(4, content);
        }
        stmt.bindDouble(5, entity.getProbability());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public SMS readEntity(Cursor cursor, int offset) {
        SMS entity = new SMS( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // time
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // address
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // content
            cursor.getDouble(offset + 4) // probability
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SMS entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTime(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAddress(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setContent(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setProbability(cursor.getDouble(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(SMS entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(SMS entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(SMS entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
