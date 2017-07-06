package cn.appscomm.l38t.model.database;

import org.litepal.crud.DataSupport;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/12 10:36
 */
public class QueryLeaderBoardDB extends DataSupport {

    private int ddId;
    private String queryDateStart;
    private String queryDateEnd;
    private String content;

    public int getDdId() {
        return ddId;
    }

    public void setDdId(int ddId) {
        this.ddId = ddId;
    }

    public String getQueryDateStart() {
        return queryDateStart;
    }

    public void setQueryDateStart(String queryDateStart) {
        this.queryDateStart = queryDateStart;
    }

    public String getQueryDateEnd() {
        return queryDateEnd;
    }

    public void setQueryDateEnd(String queryDateEnd) {
        this.queryDateEnd = queryDateEnd;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
