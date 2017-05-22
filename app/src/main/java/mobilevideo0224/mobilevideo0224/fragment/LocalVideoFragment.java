package mobilevideo0224.mobilevideo0224.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import mobilevideo0224.mobilevideo0224.R;
import mobilevideo0224.mobilevideo0224.activity.SystemVideoPlayerActivity;
import mobilevideo0224.mobilevideo0224.adapter.LocalVideoAdapter;
import mobilevideo0224.mobilevideo0224.base.BaseFragment;
import mobilevideo0224.mobilevideo0224.bean.MediaItem;

/**
 * 作者：田学伟 on 2017/5/19 20:22
 * QQ：93226539
 * 作用：本地视频
 */

public class LocalVideoFragment extends BaseFragment {

    private ListView listview;
    private TextView tv_no_media;
    private ArrayList<MediaItem> mediaItems;
    private LocalVideoAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size() > 0) {
                tv_no_media.setVisibility(View.GONE);
                adapter = new LocalVideoAdapter(mContext, mediaItems, true);
                //设置适配器
                listview.setAdapter(adapter);
            } else {
                //没有数据,文本显示
                tv_no_media.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public View initView() {
        Log.e("TAG", "本地音乐ui初始化了。。");
        View view = View.inflate(mContext, R.layout.fragment_local_video, null);
        //初始化
        listview = (ListView) view.findViewById(R.id.listview);
        tv_no_media = (TextView) view.findViewById(R.id.tv_no_media);

        //设置listview的item的监听
        listview.setOnItemClickListener(new MyOnItemClickListener());

        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //            MediaItem mediaItem = mediaItems.get(position);
            MediaItem item = adapter.getItem(position);
            // Toast.makeText(mContext, "" + item.toString(), Toast.LENGTH_SHORT).show();
            /**
             * 调用系统的播放器播放视频
             */
            //Intent intent = new Intent();
            Intent intent = new Intent(mContext,SystemVideoPlayerActivity.class);
            Bundle bunlder = new Bundle();
            bunlder.putSerializable("videolist",mediaItems);
            intent.putExtra("position",position);
            intent.putExtras(bunlder);
            startActivity(intent);
        }
    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG", "本地音乐数据初始化了。。");
        getData();
    }

    private void getData() {
        new Thread() {
            public void run() {
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver = mContext.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//在sdcard显示的视频名称
                        MediaStore.Video.Media.DURATION,//视频的时长,毫秒
                        MediaStore.Video.Media.SIZE,//文件大小-byte
                        MediaStore.Video.Media.DATA,//在sdcard的路径-播放地址
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(0);
                        long duration = cursor.getLong(1);
                        long size = cursor.getLong(2);
                        String data = cursor.getString(3);

                        mediaItems.add(new MediaItem(name, duration, size, data));

                        //使用handler
                        handler.sendEmptyMessage(0);
                    }
                    cursor.close();
                }
            }
        }.start();
    }
}

