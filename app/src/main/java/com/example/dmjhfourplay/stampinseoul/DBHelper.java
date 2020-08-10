package com.example.dmjhfourplay.stampinseoul;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kakao.usermgmt.response.MeV2Response;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "StampInSeoulDB";
    private static final int VERSION = 1;

    private static DBHelper dbHelper;

    private SQLiteDatabase writeableDatabase = getWritableDatabase();
    private SQLiteDatabase readableDatabase = getReadableDatabase();

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
    //LoginActivity

    //로그인 이후 유저테이블에 로그인한 유저의 정보를 생성해주는 함수
    public void userAdd(MeV2Response result) {

        String query = "INSERT OR REPLACE INTO userTBL values('"
        + result.getId() + "','"
        + result.getNickname() + "','"
        + result.getProfileImagePath() +"');";

        writeableDatabase.execSQL(query);

    }

    //새로 로그인한 유저일 경우 그 유저 전용 테이블 2개를 생성해줍니다.
    public void newUserAddTBL(MeV2Response result) {

        //해당 유저의 찜목록을 저장해주는 테이블 생성
        String zzimTableQuery = "CREATE TABLE IF NOT EXISTS ZZIM_" + result.getId() + "("
        +"title TEXT PRIMARY KEY, "
        +"addr TEXT, "
        +"mapX REAL, "
        +"mapY REAL, "
        +"firstImage TEXT);";

        writeableDatabase.execSQL(zzimTableQuery);

        //해당 유저의 스탬프목록을 저장해주는 테이블 생성
       createStampList();

    }

    //==========================  userTBL 관련 함수 =====================================
    public Cursor getUserData() {
        Cursor cursor = readableDatabase.rawQuery("SELECT * FROM userTBL WHERE userID = "+ LoginSessionCallback.userId +";",null);
        return cursor;
    }

    //==========================  ZzimTBL 관련 함수 =====================================
    //ZzimTBL

    //선택한 곳을 찜리스트 DB에 추가
    public void insertZzimList(ThemeData themeData) {

        String query = "INSERT INTO ZZIM_" + LoginSessionCallback.userId + " VALUES('" + themeData.getTitle() + "', '"
                + themeData.getAddr() + "', '"
                + themeData.getMapX() + "', '"
                + themeData.getMapY() + "', '"
                + themeData.getFirstImage() + "');";
        writeableDatabase.execSQL(query);
    }

    //DB에 있는 찜목록을 가져와서 커서로 반환
    public Cursor getZzimList() {

        Cursor cursor = readableDatabase.rawQuery("SELECT * FROM ZZIM_"+ LoginSessionCallback.userId +";",null);

        return cursor;
    }

    //찜목록 DB에서 삭제
    public void deleteZzimList(String title) {

        String query = "DELETE FROM ZZIM_"+ LoginSessionCallback.userId +" WHERE title = '"+ title +"';";
        writeableDatabase.execSQL(query);
    }

    //==========================  StampTBL 관련 함수 =====================================
    //StampTBL

    //Stamp테이블이 존재하지 않으면 새로 생성해주는 함수
    public void createStampList() {

        String query = "CREATE TABLE IF NOT EXISTS STAMP_" + LoginSessionCallback.userId + "("
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

        writeableDatabase.execSQL(query);
    }

    //선택한 곳을 스탬프 리스트 DB에 추가
    public void insertStampList(ArrayList<ThemeData> arrayList) {

        for(ThemeData themeData : arrayList) {
            String query = "INSERT INTO STAMP_"+ LoginSessionCallback.userId +"(title, addr, mapX, mapY, firstImage) VALUES ('"+ themeData.getTitle()+"'," + "'"
                    +themeData.getAddr()+"','"
                    +themeData.getMapX()+"','"
                    +themeData.getMapY()+"','"
                    +themeData.getFirstImage()+"');";
            writeableDatabase.execSQL(query);
        }
    }

    //DB에 있는 Stamp테이블을 가져와서 반환
    public Cursor getStampList() {

        Cursor cursor = readableDatabase.rawQuery("SELECT * FROM STAMP_" + LoginSessionCallback.userId + ";",null);

        return cursor;
    }

    //사진 등록이 완료된 스탬프 리스트를 불러와줍니다.
    public Cursor getCompleteStampList() {

        Cursor cursor = readableDatabase.rawQuery("SELECT * FROM STAMP_" + LoginSessionCallback.userId +" WHERE complete = 1;",null);

        return cursor;
    }

    //스탬프 DB의 내용을 변경시켜 줍니다.
    public void updateStampList(String[] strings) {
        String query = "UPDATE STAMP_" + LoginSessionCallback.userId +" SET picture = '" + strings[0]
        + "', content_pola = '" + strings[1]
        + "', content_title = '" + strings[2]
        + "', contents = '" + strings[3]
        + "', complete = "+ 1
        + " WHERE title = '" + strings[4] + "';";
        writeableDatabase.execSQL(query);
    }

    //스탬프 테이블을 삭제해준다.
    public void dropStampList() {
        String query = "DROP TABLE IF EXISTS STAMP_" + LoginSessionCallback.userId +";";
        writeableDatabase.execSQL(query);
    }

}
