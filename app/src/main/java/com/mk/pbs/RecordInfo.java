package com.mk.pbs;

public class RecordInfo {
    private String reportNo;
    private String msg;

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "RecordInfo{" +
                "reportNo='" + reportNo + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
