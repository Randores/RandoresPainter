/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 socraticphoenix@gmail.com
 * Copyright (c) 2016 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gmail.socraticphoenix.forge.randores.painter.git;

public class GitItem {
    private String name;
    private String path;
    private String sha;
    private long size;
    private String url;
    private String html_url;
    private String git_url;
    private String download_url;
    private String type;

    public GitItem(String name, String path, String sha, long size, String url, String html_url, String git_url, String download_url, String type) {
        this.name = name;
        this.path = path;
        this.sha = sha;
        this.size = size;
        this.url = url;
        this.html_url = html_url;
        this.git_url = git_url;
        this.download_url = download_url;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public GitItem setName(String name) {
        this.name = name;
        return this;
    }

    public String getPath() {
        return this.path;
    }

    public GitItem setPath(String path) {
        this.path = path;
        return this;
    }

    public String getSha() {
        return this.sha;
    }

    public GitItem setSha(String sha) {
        this.sha = sha;
        return this;
    }

    public long getSize() {
        return this.size;
    }

    public GitItem setSize(long size) {
        this.size = size;
        return this;
    }

    public String getUrl() {
        return this.url;
    }

    public GitItem setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getHtml_url() {
        return this.html_url;
    }

    public GitItem setHtml_url(String html_url) {
        this.html_url = html_url;
        return this;
    }

    public String getGit_url() {
        return this.git_url;
    }

    public GitItem setGit_url(String git_url) {
        this.git_url = git_url;
        return this;
    }

    public String getDownload_url() {
        return this.download_url;
    }

    public GitItem setDownload_url(String download_url) {
        this.download_url = download_url;
        return this;
    }

    public String getType() {
        return this.type;
    }

    public GitItem setType(String type) {
        this.type = type;
        return this;
    }
}
