package com.dager.analyser.base;


public class PageRequest extends BaseRequest {

    private static final long serialVersionUID = 3272299982541641132L;

    /**
     * 最大分页大小为10000
     */
    private static final Integer MAX_PAGESIZE = 10000;
    /**
     * 当前页
     */
    private Integer pageNo=1;

    /**
     * 每页大小，默认10，最大10000
     */
    private Integer pageSize = 10;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        if(pageNo!=null && pageNo>0){
            this.pageNo = pageNo;
        }
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        if(pageSize!=null && pageSize>0){
            this.pageSize = pageSize > MAX_PAGESIZE ? MAX_PAGESIZE : pageSize;
        }
    }

    public Integer getLimitStart() {
        return Integer.valueOf(this.pageSize.intValue() * (this.pageNo.intValue() - 1));
    }

    public Integer getLimitSize() {
        return this.pageSize;
    }

}
