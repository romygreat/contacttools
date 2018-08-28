package com.longcheercontact.com.contact;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity {
    private  String TAG="TestMainActivity";
    private Button delteButton,addButton;
    private String[] permissionArray=new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.READ_SMS
    };
    private boolean isAdd=true;
    private ChinneseName chinneseName=new ChinneseName();
    private String name;
    private EditText editText;
    private ProgressBar progressBar;
    private ContentResolver resolver;
    private TextView DisText,topicText,description,Distext;
    private  int  ContactNum=0;
    private ImageView imageView;
    private    Cursor phoneCursor;
    private int param=0;
    private int mMode=1;
    private TextView logo,displayContentText;
    ContentValues values = new ContentValues();
    int tt=1;
    private String phoneNumber;
    private boolean mIsMessage=true;
    private boolean mIsContact=true;
    Object localObject1 = null;
    Object localObject2 =null;
    byte[] avatar;
    private HashMap<String,Object> hashMap=new HashMap<String,Object>();
    private static final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.Contacts.Photo.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID };
    private boolean first=true;
    private boolean mMesage1=true;
    private boolean mCaiXin=true;
    private boolean mIsDeleteDuoyu=true;
    private String defaultSmsApp="one";
    private int permission;
    private int numMessage;
    private Integer progressBarInt=0;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
     //   String json="{\"user:{\"name\":\"romy\",\"age\":\"25\"}}";
        String json="{user:{name:romy,age:25},test:testContent}";
        try {
            JSONObject jsonObject=new JSONObject(json);
           JSONObject jname=jsonObject.getJSONObject("user");
            Log.i(TAG, "onCreate1: "+jname.getString("name"));
            Log.i(TAG, "onCreate1: "+jsonObject.get("test"));
            
        } catch (JSONException e) {
            Log.i(TAG, "onCreate1: jsonerro");
            e.printStackTrace();
        }
        
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.layout_porttrait);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
        }
        initView();
        dialog=new ProgressDialog(this);
        Log.i(TAG, "onCreate:progressBarlog "+progressBar.getProgress());
        permission = ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS);
        verifyStoragePermissions(MainActivity.this,permissionArray);//放在点击按钮adb
        defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(MainActivity.this);//获取手机当前设置的默认短信应用的包名
        editText.setText("5");
        // progressBar.setMax(5); //修改时间为7月3号 14:57
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState: ");
    }
    @Override
    protected void onResume(){
        Log.i(TAG, "onResume: ");
       progressBar.setProgress(progressBarInt);
        super.onResume();
        //DisContatcNum();
    }
    private void DisContatcNum() {
        DisText.post(new Runnable() {
            @Override
            public void run() {
                ContactNum=getPhoneContactNum();
                DisText.setText( "当前手机联系人个数为:"+ContactNum);
            }
        });

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_LANDSCAPE) {
            progressBar.setProgress(0);
        } else if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT) {
            progressBar.setProgress(0);

        }
    }
    private void initView() {
        editText=findViewById(R.id.editText);
        DisText=findViewById(R.id.Distext);
        progressBar=findViewById(R.id.progressBar);
        imageView=findViewById(R.id.image);
        delteButton=findViewById(R.id.deleteButton);
        addButton=findViewById(R.id.addButton);
        logo=findViewById(R.id.textView5);
        displayContentText=findViewById(R.id.display);
        topicText=findViewById(R.id.topic);
        description=findViewById(R.id.description);
        DisText=findViewById(R.id.Distext);
    }
    public void addContactButton(View view){

        param= getEditText();
        progressBar.setMax(param);
        switch (mMode){
            case 1:  {
                MutilContactTask addmutiContact=new MutilContactTask();
                isAdd=true;
                addButton.setClickable(false);
                if(editText.getText().toString().equals("")==false)
                {  delteButton.setClickable(false);
                    addmutiContact.execute(param);
                    if(param>100){
                        dayin("正在进行中，请耐心等待");
                    }
                    else dayin("正在处理中");
                }
                else {
                    addButton.setClickable(true);
                    delteButton.setClickable(true);
                }

            }
            break;
            case 2: {
                changeDefaultAPP();
                {   mIsContact = false;
                    if (Telephony.Sms.getDefaultSmsPackage(MainActivity.this).equals("com.longcheercontact.luominming")){
                        MessageTask messageTask=new MessageTask();
                        if(editText.getText().toString().equals("")==false)
                        {   dayin("请稍等");
                            messageTask.execute(1);}
                        else dayin("编辑框不能为空");
                    }

                }
            }break;
            case 3:{
                changeDefaultAPP();
                if (Telephony.Sms.getDefaultSmsPackage(MainActivity.this).equals("com.longcheercontact.luominming")){
                MessageTask messageTask=new MessageTask();
                if(editText.getText().toString().equals("")==false)
                {  dayin("请稍等");
                    messageTask.execute(0);
                }
                else dayin("编辑框不能为空");
                }
            }break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void onDeleteButton(View view){
        switch (mMode){
            case 1:
            {  // progressBar.setMax(param);
                isAdd=false;
                param=getEditText();
                MutilContactTask mutilContactTask=new MutilContactTask();
                if(editText.getText().toString().equals("")==false)
                {   dealwithdialog();
                    mutilContactTask.execute(1);}
               // else dayin("编辑框不能为空");
            }
            break;
            case 3:{
                changeDefaultAPP();
                if (Telephony.Sms.getDefaultSmsPackage(MainActivity.this).equals("com.longcheercontact.luominming")){
                    if(editText.getText().toString().equals("")==false){
                    dealwithdialog();
                    MessageTask messageTask=new MessageTask();
                    messageTask.execute(2);} else dayin("编辑框不能为空");}
            }break;
            case 2:
                changeDefaultAPP();
                if (Telephony.Sms.getDefaultSmsPackage(MainActivity.this).equals("com.longcheercontact.luominming")){
                    {    if(editText.getText().toString().equals("")==false){
                        dealwithdialog();
                        MessageTask messageTask=new MessageTask();
                        messageTask.execute(2);}   else dayin("编辑框不能为空");

                    }
                }
                break;
        }

//
    }
    public void dayin(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }
    private void addContact()throws NullPointerException {
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        values.clear();
        name=chinneseName.getName();
        //添加姓名
        long contactId = ContentUris.parseId(resolver.insert(uri, values));
        uri = Uri.parse("content://com.android.contacts/data");
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/name");
        values.put("data2", name);
        resolver.insert(uri, values);
        // 添加电话
        values.clear();
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/phone_v2");
        values.put("data2", "2");
        String phone=chinneseName.getPhone();
        values.put("data1",phone );
        resolver.insert(uri, values);
        // 添加email
        values.clear();
        values.put("raw_contact_id", contactId);
//        values.put("mimetype", "vnd.android.cursor.item/email_v2");
        values.put("mimetype", "vnd.android.cursor.item/organization");
        values.put("data2", "2");
        values.put("data1", "龙旗科技股份有限公司");
//        values.put("data1", chinneseName.getEmail());
        resolver.insert(uri, values);
        values.clear();
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/email_v2");
        values.put("data2", "2");
        values.put("data1", chinneseName.getEmail());
        resolver.insert(uri, values);

        values.clear();
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/nickname");
        values.put("data2", "2");
        values.put("data1",chinneseName.getName());
        resolver.insert(uri, values);
        values.clear();

//        values.put("raw_contact_id", contactId);
//        values.put(ContactsContract.CommonDataKinds.Photo.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
//        values.put(ContactsContract.CommonDataKinds.Photo.PHOTO,avatar);
//        resolver.insert(uri, values);
//        values.clear();

        System.gc();
    }

    private byte[] getBytes() {
        Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.playphone);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
// 将Bitmap压缩成PNG编码，质量为100%存储
        sourceBitmap.compress(Bitmap.CompressFormat.JPEG, 30, os);
        return os.toByteArray();
    }

    public int getEditText() {
        String text=editText.getText().toString();
        if(text.equals("")){dayin("输入不能为空");return 0;}
        editText.setSelection(text.length());
        param=Integer.valueOf(text);
        return param;
    }
    public class MessageTask extends AsyncTask <Integer, Integer, String>{
        @Override
        protected String doInBackground(Integer... integers) {

            if(editText.getText().toString().equals("")==false)
                numMessage  = Integer.parseInt(editText.getText().toString());
            else  return "kong";
            if (integers[0].equals(0)){
                for (int i = 1; i <=numMessage; i++) {
                    if(mCaiXin){
                        caiXInTest(0);
                    }else {
                        caiXInTest(i);
                    }
                    publishProgress(i);
                }
                return "MMS";
            }
            if (integers[0].equals(1)){
                for (int i = 1; i <=numMessage; i++) {
                    if(mMesage1){
                        duanXin(0);}else {
                        duanXin(i);
                    }
                    publishProgress(i);
                }
            }
            if (integers[0].equals(2)){
                DisText.setText("正在处理中");
                deleteSMS(numMessage);return "deleteSMS";
            }
            return "message";
        }
        @Override
        protected void onPostExecute(String s) {
            if (s.equals("kong")){
                dayin("编辑框不能为空");
                return;
            }
            if (Telephony.Sms.getDefaultSmsPackage(MainActivity.this).equals("com.longcheercontact.com.contact"))
                dayin("温馨提示您，完成啦");
            setDisText(mMode);
            imageView.setVisibility(View.VISIBLE);
            logo.setVisibility(View.INVISIBLE);
            if (s.equals("MMS")){
                first=true;
            }
            deleteCaiXinMessage();
            Log.i(TAG, "onPostExecute: first"+first);
           dialog.dismiss();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBarInt=values[0];
            progressBar.setProgress(progressBarInt);
            isAdd=true;
            DisText.setText("\n正在处理中");
            Log.i(TAG, "onProgressUpdate: "+values[0]);
        }

    }
    public class MutilContactTask extends AsyncTask <Integer, Integer, String>{
        private int number;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Integer... params) {
            Log.i(TAG, "doInBackground: test");
            resolver  = getContentResolver();
            avatar = getBytes();
            number=params[0].intValue();
// 获取手机联系人
            try{
                phoneCursor= resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,PHONES_PROJECTION,
                        null, null, null);}catch (Exception e){
                return "error";
            }
            if(isAdd){
                ContactNum=0;
                //resolver= getContentResolver();
                for(int i=0;i<number;i++)

                {
                    addContact();
                    publishProgress(i+1);
                    System.gc();
                }
            }
            else {
                try {
                    Log.i(TAG, "doInBackground: begin");
                  //  progressBar.setMax(number);
                    DisText.setText("\n删除处理中，无进度条提示");
                    deleteContact();
                    Log.i(TAG, "doInBackground: begin-end");
                    DisContatcNum();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "doInBackground: error");
                }
                Log.i(TAG+"else", "doInBackground: delete");
            }
            return "ok";
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            isAdd=true;
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            DisText.setText("\n正在处理中");
            Log.i(TAG, "onProgressUpdate: ");
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("error")){
                addButton.setClickable(true);
                dayin("请打开通讯录权限，否则不能正常运行");return;}
            imageView.setVisibility(View.VISIBLE);
            logo.setVisibility(View.INVISIBLE);
            DisText.setSelected(false);
            DisContatcNum();
            dayin("通讯录已经处理完成");
            DisContatcNum();
            addButton.setClickable(true);
            delteButton.setClickable(true);
            resolver=null;
            phoneCursor=null;
            setDisText(mMode);
           dialog.dismiss();
            System.gc();
            Log.i(TAG, "onPostExecute: after gc");
        }

    }
    private int getPhoneContactNum() {
        ContactNum=0;
        resolver = getContentResolver();
        String[] projecttion=new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        try {
            phoneCursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
            if (phoneCursor != null) {
                while (phoneCursor.moveToNext()) {
                    ContactNum++;
                    //修改为1几为姓名
                    Log.i(TAG, "Phonenumber:4 "+phoneNumber);
                }
            }}catch (Exception e){
            addButton.setClickable(true);
            dayin("请设置权限，否则无法正常操作");
            e.printStackTrace();
        }
        return  ContactNum;
    }
    private void deleteContact() {
        isAdd=false;
        int a=0;
        try {
//            String[] projecttion=new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            resolver = getContentResolver();
            phoneCursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            String where = ContactsContract.Data._ID  + " =?";
            while(phoneCursor.moveToNext()){
                a++;
                String rawId = phoneCursor.getString(phoneCursor.getColumnIndex("_id"));
                String[] whereparams = new String[]{rawId};
                getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, where, whereparams);
                if(a>=param) {
                    resolver =null;phoneCursor=null;System.gc();
                    break;
                }
            }
        }
        catch (Exception e){
            Log.i(TAG, "deleteContact: so bad");
            deleteContact();

        }
        Log.i(TAG, "deleteContact:return");
    }
    public void inContact(View view){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.PICK");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("vnd.android.cursor.dir/phone_v2");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    public void verifyStoragePermissions(Activity activity, String[] permissionArray ){
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissionArray, 100);
        }
        else if (permission == PackageManager.PERMISSION_GRANTED)
        { permission = ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS);

            DisContatcNum();  return;
        } else {
            Log.i(TAG, "verifyStoragePermissions: ");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==100){
            DisContatcNum();
        }
        Log.i(TAG, "onRequestPermissionsResult: "+permissions[0]);
    }
    private void caiXInTest(int caiXinMessage) {
        {
            String phone= chinneseName.getPhone();
            Object localObject1 =3;
            int i = 0;
            {
                Object localObject2 = new ContentValues();
                if(caiXinMessage==0){

                    ((ContentValues)localObject2).put("address", "10086" );}
                else {
                    ((ContentValues)localObject2).put("address",phone );
                }
                ((ContentValues)localObject2).put("read", Integer.valueOf(1));
                ((ContentValues)localObject2).put("status", Integer.valueOf(-1));
                ((ContentValues)localObject2).put("type", Integer.valueOf(1));
                ((ContentValues)localObject2).put("body", "彩信测试zxcvb" );
                localObject1 = null;
                {
                    {
                        if(mIsMessage) first = false;
                        localObject2 = MainActivity.this.getContentResolver().insert(Uri.parse("content://sms/inbox"), (ContentValues) localObject2);
                        hashMap.put("1", localObject2);
                    }
                    Uri uridel=(Uri)hashMap.get("1");
                    Log.i(MainActivity.this.TAG, "insert result Uri content://sms/inbox:" + localObject2);
                    localObject1 = hashMap.get("1");
                    long l = ContentUris.parseId((Uri) localObject1);//空指针
                    Log.i(MainActivity.this.TAG, "sms  ContentUris parseId smsid:" + String.valueOf(l));
                    localObject1 = MainActivity.this.getContentResolver();
                    localObject2 = Uri.parse("content://sms/inbox");
                    String str = Long.toString(l);
                    localObject1 = ((ContentResolver)localObject1).query((Uri)localObject2, new String[] { "_id", "thread_id" }, "_id=?", new String[] { str }, "date desc");
                    ((Cursor)localObject1).moveToLast();
                    try{
                        localObject1 = Long.valueOf(((Cursor)localObject1).getLong(((Cursor)localObject1).getColumnIndex("thread_id")));
                        Log.i(MainActivity.this.TAG, "sms threadid:" + String.valueOf(localObject1));
                        Log.i(MainActivity.this.TAG, "insert PDU");
                        localObject2 = new ContentValues();
                        ((ContentValues)localObject2).put("thread_id", (Long)localObject1);
                        ((ContentValues)localObject2).put("msg_box", Integer.valueOf(1));
                        ((ContentValues)localObject2).put("read", Integer.valueOf(1));
                        ((ContentValues)localObject2).put("ct_t", "application/vnd.wap.multipart.related");
                        ((ContentValues)localObject2).put("m_type", Integer.valueOf(132));
                        ((ContentValues)localObject2).put("v", Integer.valueOf(18));
                        ((ContentValues)localObject2).put("text_only", Integer.valueOf(1));
                        ((ContentValues)localObject2).put("tr_id", "T14d5046b9e" + String.valueOf(i));
                    }
                    catch (Exception e){
                        e.toString();
                        e.printStackTrace(); //开启发生报错，异常出现

                    }
                }

                {
                    try
                    {
                        Log.i(TAG, "run: MMS /inbox Uri parse");
                        localObject1 = MainActivity.this.getContentResolver().insert(Uri.parse("content://mms/inbox"), (ContentValues)localObject2);
                        Log.i(MainActivity.this.TAG, "insert MMS PDU content://mms/inbox Uri:" + localObject1);
                        long l = ContentUris.parseId((Uri)localObject1);
                        Log.i(MainActivity.this.TAG, "insert MMS address table ");
                        localObject1 = new ContentValues();
                        ((ContentValues)localObject1).put("msg_id", Long.valueOf(l));
                        ((ContentValues)localObject1).put("address", "龙旗测试部骆敏明");
                        ((ContentValues)localObject1).put("type", "137");
                        ((ContentValues)localObject1).put("charset", "106");
                        MainActivity.this.getContentResolver().insert(Uri.parse("content://mms/" + l + "/addr"), (ContentValues)localObject1);
                        ((ContentValues)localObject1).put("type", "151");
                        ((ContentValues)localObject1).put("address", phone);
                        MainActivity.this.getContentResolver().insert(Uri.parse("content://mms/" + l + "/addr"), (ContentValues)localObject1);
                        Log.i(MainActivity.this.TAG, "insert mms part table :" + String.valueOf(l));
                        localObject1 = MainActivity.this.createPartRecord(-1, "application/smil", "smil.xml",
                                "<siml>", "smil.xml",
                                null, "<smil><head><layout>" +
                                        "<root-layout width=\"320px\" height=\"480px\"/>" +
                                        "<region id=\"Text\" left=\"0\" top=\"320\" " +
                                        "width=\"500px\" height=\"160px\" fit=\"meet\"/><region id=\"Image\" left=\"0\" top=\"0\" " +
                                        "width=\"320px\" height=\"320px\" " +
                                        "fit=\"meet\"/>" +
                                        "</layout>" +
                                        "</head><body><par dur=\"5000ms\"><text src=\"text_0.txt\" region=\"Text\"/>" +
                                        "<img src=\"playphone.jpeg\" region=\"Image\"/>" +
                                        "</par></body></smil>");
                        localObject2 = MainActivity.this.createPartRecord(0, "image/jpg", "playphone.jpg", "<test>",
                                "playphone.jpeg", null, null);

                        // localObject2 = MainActivity.this.createPartRecord(0, "image/jpeg", "image50k_test.jpeg", "<50k_test>", "image50k_test.jpeg", null, null);
                        ((ContentValues)localObject1).put("mid", Long.valueOf(l));
                        ((ContentValues)localObject2).put("mid", Long.valueOf(l));
                        Log.i(TAG, "run: this is midput2");
                        // MainActivity.this.getContentResolver().insert(Uri.parse("content://mms/" + l + "/part"), (ContentValues)localObject1);
                        localObject1 = MainActivity.this.getContentResolver().insert(Uri.parse("content://mms/" + l + "/part"), (ContentValues)localObject2);
                        MainActivity.this.Copydata1((Uri)localObject1);
                        i += 1;
                    }
                    catch (Exception localException1)
                    {Log.i(TAG, "run: Exception localException1"+localException1.toString());}
                }
            }
        }
    };
    //    }
    private ContentValues createPartRecord(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
    {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("seq", Integer.valueOf(paramInt));
        localContentValues.put("ct", paramString1);
        localContentValues.put("name", paramString2);
        localContentValues.put("cid", paramString3);
        localContentValues.put("cl", paramString4);
        if (paramString5 != null) {
            localContentValues.put("_data", paramString5);
        }
        if (paramString6 != null) {
            localContentValues.put("text", paramString6);
        }
        return localContentValues;
    }
    private void Copydata1(Uri paramUri) {
        Log.i(TAG, "Copydata: first");
        InputStream localInputStream = null;
        int i,len;
        byte[] byte1=new byte[1024];
        byte[] byte2=new byte[1024];
        OutputStream localOutputStream= null;
        try {
            localOutputStream = getContentResolver().openOutputStream(paramUri);
        } catch (FileNotFoundException e) {
            Log.i(TAG, "Copydata: contentResolver().openOutStream");
            e.printStackTrace();
        }
        localInputStream = getResources().openRawResource(R.raw.playphone);//修改文件内容
        try {
            Log.i(TAG, "Copydata: localInputStream.available()"+localInputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Copydata: localInputsream");
        try {
            while ((len = localInputStream.read(byte2)) != -1){
                Log.i(TAG, "Copydata: read");
                localOutputStream.write(byte2);
            }
            Log.i(TAG, "Copydata: len写入字节");
        } catch (IOException e) {
            Log.i(TAG, "Copydata: IOEXception2");
            e.printStackTrace();
        }
    }
    public void duanXin(int number){
        ContentValues contentValues=new ContentValues();
        if (number==0){ contentValues.put("address","07523050888");}
        else {contentValues.put("address","07523050888"+number);}
        contentValues.put("read", Integer.valueOf(1));
        contentValues.put("status", Integer.valueOf(-1));
        contentValues.put("type", Integer.valueOf(1));
        contentValues.put("body", "龙旗测试部开发，使用自动短信生成工具"+
                "短信的测试结果第"+tt+"次");
        tt++;
        MainActivity.this.getContentResolver().insert(Uri.parse("content://sms/inbox"), contentValues);
        //  Intent intent1 = new Intent( Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        Log.i(TAG, "clickContactNum: "+Telephony.Sms.getDefaultSmsPackage(getApplicationContext()));
    }
    void changeDefaultAPP(){
        Log.i(TAG, "changeDefaultAPP: ");
        tt=1;
        if (!Telephony.Sms.getDefaultSmsPackage(MainActivity.this).equals("com.longcheercontact.luominming")) {
            if (Build.VERSION.SDK_INT >= 19) {
                Log.i(TAG, "run: SDK_INT>=19");
                localObject1 = new Intent("android.provider.Telephony.ACTION_CHANGE_DEFAULT");
                ((Intent) localObject1).putExtra("package", MainActivity.this.getApplicationContext().getPackageName());
                MainActivity.this.startActivity((Intent) localObject1);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        imageView.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.VISIBLE);
        if (id == R.id.action_settings) {
            mMode=1;
            mIsMessage=true;
            first=true;
            topicText.setText("手机通讯录工具");
            DisContatcNum();
            displayContentText.setText("输入想要添加（删除）联系人数量");
            description.setText("说明：通讯录联系人随机生成，邮件属性对应拼音名称，单位属性统一为龙旗科技股份有限公司");
            return true;
        }
        if (id == R.id.messages) {
            changeDefaultAPP();
            topicText.setText("手机短信自动生成工具");
            mMode=2;
            description.setText("说明：生成需求数量短信，对应一个手机号");
            DisText.setText("当前有"+getSMSMessage()+"条短信");
            mMesage1=true;
            mIsMessage=true;
            displayContentText.setText("输入你想要添加（删除）短信数量");
            displayContentText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //  dayin("messages");
            return true;
        }
        if (id == R.id.messages1) {//短信2

            mMode=2;
            topicText.setText("手机短信自动生成工具");
            mIsMessage=true;
            DisText.setText("当前有"+getSMSMessage()+"条短信");
            topicText.setText("手机短信自动生成工具");
            displayContentText.setText("输入你想要添加（删除）短信数量");
            description.setText("说明：生成需求数量短信，对应不同号码");
            changeDefaultAPP();
            mMesage1=false;
            return true;
        }
        if (id == R.id.caiXin) {
            mMode=3;
            topicText.setText("手机彩信自动生成工具");
            description.setText("说明：生成需求数量彩信，对应一个手机号");
            mCaiXin=true;
            DisText.setText("当前有"+getSMSMessage()+"条彩信");
            mIsMessage=true;//修改为true
            first=true;
            displayContentText.setText("输入你想要添加（删除）彩信数量");
            changeDefaultAPP();
            return true;
        }
        if (id == R.id.caiXin1) {
            mMode=3;
            mIsMessage=false;
            mCaiXin=false;
            first=true;
            displayContentText.setText("输入你想要添加（删除）彩信数量");
            setDisText(mMode);
            topicText.setText("手机彩信自动生成工具");
            changeDefaultAPP();
            description.setText("说明：生成需求数量彩信，对应不同号码");
            return true;
        }
        if (id==R.id.more){
            dayin("该功能暂未开通，敬请留意下一个版本，谢谢！");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public int getSMSMessage(){
        ContentResolver cr=getContentResolver();
        Cursor cur=null;
        int messagenum=0;
        try{
            if(mMode==2){
                cur =cr.query(Uri.parse("content://sms/"),null,null,null,"date desc");}

            else{
                cur =cr.query(Uri.parse("content://mms/"),null,null,null,"date desc");
            }
            while (cur.moveToNext() ){
                messagenum++;
            }
            Log.i(TAG, "getSMSMessage: "+messagenum);}
        catch (Exception e){
            verifyStoragePermissions(MainActivity.this,permissionArray);//放在点击按钮adb
            dayin("请正确打开短信权限");
        }
        return messagenum;
    }
    public void deleteSMS(int deleteNum)
    {Uri uri;
        int deleteOK=0;
        try
        {
            // 准备系统短信收信箱的uri地址
            if(mMode==2){
                uri= Uri.parse("content://sms/");}//content://sms/inbox
            else {
                uri = Uri.parse("content://mms/");}

            Cursor curs =    getContentResolver().query(uri, null, null,null, "date desc");
            if(curs.moveToFirst())
            {
                do{
                    {
                        int id = curs.getInt(curs.getColumnIndex("_id"));
                        if (mMode==2)
                            getContentResolver().delete(Uri.parse("content://sms/"),"_id=?",new String[]{ String.valueOf(id)});
                        else {
                            getContentResolver().delete(Uri.parse("content://mms/"),"_id=?",new String[]{ String.valueOf(id)});
                        }
                        deleteOK++;
                    }
                }while (curs.moveToNext() && deleteNum>deleteOK );
            }
            curs.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        switch (mMode){
            case 2: DisText.setText("当前有"+getSMSMessage()+"条短信");break;
            case 3:DisText.setText("当前有"+getSMSMessage()+"条彩信");break;
            default:break;
        }
    }
    public void deleteCaiXinMessage(){
        {
            try
            {
                // 准备系统短信收信箱的uri地址
                Uri uri = Uri.parse("content://sms/inbox");// 收信箱
                Cursor curs =    getContentResolver().query(uri, null, null,null, "date desc");
                if(curs.moveToFirst())
                {
                    String body =curs.getString(curs.getColumnIndex("body")).trim();// 获取信息内容
                    if (body.contains("彩信测试zxcvb"))
                    {
                        MainActivity.this.getContentResolver().delete(Uri.parse("content://sms/"),"body=?",new String[]{"彩信测试zxcvb"});
                    }
                }
                curs.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void insertCallLog(String number, String duration, String type, String isNew) {
        //在通讯录查询是否存在该联系人，若存在则把名字信息也插入到通话记录中
        String name = "";
        String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME};
        //设置查询条件
        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "='"+number+"'";
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                cols, selection, null, null);
        int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            name = cursor.getString(nameFieldColumnIndex);
        }
        cursor.close();
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.CACHED_NAME, name);
        values.put(CallLog.Calls.NUMBER, number);
        values.put(CallLog.Calls.DATE, System.currentTimeMillis() );
        values.put(CallLog.Calls.DURATION, duration);
        values.put(CallLog.Calls.TYPE, type);
        values.put(CallLog.Calls.NEW, isNew);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALL_LOG}, 1000);
        }
        getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
    }
    public void photo(){
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                null);
        if (cursor.moveToFirst()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xiao);
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            byte[] avatar =os.toByteArray();
            ContentValues values = new ContentValues();
            values.put(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, contactId);
            values.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, avatar);
            getContentResolver().update(ContactsContract.Data.CONTENT_URI, values, null, null);
        }
    }
    public String setDisText(int mode){
        String notificationContent="notificaion";
        switch (mMode){
            case 1:{DisContatcNum();
                notificationContent="当前有"+ContactNum+"个联系人";
                break;}
            case 2:
            {  notificationContent="当前有"+getSMSMessage()+"条短信";
                DisText.setText(notificationContent);break;}
            case 3:{notificationContent="当前有"+getSMSMessage()+"条彩信";
                DisText.setText(notificationContent);break;}
            default:break;
        }
        return notificationContent;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("progressbar",editText.getText().toString());
    }
   public void  dealwithdialog(){
        dialog.setCancelable(true);
        dialog.setMessage("  龙旗测试部----删除处理中");
        dialog.show();
    }


}








