package com.nowcoder.community.entity;

/**
 * 封装分页的相关信息.
 * <p>
 * 页码是从第一页开始显示的。
 */
public class Page {

    //当前页码
    private int current = 1;

    //显示的页码上限。(一页显示limit条数据)
    private int limit = 5;

    //总的行数,也就是帖子有多少条（总数据/limit）。50条数据，每一页显示10条数据，那么总页数就是5
    private int rows;

    //查询路径（用于复用分页连接）。点击首页/末尾/上一页/下一页/1/2/3页，分别跳转到不同的页面。
    private String path;

    /**
     * 获取当前页的起始行。(根据当前的页码算出起始行是多少)(从第0行算起)
     *
     * @return
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 获取总的页数。
     * <p>
     * 如果有20行数据，每页显示10行数据，则显示2页。
     * 如果有21行数据，每页显示10行数据，则显示3页，最后一页只有1行数据。
     *
     * @return
     */
    public int getTotal() {
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return (rows / limit + 1);
        }
    }

    /**
     * 获取起始页码
     *
     * @return
     */
    public int getFrom() {
        int from = current - 2;
        //需要判断from页会不会超出范围。
        return from < 1 ? 1 : from;

    }

    /**
     * 获取结束页码
     *
     * @return
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to < total ? to : total;


    }


    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }

    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
