package mobilevideo0224.mobilevideo0224.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mobilevideo0224.mobilevideo0224.R;
import mobilevideo0224.mobilevideo0224.base.BaseFragment;

/**
 * 作者：田学伟 on 2017/5/23 21:01
 * QQ：93226539
 * 作用：
 */

public class RecyclerViewFragment extends BaseFragment {

    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.progressbar)
    ProgressBar progressbar;
    @InjectView(R.id.tv_nomedia)
    TextView tvNomedia;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_recyclerview, null);
        ButterKnife.inject(this, view);
        return view;
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }
}
