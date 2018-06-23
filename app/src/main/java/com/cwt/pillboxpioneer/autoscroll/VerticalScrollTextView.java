package com.cwt.pillboxpioneer.autoscroll;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 曹吵吵 on 2018/6/23 0023.
 */

public class VerticalScrollTextView extends AppCompatTextView {
    private float step =0f;
    private Paint mPaint=new Paint(); ;
    private String text;
    private float width;
    private List<String> textList = new ArrayList<String>();    //分行保存textview的显示信息。
    private float spead =0.3f;
    private float textSize =40f;
    private int num_one_line;

    public VerticalScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //TypedArray typedArray = context.obtainStyledAttributes(attrs, android.support.v7.appcompat.R.styleable.AppCompatTextView);
        textSize=super.getTextSize();
        Log.e("VerticalScrollTextView","textSize:"+textSize);
    }


    public VerticalScrollTextView(Context context) {
        super(context);
    }

    public void setSpead(float speed){
        this.spead=speed;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        num_one_line= (int) (width/textSize)-1;
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("ScrollLayout only canmCurScreen run at EXACTLY mode!");
        }

        text=getText().toString();
        if(text==null|text.length()==0){

            return ;
        }

        //下面的代码是根据宽度和字体大小，来计算textview显示的行数。

        textList.clear();
        textList.add(text);
        /*StringBuilder builder =null;
        int buf=-1;
        for(int i=0;i<text.length();i++){
            buf++;
            if(buf==0){
                builder = new StringBuilder();
            }
            if(buf<=num_one_line-1){
                builder.append(text.charAt(i));
            }
            if(buf==num_one_line-1||text.charAt(i)=='\n'||i==text.length()-1){
                textList.add(builder.toString());
                buf=-1;
            }
        }
        Log.e("VerticalScrollTextView",""+textList.size());*/

    }


    //下面代码是利用上面计算的显示行数，将文字画在画布上，实时更新。
    @Override
    public void onDraw(Canvas canvas) {
        if(textList.size()==0)  return;

        mPaint.setTextSize(textSize);//设置字体大小
        for(int i = 0; i < textList.size(); i++) {
            canvas.drawText(textList.get(i), 0, this.getHeight()+(i+1)*mPaint.getTextSize()-step+30, mPaint);
        }
        invalidate();

        step = step+spead;
        if (step >= this.getHeight()+textList.size()*mPaint.getTextSize()) {
            step = 0;
        }
    }
}
