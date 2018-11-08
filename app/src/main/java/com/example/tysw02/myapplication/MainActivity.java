package com.example.tysw02.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout swipeRefresh;
    RecyclerViewListAdapter adapter_change;
    boolean isScroll = false;
    protected Scheduler scheduler;
    LinkedList<String> listFileXML = new LinkedList<String>();
    private LinkedList<String> listFileTotalTemp = new LinkedList<String>();
    protected int totalFileCounts;
    protected static final int LIST_COUNTS_EACH_TIME = 16;
    protected RecyclerAdapterHandler recyclerAdapterHandler;
    protected LinkedList<String> listText = new LinkedList<String>();
    private Button button1,button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.camera_swipe);
        recyclerView = (RecyclerView)findViewById(R.id.camera_recycler_view);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);

        recyclerAdapterHandler = new RecyclerAdapterHandler(this);

        adapter_change = new RecyclerViewListAdapter(getApplicationContext(), listFileXML);
        final MyLinearLayoutManager layoutManager = new MyLinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(MyLinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter_change);
        recyclerView.setItemViewCacheSize(30);
        //20181105 Cliff
        recyclerView.setHasFixedSize(true);
        recyclerView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                //check if in refresh
                scheduler.shutdownNow();
                System.gc();
                //20181015 Cliff
                scheduler = new Scheduler();
                listFileTotalTemp = new LinkedList<>();
                listText.clear();
                listFileXML.clear();
                getList(0,0,0,getResources());
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if(fileType== FileSystem.FILE_TYPE.VIDEO_Lock ||fileType== FileSystem.FILE_TYPE.VIDEO||fileType== FileSystem.FILE_TYPE.VIDEO_Backup){
                    if(newState==recyclerView.SCROLL_STATE_IDLE){
                        Log.d("resume","scheduler resume");
                        scheduler.resume();
                        isScroll = false;
                    }else if(newState==recyclerView.SCROLL_STATE_DRAGGING ){
                        Log.d("pause","scheduler pause");
                        scheduler.pause();
                        isScroll = true;
                    }
//                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduler.shutdownNow();
                System.gc();
                    //20181015 Cliff
                scheduler = new Scheduler();
                listFileTotalTemp = new LinkedList<>();
                listText.clear();
                listFileXML.clear();
                getList(0,0,0,getResources());
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduler.shutdownNow();
                System.gc();
                //20181015 Cliff
                scheduler = new Scheduler();
                listFileTotalTemp = new LinkedList<>();
                listText.clear();
                listFileXML.clear();
                getList(0,0,1,getResources());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(scheduler==null){
            scheduler =new Scheduler();
            listFileTotalTemp = new LinkedList<>();
            listText.clear();
            listFileXML.clear();
            getList(0,0,0,getResources());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(scheduler!=null)
            scheduler.clear();
    }

    public class MyLinearLayoutManager extends LinearLayoutManager {
        public MyLinearLayoutManager(Context context) {
            super(context);
        }

        public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super( context, orientation, reverseLayout );
        }

        public MyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super( context, attrs, defStyleAttr, defStyleRes );
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();

            }
        }

        @Override
        public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                return super.scrollVerticallyBy(dy, recycler, state);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    private void getList(final int count,final int from,final int dirtype,final Resources resources){
//        Log.e("px", "get camera list start:" + from + " count:" + count + " dirtype=" +dirtype +" fileType" );
        GetCameraList getCameraList;
        getCameraList = new GetCameraList( count, from,dirtype,resources);
        getCameraList.setOnCameraListCallback(new OnCameraListCallback() {
            @Override
            public void onCameraListCallback(LinkedList<String> listFile) {
//                Log.e("info_xml",listFile.size()+"");
                if (listFile != null && listFile != null){
                    listFileTotalTemp.addAll(listFile);
                    if(listFile.size()==0||listFile.size()<LIST_COUNTS_EACH_TIME||listFileTotalTemp.size()>1000){
//                        Log.e("getlist","Done get allList time="+new Date(System.currentTimeMillis()));
                        totalFileCounts = listFileTotalTemp.size();
//                        Log.e("CameraList","totalFileCounts = "+totalFileCounts);
                            addDummy1(LIST_COUNTS_EACH_TIME);

                        //20181029 Cliff
                        listFileXML.clear();
                        listFileXML.addAll(listFileTotalTemp);
//                        listFileXML = listFileTotalTemp ;
                        Log.e("listFileXML","listFileXML size="+listFileXML.size());
//                        if((totalFileCounts - listFile.size())==0){
//                            getMetadataInDatabase(totalFileCounts,0);
//                        }else {
//                            getMetadataInDatabase(totalFileCounts % LIST_COUNTS_EACH_TIME, totalFileCounts - listFile.size());
//                        }
//                        if(fileType == FileSystem.FILE_TYPE.PHOTO){
//                        }else{//get video pic
//                            Log.e("enter","EnterPlaybackMode GlobalInfo.isPlackbackMode="+GlobalInfo.isPlackbackMode);
//                            if(!GlobalInfo.isPlackbackMode){
//                                new EnterPlaybackMode().executeOnExecutor(connectionExecutor);
//                            }
//                        }
                    }else{
                        totalFileCounts = listFileTotalTemp.size();

                            addDummy1(LIST_COUNTS_EACH_TIME);

                        //20181029 Cliff
                        listFileXML.clear();
                        listFileXML.addAll(listFileTotalTemp);
//                        listFileXML = listFileTotalTemp ;
                        //test 刷新會閃動
//                        if(totalFileCounts-LIST_COUNTS_EACH_TIME>=0) {
//                            getMetadataInDatabase(LIST_COUNTS_EACH_TIME, totalFileCounts - LIST_COUNTS_EACH_TIME);
//                        }else{
//                            getMetadataInDatabase(totalFileCounts, 0);
//                        }
//                        recyclerAdapterHandler.sendEmptyMessage(RecyclerAdapterHandler.WHAT_NOTIFY_WHOLE_CHANGE);
                        getList(count, listFileTotalTemp.size(), dirtype,resources);
                    }
                }
            }
            @Override
            public void onErrorCallback(int errorResponse) {
                //let see what to do
            }
        });
//        connectionExecutor.execute(getCameraList);
        scheduler.schedule(getCameraList);
    }

    protected static class GetCameraList implements Runnable{

        private static final int waitBeforeTimeout = 120000;

        private int count,from,type;
        private Resources resources;

        OnCameraListCallback callback;

        public GetCameraList(int count,int from,int type,Resources resources){
            this.count = count;
            this.from = from;
            this.type = type;
            this.resources=resources;
        }

        public void setOnCameraListCallback(OnCameraListCallback callback){
            this.callback = callback;
        }

        @Override
        public void run() {
            int responseCode =200;
                if(responseCode == HttpURLConnection.HTTP_OK)
                {
                    LinkedList<String> fileListXML = new LinkedList<String>();
                    switch (type){
                        case 0:
                          for(int i=0;i<16;i++){
                              fileListXML.add("i="+i);
                          }
                            break;
                        case 1:
                            for(int i=0;i<16;i++){
                                fileListXML.add("iiiii="+i);
                            }
                            break;
                    }
                    Log.i("fileListXML_size",fileListXML.size()+"");
                    if(fileListXML == null) fileListXML = new LinkedList<>();
                    if(callback != null){
                        callback.onCameraListCallback(fileListXML);
                    }
                }else{
                    if(callback != null){
                        callback.onErrorCallback(500);
                    }
                }

        }
    }

    interface OnCameraListCallback{
        void onErrorCallback(int errorResponse);
        void onCameraListCallback(LinkedList<String> listFile);
    }

    protected void addDummy1(int count){

        if(adapter_change.getItemCount() == totalFileCounts){
            return;
        }

        final int nextStartPosition = adapter_change.getItemCount();

        //Don't over produce dummy.
        if(adapter_change.getItemCount() + count >= totalFileCounts){
            count = totalFileCounts - adapter_change.getItemCount();
        }

        //no further image leave
        if(count == 0) return;

        //Set dummy content.
        if(nextStartPosition==0) {
            for(int i = 0; i < totalFileCounts; i++){
                //20181025 Cliff
//            int idx = i + nextStartPosition;
                int idx = i + 1;
                listText.add("" + idx);
            }
        }else{
            if( nextStartPosition > totalFileCounts){//refresh but get adapter old count
                for (int i = 0 ; i < totalFileCounts; i++) {
                    //20181025 Cliff
//            int idx = i + nextStartPosition;
                    int idx = i + 1;
                    listText.add("" + idx);
                }
            }else {
                for (int i = nextStartPosition; i < totalFileCounts; i++) {
                    //20181025 Cliff
//                    int idx = i + nextStartPosition;
                    int idx = i + 1;
                    listText.add("" + idx);
                }
            }
        }
        recyclerAdapterHandler.sendEmptyMessage(RecyclerAdapterHandler.WHAT_NOTIFY_WHOLE_CHANGE);

    }

    private abstract static class MyBaseHandler extends Handler {
        protected WeakReference<MainActivity> weakFrag;

        public MyBaseHandler(MainActivity fragment){
            weakFrag = new WeakReference<MainActivity>(fragment);
        }
    }

    protected static class RecyclerAdapterHandler extends MyBaseHandler{
        public static final String TAG_NOTIFY_START_POSITION = "start_position";
        public static final String TAG_NOTIFY_ITEM_COUNT = "item_count";

        public static final int WHAT_NOTIFY_INSERT = 0;
        public static final int WHAT_NOTIFY_REMOVE = 1;
        public static final int WHAT_NOTIFY_UPDATE = 2;
        public static final int WHAT_NOTIFY_WHOLE_CHANGE = 3;

        private static final int START_NOT_SET = -1;
        private static final int DEFAULT_ITEM_COUNT = 1;

//        private WeakReference<CameraMediaFragment> weakFragment;

        public RecyclerAdapterHandler(MainActivity mainActivity){
            super(mainActivity);
//            weakFragment = new WeakReference<CameraMediaFragment>(fragment);
        }

        /**
         * Handle the recycler adapter for insert/remove/update/whole_change
         * @param msg : msg.what must set for WHAT_NOTIFY_[INSERT/REMOVE/UPDATE/WHOLE_CHANGE]
         *            for each ACTION.
         *            When [INSERT/REMOVE/UPDATE], must put item start position into bundle with
         *            START_NOT_SET into bundle Within message, a optional item count can set
         *            how many items will be effected can be set with TAG_NOTIFY_ITEM_COUNT
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(weakFrag.get() == null) return;
            MainActivity mainActivity = weakFrag.get();

            try {
                Bundle data = msg.getData();
                int startPosition = data.getInt(TAG_NOTIFY_START_POSITION, START_NOT_SET);
                int itemCount = data.getInt(TAG_NOTIFY_ITEM_COUNT,DEFAULT_ITEM_COUNT);

                if(startPosition == START_NOT_SET){
                    if(msg.what == WHAT_NOTIFY_WHOLE_CHANGE){
                            mainActivity.adapter_change.notifyDataSetChanged();
                    }
                }else{
                    if(overFlowCheckSum(msg.what,startPosition,itemCount)){
                        switch (msg.what) {
                            case WHAT_NOTIFY_INSERT:
                                    mainActivity.adapter_change.notifyItemRangeInserted(startPosition, itemCount);
                                break;
                            case WHAT_NOTIFY_REMOVE:

                                //Remove data must be done here
                                for(int i = 0; i < itemCount; i++){
                                    int itemNo = startPosition + i;
                                    weakFrag.get().totalFileCounts--;
                                    weakFrag.get().listFileXML.remove(itemNo);
                                }

                                    mainActivity.adapter_change.notifyItemRangeRemoved(startPosition, itemCount);

                                break;
                            case WHAT_NOTIFY_UPDATE:
                                mainActivity.adapter_change.notifyItemRangeChanged(startPosition, itemCount);

                                break;
                        }

//                        if(weakFrag.get().checkLastChildViewShowed()){
//                                mainActivity.addDummy1(mainActivity.LIST_COUNTS_EACH_TIME);
//
//                        }
                    }
                }

            }catch (NullPointerException e){

            }
        }

        private boolean overFlowCheck(int what,int startPosition,int itemCount){
            if(startPosition < 0
                    || itemCount <= 0
                    || startPosition >= weakFrag.get().adapter_change.getItemCount()){
                return false;
            }

            if((what == WHAT_NOTIFY_INSERT || what == WHAT_NOTIFY_UPDATE)
                    && startPosition + itemCount > weakFrag.get().adapter_change.getItemCount()){
                return false;
            }

            if(what == WHAT_NOTIFY_REMOVE
                    && startPosition + itemCount > weakFrag.get().adapter_change.getItemCount()+1){
                return false;
            }

            return true;
        }

        //20170907
        private boolean overFlowCheck1(int what,int startPosition,int itemCount){
            if(startPosition < 0
                    || itemCount <= 0
                    || startPosition >= weakFrag.get().adapter_change.getItemCount()){
                return false;
            }

            if((what == WHAT_NOTIFY_INSERT || what == WHAT_NOTIFY_UPDATE)
                    && startPosition + itemCount > weakFrag.get().adapter_change.getItemCount()){
                return false;
            }

            if(what == WHAT_NOTIFY_REMOVE
                    && startPosition + itemCount > weakFrag.get().adapter_change.getItemCount()+1){
                return false;
            }

            return true;
        }

        private boolean overFlowCheckSum(int what,int startPosition,int itemCount){
                return overFlowCheck1( what, startPosition, itemCount);
        }
    }

//    protected synchronized void getMetadataInDatabase(int count, int from){
//        Log.e("getPic","getMetadataInDatabase");
//        int idx;
//        for(int i = 0; i < count; i++)
//        {
//            idx = from + i;
//            try {
//                MediaFileXML file = listFileXML.get(idx);
//                GetMetadataInDatabase getMetadata = new GetMetadataInDatabase(this, file);
//                databaseExecutor.execute(getMetadata);
//            }catch (IndexOutOfBoundsException e){
//                break;
//            }catch (NullPointerException e){
//                break;
//            }
//        }
//    }
}
