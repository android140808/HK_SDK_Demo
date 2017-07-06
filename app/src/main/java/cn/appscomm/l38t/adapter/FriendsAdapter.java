package cn.appscomm.l38t.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.UI.show.CircularImage;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.model.bean.LeaderBoardFriend;
import cn.appscomm.l38t.utils.UniversalImageLoaderHelper;
import cn.appscomm.netlib.util.DateUtil;


/**
 * Created by weiliu on 2016/7/20.
 */
public class FriendsAdapter extends BaseAdapter {
    private static final String TAG = FriendsAdapter.class.getSimpleName();
    private Context context;
    private List<LeaderBoardFriend> friendsList;
    private LayoutInflater inflater;
    private Holder holder = null;
    private Handler mHandler;

    public FriendsAdapter(Context context, List<LeaderBoardFriend> friendsList) {
        this.context = context;
        this.friendsList = friendsList;
        inflater = LayoutInflater.from(context);
        mHandler = new Handler();
    }

    @Override
    public int getCount() {
        if (friendsList != null)
            return friendsList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (friendsList != null && friendsList.size() > 0)
            return friendsList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_leader_board_friend, null);
            holder = new Holder();
            holder.tv_ranking = (TextView) convertView.findViewById(R.id.tv_ranking);
            holder.ci_pic = (CircularImage) convertView.findViewById(R.id.ci_pic);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_step = (TextView) convertView.findViewById(R.id.tv_step);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        final LeaderBoardFriend friend = (LeaderBoardFriend) getItem(position);
        if (friend != null) {
            UniversalImageLoaderHelper.displayImage(friend.getIconUrl(),holder.ci_pic);
            holder.tv_ranking.setText(friend.getRank() + "");
            holder.tv_name.setText(friend.getUserName());
            holder.tv_step.setText(friend.getSportsStep() + "");
            if (friend.getDdId() == AccountConfig.getAccountDDID()) {
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.header_bg_color));
                holder.tv_step.setTextColor(context.getResources().getColor(R.color.header_bg_color));
            } else {
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.black));
                holder.tv_step.setTextColor(context.getResources().getColor(R.color.header_bg_color));
            }
            if (friend.getUpdateTime() > 0) {
                holder.tv_time.setVisibility(View.VISIBLE);
                Date date = DateUtil.timestampToDate(friend.getUpdateTime());
                holder.tv_time.setText(DateUtil.getNowDateAgoShowString(context, date) + "");
            } else {
                holder.tv_time.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    static class Holder {
        TextView tv_ranking;
        CircularImage ci_pic;
        TextView tv_name;
        TextView tv_step;
        TextView tv_time;
    }
}
