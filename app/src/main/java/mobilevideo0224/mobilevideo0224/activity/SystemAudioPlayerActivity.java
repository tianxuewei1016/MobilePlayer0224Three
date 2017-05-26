package mobilevideo0224.mobilevideo0224.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mobilevideo0224.mobilevideo0224.R;
import mobilevideo0224.mobilevideo0224.service.MusicPlayService;

public class SystemAudioPlayerActivity extends AppCompatActivity {

    @InjectView(R.id.iv_icon)
    ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_audio_player);
        ButterKnife.inject(this);
        ivIcon.setBackgroundResource(R.drawable.animation_bg);
        AnimationDrawable drawable = (AnimationDrawable) ivIcon.getBackground();
        drawable.start();

        Intent intent = new Intent(this, MusicPlayService.class);
        startService(intent);
    }
}
