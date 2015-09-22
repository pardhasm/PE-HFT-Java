/*
 * #%L
 * ICE-9 - Algo Client API
 * %%
 * Copyright (C) 2010 - 2015 Snowfall Systems, Inc.
 * %%
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 * #L%
 */
package com.portfolioeffect.quant.client.message;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author alex
 *
 */
public class TestRequest extends FastMessage{

	private static final long serialVersionUID = -3348342181849875479L;
	private String testReqID;
	
	public TestRequest(StandardHeader messageHeader) {
		super(messageHeader);
	}

	public String getTestReqID() {
		return testReqID;
	}

	public void setTestReqID(String testReqID) {
		this.testReqID = testReqID;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
