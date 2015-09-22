/*
 * #%L
 * PortfolioEffect - Quant Client
 * %%
 * Copyright (C) 2011 - 2015 Snowfall Systems, Inc.
 * %%
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 * #L%
 */
package com.portfolioeffect.quant.client.message;

public class TransmitDataResponse extends AbstractMessage {

	private static final long serialVersionUID = -4649536228905096099L;
	
	public TransmitDataResponse(String responseType) {
		this(responseType, null);
	}
	
	public TransmitDataResponse(String msgType, String msgBody) {
		super(msgType, msgBody);
	}
	
}
