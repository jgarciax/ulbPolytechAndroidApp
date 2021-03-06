package com.example.polytech2;

import java.io.FileOutputStream;
import java.io.IOException;    
import java.io.InputStream;
import java.io.OutputStream;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper{

//http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
	 
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.example.polytech2/databases/";
 
    private static String DB_NAME = "ulbDB";
 
    private SQLiteDatabase myDataBase; 
 
    private final Context myContext;
 
    private static final String TAG = null;
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {
 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//do nothing - database already exist
    	}else{
 
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
 
        	try {
 
    			copyDataBase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
    	
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    		Log.v(TAG, "this is the buffer: "+buffer);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDataBase() throws SQLException{
 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    }
   
    
	public String[] getCours(){
    	String[] List = null;
		Cursor c= myDataBase.rawQuery("SELECT NomDuCours FROM Cours", null);
		List=new String[c.getCount()];
		c.moveToNext();
		for(int i=0;i<c.getCount();i++){
			
			List[i]=c.getString(0);
			c.moveToNext();
		}
		return List;
    }
	public String getWebsiteFromCours(String cours){
		
		String  website= null;
		Cursor c= myDataBase.rawQuery("SELECT Website FROM Cours WHERE NomDuCours=?", new String[]{cours});
		website=new String();
		c.moveToNext();
		
		website=c.getString(0);
					
		return website;

	}
	
	public String[] getMnemomique(){
    	String[] List = null;
		Cursor c= myDataBase.rawQuery("SELECT Mnemomique FROM Cours", null);
		List=new String[c.getCount()];
		c.moveToNext();
		for(int i=0;i<c.getCount();i++){
			
			List[i]=c.getString(0);
			c.moveToNext();
		}
		return List;
    }
	public boolean[] getCheckBoxStatus(){
    	//int[] Listnumber = null;
    	boolean[] listbolean=null;
		Cursor c= myDataBase.rawQuery("SELECT checked FROM Cours", null);
		listbolean=new boolean[c.getCount()];
		c.moveToNext();
		for(int i=0;i<c.getCount();i++){
			if(c.getInt(0)==1){
				listbolean[i]=true;
			}else{
				listbolean[i]=false;
			}
			
			c.moveToNext();
		}
		return listbolean;
    }
	
	public void setElementsChecked(boolean[] listCheckboxes) throws SQLException{
		 
	    	//Open the database
	        String myPath = DB_PATH + DB_NAME;
	        //String[] number=new String[2];
	        //String updateQuery;
	        ContentValues values = new ContentValues();
	        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
	    	for(int i=0;i<listCheckboxes.length;i++){
	    		//number[0]=String.valueOf(listCheckboxes[i]? 1 : 0);
	    		//number[1]=String.valueOf(i+1);
	    		//updateQuery="UPDATE Cours SET checked="+number[0]+" WHERE _id="+number[1];
	    		//Cursor c= myDataBase.rawQuery(updateQuery, null);//PROBLEME AVEC CETTE REQUETTE
	    		
	    		
	    	    values.put("checked", listCheckboxes[i]? 1 : 0);
	    	    myDataBase.update("Cours", values, "_id=?", new String[] {String.valueOf(i+1)});
	    	}
	    	
	 
	}
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close();
 
    	    super.close();
 
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
 
        // Add your public helper methods to access and get content from the database.
       // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
       // to you to create adapters for your views.
 
}