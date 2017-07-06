package cn.appscomm.l38t.config.bean;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/5 08:58
 */
public class LocalUserGoal {
    public static final int DEFAULT_STEP = 7000;//步
    public static final int DEFAULT_DISTANCE = 5;//千米
    public static final int DEFAULT_ACTIVE = 30;//分钟
    public static final int DEFAULT_CALORIES = 350;//千卡
    public static final int DEFAULT_SLEEP = 8;//小时
    
    private int goals_step;
    private int goals_distance;
    private int goals_activeMinutes;
    private int goals_calories;
    private int goals_sleep;

    public LocalUserGoal() {
    }

    public LocalUserGoal(int goals_step, int goals_distance, int goals_activeMinutes, int goals_calories, int goals_sleep) {
        this.goals_step = goals_step;
        this.goals_distance = goals_distance;
        this.goals_activeMinutes = goals_activeMinutes;
        this.goals_calories = goals_calories;
        this.goals_sleep = goals_sleep;
    }

    public int getGoals_step() {
        return goals_step;
    }

    public void setGoals_step(int goals_step) {
        this.goals_step = goals_step;
    }

    public int getGoals_distance() {
        return goals_distance;
    }

    public void setGoals_distance(int goals_distance) {
        this.goals_distance = goals_distance;
    }

    public int getGoals_activeMinutes() {
        return goals_activeMinutes;
    }

    public void setGoals_activeMinutes(int goals_activeMinutes) {
        this.goals_activeMinutes = goals_activeMinutes;
    }

    public int getGoals_calories() {
        return goals_calories;
    }

    public void setGoals_calories(int goals_calories) {
        this.goals_calories = goals_calories;
    }

    public int getGoals_sleep() {
        return goals_sleep;
    }

    public void setGoals_sleep(int goals_sleep) {
        this.goals_sleep = goals_sleep;
    }
}
