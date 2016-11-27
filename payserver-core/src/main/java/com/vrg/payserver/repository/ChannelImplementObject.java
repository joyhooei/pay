/**
 *
 */
package com.vrg.payserver.repository;

import java.net.URLClassLoader;

import com.alibaba.fastjson.JSON;

public class ChannelImplementObject {
	private String channelId;
	private long lastModified;
	private long fileLength;
	private URLClassLoader urlClassLoader;

	/**
	 * @return the channelId
	 */
	public String getChannelId() {
		return channelId;
	}

	/**
	 * @param channelId
	 *            the channelId to set
	 */
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	/**
	 * @return the lastModified
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified
	 *            the lastModified to set
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return the fileLength
	 */
	public long getFileLength() {
		return fileLength;
	}

	/**
	 * @param fileLength
	 *            the fileLength to set
	 */
	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

    public URLClassLoader getUrlClassLoader() {
        return urlClassLoader;
    }

    public void setUrlClassLoader(URLClassLoader urlClassLoader) {
        this.urlClassLoader = urlClassLoader;
    }

    @Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
