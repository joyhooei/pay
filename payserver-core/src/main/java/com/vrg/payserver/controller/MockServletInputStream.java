/**
 * 
 */
package com.vrg.payserver.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 *
 */
public class MockServletInputStream extends ServletInputStream {
	private ByteArrayInputStream bais;

	public MockServletInputStream(byte[] streamBytes) {
		this.bais = new ByteArrayInputStream(streamBytes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletInputStream#isFinished()
	 */
	@Override
	public boolean isFinished() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletInputStream#isReady()
	 */
	@Override
	public boolean isReady() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletInputStream#setReadListener(javax.servlet.ReadListener
	 * )
	 */
	@Override
	public void setReadListener(ReadListener readListener) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		return bais.read();
	}

}
