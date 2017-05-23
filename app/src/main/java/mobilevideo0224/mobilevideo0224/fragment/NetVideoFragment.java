package mobilevideo0224.mobilevideo0224.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import mobilevideo0224.mobilevideo0224.R;
import mobilevideo0224.mobilevideo0224.activity.SystemVideoPlayerActivity;
import mobilevideo0224.mobilevideo0224.adapter.NetVideoAdapter;
import mobilevideo0224.mobilevideo0224.base.BaseFragment;
import mobilevideo0224.mobilevideo0224.bean.MediaItem;
import mobilevideo0224.mobilevideo0224.bean.MoveInfo;

/**
 * 作者：田学伟 on 2017/5/19 20:23
 * QQ：93226539
 * 作用：网络视频
 */

public class NetVideoFragment extends BaseFragment {

    private NetVideoAdapter adapter;
    private ListView lv;
    private TextView tv_nodata;

    private ArrayList<MediaItem> mediaItems;
    private MaterialRefreshLayout refresh;
    private List<MoveInfo.TrailersBean> datas;
    /**
     * 是否加载更多
     */
    private boolean isLoadMore = false;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_net_video_pager, null);
        lv = (ListView) view.findViewById(R.id.lv);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        refresh = (MaterialRefreshLayout) view.findViewById(R.id.refresh);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //这个只能播放,不能实现上一个下一个
//                MoveInfo.TrailersBean item = adapter.getItem(position);
//                Intent intent = new Intent(mContext, SystemVideoPlayerActivity.class);
//                intent.setDataAndType(Uri.parse(item.getUrl()), "video/*");
//                startActivity(intent);|

                //调用自己的播放器,并且实现上一个下一个网络视频的播放
                Intent intent = new Intent(mContext, SystemVideoPlayerActivity.class);
                Bundle bunlder = new Bundle();
                bunlder.putSerializable("videolist", mediaItems);
                intent.putExtra("position", position);
                //放入Bundler
                intent.putExtras(bunlder);
                startActivity(intent);
            }
        });

        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            //下拉刷新
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                isLoadMore = false;
                getDataFromNet();
            }

            //加载更多
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                isLoadMore = true;
                getMoreData();
            }
        });

        return view;
    }

    /**
     * 上拉加载更多
     */
    private void getMoreData() {
        //联网地址
        final RequestParams params = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "加载更多xUtils联网成功==" + result);
                processData(result);
                //结束上拉刷新
                refresh.finishRefreshLoadMore();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "加载更xUtils联网失败==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG", "网络视频数据初始化了。。");
        getDataFromNet();
    }

    /**
     * 网络请求视频
     */
    private void getDataFromNet() {
        final RequestParams request = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "xUtils联网成功==" + result);
                processData(result);
                //结束下拉刷新
                refresh.finishRefresh();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "xUtils联网失败==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 解析得到数据
     *
     * @param json
     */
    private void processData(String json) {
        MoveInfo moveInfo = new Gson().fromJson(json, MoveInfo.class);
        if (!isLoadMore) {
            datas = moveInfo.getTrailers();
            if (datas != null && datas.size() > 0) {
                //集合数据MediaItem
                mediaItems = new ArrayList<>();
                for (int i = 0; i < datas.size(); i++) {
                    MediaItem mediaItem = new MediaItem();
                    mediaItem.setData(datas.get(i).getUrl());
                    mediaItem.setName(datas.get(i).getMovieName());
                    mediaItems.add(mediaItem);

                }

                tv_nodata.setVisibility(View.GONE);
                //有数据-适配器
                adapter = new NetVideoAdapter(mContext, datas);
                lv.setAdapter(adapter);
            } else {
                tv_nodata.setVisibility(View.VISIBLE);
            }
        } else {
            //加载更多得到的数据新数据
            List<MoveInfo.TrailersBean> trailersBeanList = moveInfo.getTrailers();
            //要传入播放器的
            for (int i = 0; i < trailersBeanList.size(); i++) {
                MediaItem mediaItem = new MediaItem();
                mediaItem.setData(trailersBeanList.get(i).getUrl());
                mediaItem.setName(trailersBeanList.get(i).getMovieName());
                mediaItems.add(mediaItem);
            }
            //加入到原来集合的数据
            datas.addAll(trailersBeanList);
            //刷新适配器
            adapter.notifyDataSetChanged();
        }
    }
}

