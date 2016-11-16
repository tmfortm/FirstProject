package com.taoge.firstproject.beans;

/**
 * Created by my on 2016/11/12.
 */

public class Web {
    private WebItem data;

    public WebItem getData() {
        return data;
    }

    public void setData(WebItem data) {
        this.data = data;
    }

    public class WebItem {
        private String author;
        private String create_time;
        private String id;
        private String source;
        private String title;
        private String weiboUrl;
        private String wap_content;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getCreateTime() {
            return create_time;
        }

        public void setCreateTime(String createTime) {
            this.create_time = createTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTwitterUrl() {
            return weiboUrl;
        }

        public void setTwitterUrl(String twitterUrl) {
            this.weiboUrl = twitterUrl;
        }

        public String getWap_content() {
            return wap_content;
        }

        public void setWap_content(String wap_content) {
            this.wap_content = wap_content;
        }
    }
}
