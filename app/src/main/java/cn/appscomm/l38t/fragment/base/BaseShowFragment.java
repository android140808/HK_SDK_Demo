package cn.appscomm.l38t.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.appscomm.l38t.UI.showView.BaseFragShowView;


/**
 * Created by weiliu on 2016/7/25.
 */
public class BaseShowFragment extends BaseFragment {

    private final String TAG = BaseShowFragment.class.getSimpleName();

    protected BaseFragShowView baseFragBottomView;
    private boolean mInitailized = false;
    protected boolean isSelected;
    private Calendar cal = Calendar.getInstance();
    protected static final String DateFormatString = "yyyy/MM/dd";

    @Override
    public String getFragmentTAG() {
        return BaseShowFragment.class.getSimpleName();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInitailized = true;
        initView(view);
        cal.setTime(new Date());
        loadData(cal.getTime());
    }

    protected void initView(final View view) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (baseFragBottomView == null) {
            baseFragBottomView = new BaseFragShowView(activity);
            baseFragBottomView.setIvDatePreOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickDatePre();
                }
            });
            baseFragBottomView.setIvDateNextOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickDateNext();
                }
            });
        }
        return baseFragBottomView;
    }

    protected void loadData(Date now) {
        baseFragBottomView.setTvDateTime(new SimpleDateFormat(DateFormatString).format(now));
    }


    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    protected void clickDatePre() {
        cal.add(Calendar.DATE, -1);
        loadData(cal.getTime());
    }

    protected void clickDateNext() {
        cal.add(Calendar.DATE, +1);
        loadData(cal.getTime());
    }
}
