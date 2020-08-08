package com.example.dmjhfourplay.stampinseoul;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kakao.usermgmt.response.MeV2Response;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "StampInSeoulDB";
    private static final int VERSION = 1;

    private static DBHelper dbHelper;

    //싱글톤 디자인
    private DBHelper(Context context) {
        super(context, DB_NAME,null,VERSION);
    }
    public static DBHelper getInstance(Context context) {
        if(dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }

    //시스템에서 open()으로 열리는 DB가 없을때 호출된다 //getWritableDatabase 혹은 getReadableDatabase 호출시에 onCreate를 호출
    @Override
    public void onCreate(SQLiteDatabase db) {

        //사용자의 로그인 정보를 저장하는 테이블을 생성
        String query = "CREATE TABLE userTBL(userId TEXT PRIMARY KEY, userName TEXT, profileImage TEXT);";
        db.execSQL(query);

    }

    //super() 메소드에서 마지막 인자 version 값이 상향 조정될때에 호출된다.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //사용자 정보 테이블이 있으면 삭제하고 다시 만들어준다.
        db.execSQL("DROP TABLE IF EXISTS userTBL");
        onCreate(db);

    }

    //=========================== 로그인 정보 관련 DB 처리 함수 =========================================

    //로그인 이후 유저테이블에 로그인한 유저의 정보를 생성해주는 함수
    public void userAdd(MeV2Response result) {

        SQLiteDatabase db = getWritableDatabase();

        String query = "INSERT OR REPLACE INTO userTBL values('"
        + result.getId() + "','"
        + result.getNickname() + "','"
        + result.getProfileImagePath() +"');";

        db.execSQL(query);

    }

    //새로 로그인한 유저일 경우 그 유저 전용 테이블 2개를 생성해줍니다.
    public void newUserAddTBL(MeV2Response result) {

        SQLiteDatabase db = getWritableDatabase();

        //해당 유저의 찜목록을 저장해주는 테이블 생성
        String zzimTableQuery = "CREATE TABLE IF NOT EXISTS ZZIM_" + result.getId() + "("
        +"title TEXT PRIMARY KEY, "
        +"addr TEXT, "
        +"mapX REAL, "
        +"mapY REAL, "
        +"firstImage TEXT);";

        db.execSQL(zzimTableQuery);

        //해당 유저의 스탬프목록을 저장해주는 테이블 생성
        String stampTableQuery = "CREATE TABLE IF NOT EXISTS STAMP_" + result.getId() + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT, "
                + "addr TEXT, "
                + "mapX REAL, "
                + "mapY REAL, "
                + "firstImage TEXT, "
                + "picture TEXT, "
                + "content_pola TEXT, "
                + "content_title TEXT, "
                + "contents TEXT, "
                + "complete INTEGER);";

        db.execSQL(stampTableQuery);

    }

    //DB를 읽고 쓸 수 있게 해주는 SQLiteDatabse를 반환합니다
    public SQLiteDatabase getWritable() {
        return dbHelper.getWritableDatabase();
    }

    public SQLiteDatabase getReadable() {
        return dbHelper.getReadableDatabase();
    }

}
