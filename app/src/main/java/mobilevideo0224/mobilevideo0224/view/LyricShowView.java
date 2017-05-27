package mobilevideo0224.mobilevideo0224.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;

import mobilevideo0224.mobilevideo0224.bean.Lyric;

/**
 * 作者：田学伟 on 2017/5/27 13:42
 * QQ：93226539
 * 作用：
 */

public class LyricShowView extends TextView {
    private Paint paint;
    private int width;
    private int height;
    private ArrayList<Lyric> lyrics;

    public LyricShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private void initView() {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setTextSize(16);
        paint.setTextAlign(Paint.Align.CENTER);

        //准备歌词
        lyrics = new ArrayList<>();
        Lyric lyric = new Lyric();
        for (int i = 0;i<1000;i++){
           //不同的歌词
            lyric.setContent("aaaaaaaaaa_"+i);
            lyric.setSleepTime(2000);
            lyric.setTimePoint(2000+i);
            //添加到集合里面
            lyrics.add(lyric);
            lyric = new Lyric();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("没有找到歌词...", width / 2, height / 2, paint);
    }
}
