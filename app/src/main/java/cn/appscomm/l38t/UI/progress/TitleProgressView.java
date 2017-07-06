package cn.appscomm.l38t.UI.progress;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.appscomm.l38t.R;


/**
 * Created by Administrator on 2016/8/15.
 */
public class TitleProgressView extends LinearLayout {

    private TextView tv_name;
    private TextView tv_goal;
    private TextView tvCurr;
    private TextView tv_descript;
    private ImageView icon_left;
    private ImageView icon_right;
    private RelativeLayout rl_progress;
    private ProgressBar progressBar;

    private String name;
    private String goal;
    private String curr;
    private String descript;
    private int icon;
    private int progressId;
    private int currValue, goalValue;


    public TitleProgressView(Context context) {
        super(context);
        init(context);
    }

    public TitleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TitleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public TitleProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_title_progress,this, true);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_goal = (TextView) findViewById(R.id.tv_goal);
        tvCurr = (TextView) findViewById(R.id.tvCurr);
        tv_descript = (TextView) findViewById(R.id.tv_descript);
        icon_left = (ImageView) findViewById(R.id.icon_left);
        icon_right = (ImageView) findViewById(R.id.icon_right);
        rl_progress = (RelativeLayout) findViewById(R.id.rl_progress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public TitleProgressView reFreshData(String name, String goal, String curr, String descript, int icon, int progressId, int currValue, int goalValue) {
        this.name = name;
        this.goal = goal;
        this.curr = curr;
        this.descript = descript;
        this.icon = icon;
        this.progressId = progressId;
        this.currValue = currValue;
        this.goalValue = goalValue;
        invalite();
        return this;
    }

    private void invalite(){
        setProgress(currValue, goalValue, goalValue);
        setName(name).setGoal(goal).setCurr(curr).setDescript(descript).setIcon(icon).setProgressBarBackground(progressId);
    }

    public TitleProgressView setIcon(int drawable) {
        if (icon_right != null) {
            icon_right.setImageResource(drawable);
        }
        if (icon_left != null) {
            icon_left.setImageResource(drawable);
        }
        return this;
    }

    public TitleProgressView setName(String name) {
        if (tv_name != null) {
            tv_name.setText(name);
        }
        return this;
    }

    public TitleProgressView setGoal(String goal) {
        if (tv_goal != null) {
            tv_goal.setText(goal);
        }
        return this;
    }

    public TitleProgressView setCurr(String curr) {
        if (tvCurr != null) {
            tvCurr.setText(curr);
        }
        return this;
    }

    public TitleProgressView setDescript(String descript) {
        if (tv_descript != null) {
            tv_descript.setText(descript);
        }
        return this;
    }

    public TitleProgressView setProgress(int progress, int secondProgress, int max) {
        if (progressBar != null) {
//            if (progress >= max) {
//                icon_right.setVisibility(View.VISIBLE);
//                icon_left.setVisibility(View.GONE);
//            } else {
//                icon_right.setVisibility(View.GONE);
//                icon_left.setVisibility(View.VISIBLE);
//            }
            icon_right.setVisibility(View.VISIBLE);
            icon_left.setVisibility(View.GONE);
            progressBar.setMax(max);
            progressBar.setSecondaryProgress(secondProgress);
            progressBar.setProgress(progress);
            progressBar.invalidate();
        }
        return this;
    }

    public TitleProgressView setProgressBarBackground(int progressResourceId) {
        if (progressBar != null) {
            progressBar.setProgressDrawable(getResources().getDrawable(progressResourceId));
        }
        return this;
    }

}
