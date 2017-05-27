package mobilevideo0224.mobilevideo0224.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import mobilevideo0224.mobilevideo0224.IMusicPlayService;
import mobilevideo0224.mobilevideo0224.R;
import mobilevideo0224.mobilevideo0224.bean.MediaItem;
import mobilevideo0224.mobilevideo0224.service.MusicPlayService;
import mobilevideo0224.mobilevideo0224.utils.Utils;

public class SystemAudioPlayerActivity extends AppCompatActivity {

    @InjectView(R.id.iv_icon)
    ImageView ivIcon;
    @InjectView(R.id.tv_artist)
    TextView tvArtist;
    @InjectView(R.id.tv_audioname)
    TextView tvAudioname;
    @InjectView(R.id.rl_top)
    RelativeLayout rlTop;
    @InjectView(R.id.tv_time)
    TextView tvTime;
    @InjectView(R.id.seekbar_audio)
    SeekBar seekbarAudio;
    @InjectView(R.id.btn_playmode)
    Button btnPlaymode;
    @InjectView(R.id.btn_pre)
    Button btnPre;
    @InjectView(R.id.btn_start_pause)
    Button btnStartPause;
    @InjectView(R.id.btn_next)
    Button btnNext;
    @InjectView(R.id.btn_lyric)
    Button btnLyric;
    @InjectView(R.id.ll_bottom)
    LinearLayout llBottom;
    //这个就是IMusicPlayService.Stub的实例
    private IMusicPlayService service;
    private int position;
    private MyReceiver receiver;
    private Utils utils;
    private final static int PROGRESS = 0;

    private boolean notification;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    try {
                        int currentPosition = service.getCurrentPosition();
                        seekbarAudio.setProgress(currentPosition);
                        //设置更新时间
                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getDuration()));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    //每秒钟更新一次
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };
    private ServiceConnection conon = new ServiceConnection() {
        /**
         * 当绑定服务成功的时候回调
         * @param name
         * @param iBinder    就是IMusicPlayService.Stub的实例
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            //这个就是stub，stub包含很多方法，这些方法调用服务的方法
            service = IMusicPlayService.Stub.asInterface(iBinder);
            try {
                if (notification) {
                    //什么不用做
                    setViewData(null);
                } else {
                    service.openAudio(position);//打开播放第0个音频
                    tvArtist.setText(service.getArtistName());
                    tvAudioname.setText(service.getAudioName());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /**
         * 当断开连接的时候
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_audio_player);
        ButterKnife.inject(this);
        initData();
        setListener();

        ivIcon.setBackgroundResource(R.drawable.animation_bg);
        AnimationDrawable drawable = (AnimationDrawable) ivIcon.getBackground();
        drawable.start();
        getData();
        startAndBindService();
    }

    private void setListener() {
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            try {
                if (fromUser) {
                    service.seekTo(progress);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private void initData() {
        //注册广播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayService.OPEN_COMPLETE);
        registerReceiver(receiver, intentFilter);

        utils = new Utils();
        //1.注册EventButs
        EventBus.getDefault().register(this);
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //主线程
            setViewData(null);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setViewData(MediaItem mediaItem) {
        try {
            setButtonImage();

            int duration = service.getDuration();
            seekbarAudio.setMax(duration);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //发消息更新进度
        handler.sendEmptyMessage(PROGRESS);
    }

    private void startAndBindService() {
        Intent intent = new Intent(this, MusicPlayService.class);
        //绑定-得到服务的操作对象-IMusicPlayService service
        bindService(intent, conon, Context.BIND_AUTO_CREATE);
        //防止多次实例化Service
        startService(intent);
    }

    private void getData() {
        notification = getIntent().getBooleanExtra("notification", false);
        if (!notification) {
            position = getIntent().getIntExtra("position", 0);
        }
    }

    @OnClick({R.id.btn_playmode, R.id.btn_pre, R.id.btn_start_pause, R.id.btn_next, R.id.btn_lyric})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_playmode:
                setPlayMode();
                break;
            case R.id.btn_pre:
                try {
                    service.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_start_pause:
                try {
                    if (service.isPlaying()) {
                        //暂停
                        service.pause();
                        //按钮状态--播放
                        btnStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                    } else {
                        //播放
                        service.start();
                        //按钮状态--暂停
                        btnStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_next:
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_lyric:
                break;
        }
    }

    private void setPlayMode() {
        try {
            int playmode = service.getPlaymode();
            if (playmode == MusicPlayService.REPEAT_NORMAL) {
                playmode = MusicPlayService.REPEAT_SINGLE;
            } else if (playmode == MusicPlayService.REPEAT_SINGLE) {
                playmode = MusicPlayService.REPEAT_ALL;
            } else if (playmode == MusicPlayService.REPEAT_ALL) {
                playmode = MusicPlayService.REPEAT_NORMAL;
            }
            //保存到服务里面
            service.setPlaymode(playmode);
            setButtonImage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setButtonImage() {
        try {
            int playmode = service.getPlaymode();
            if (playmode == MusicPlayService.REPEAT_NORMAL) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_normal_selector);
            } else if (playmode == MusicPlayService.REPEAT_SINGLE) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_single_selector);
            } else if (playmode == MusicPlayService.REPEAT_ALL) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_all_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (conon != null) {
            unbindService(conon);
            conon = null;
        }
        //广播取消注册
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        //2.取消注册EventBus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
