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
package com.portfolioeffect.quant.client.portfolio.optimizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

import com.portfolioeffect.quant.client.portfolio.ArrayCache;
import com.portfolioeffect.quant.client.portfolio.Portfolio;
import com.portfolioeffect.quant.client.result.MethodResult;
import com.portfolioeffect.quant.client.util.Console;
import com.portfolioeffect.quant.client.util.MessageStrings;
import com.portfolioeffect.quant.client.util.MetricStringBuilder;

public class ForecastedValues{

	private boolean[][] isSymbolCumulantPresent;
	private boolean[] isIndexCumulantPresent = { false, false, false, false };
	private int symbolNamber = 0;
	private ArrayList<String> symbolsName;

	private ArrayCache[][] forecastedSymbolValue;
	private ArrayCache[][] forecastedSymbolValueTime;

	private ArrayCache[] forecastedIndexValue;
	private ArrayCache[] forecastedIndexValueTime;

	private boolean[][] isCumulants;
	private boolean[] isCumulantsIndex = new boolean[2];

	private boolean isTimeStep;
	private ArrayCache timeStep;
	private ArrayCache timeStepTimeMilliSec;
	private int N = 1;

	public ForecastedValues(Portfolio portfolio){

		symbolsName = new ArrayList<String>(portfolio.getSymbolNamesList());

		symbolNamber = symbolsName.size();

		isSymbolCumulantPresent = new boolean[symbolNamber][];

		forecastedSymbolValue = new ArrayCache[symbolNamber][];
		forecastedSymbolValueTime = new ArrayCache[symbolNamber][];

		isCumulants = new boolean[symbolNamber][];

		forecastedIndexValue = new ArrayCache[4];
		forecastedIndexValueTime = new ArrayCache[4];

		for (int i = 0; i < symbolNamber; i++) {
			forecastedSymbolValue[i] = new ArrayCache[5];
			forecastedSymbolValueTime[i] = new ArrayCache[5];

			isCumulants[i] = new boolean[2];

			isSymbolCumulantPresent[i] = new boolean[5];
			isSymbolCumulantPresent[i][0] = false;
			isSymbolCumulantPresent[i][1] = false;
			isSymbolCumulantPresent[i][2] = false;
			isSymbolCumulantPresent[i][3] = false;
			isSymbolCumulantPresent[i][4] = false; // beta
		}

		isIndexCumulantPresent[0] = true;// index expReturn is not need

	}

	public MethodResult isAllForecastedValuesPresent() {

		String symbols = "";

		for (int i = 0; i < 4; i++) {
			if (!isIndexCumulantPresent[i]) {
				symbols += "Index-forecasted cumulant" + (i + 1) + ";\n";
			}
		}

		if (!isTimeStep) {

			symbols += "forecast time step;\n";

		}

		for (int i = 0; i < symbolNamber; i++) {
			for (int j = 0; j < 4; j++) {
				if (!isSymbolCumulantPresent[i][j]) {
					symbols += symbolsName.get(i) + "-forecasted cumulant"
							+ (j + 1) + ";\n";
				}
			}

			if (!isSymbolCumulantPresent[i][4]) {
				symbols += symbolsName.get(i) + "-forecasted beta;\n";
			}

		}

		if (symbols.equals(""))
			return new MethodResult();
		else
			return new MethodResult("The next values not defined:\n" + symbols);
	}

	public MethodResult setForecastTimeStep(double value) {
		return setForecastTimeStep(new double[] { value }, new long[] { -1 });
	}

	public MethodResult setForecastTimeStep(String value) {
		try {
			
			N = parseTimeInterval(value,"forecast time step");
			
			return setForecastTimeStep(
					new double[] { N }, new long[] { -1 });
		} catch (Exception e) {
			return new MethodResult(e.getMessage());
		}
	}

	public MethodResult setForecastTimeStep(double[] value, long[] time) {

		try {
			timeStep = new ArrayCache(value);
			
			timeStepTimeMilliSec = new ArrayCache(time);
			

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new MethodResult(e.getMessage());
			else
				return new MethodResult(MessageStrings.ERROR_FILE);
		}

		isTimeStep = true;

		return new MethodResult();
	}

	public MethodResult setSymbolForecastedExpReturn(String symbol,
			double[] value, long[] time) {

		if (!symbolsName.contains(symbol))
			return new MethodResult(String.format(MessageStrings.SYMBOL_NOT_IN_PORTFOLIO, symbol));

		int index = symbolsName.indexOf(symbol);

		try {
			forecastedSymbolValue[index][0] = new ArrayCache(value);
			
			forecastedSymbolValueTime[index][0] = new ArrayCache(time);
			

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new MethodResult(e.getMessage());
			else
				return new MethodResult(MessageStrings.ERROR_FILE);
		}

		isSymbolCumulantPresent[index][0] = true;

		return new MethodResult();
	}

	public MethodResult setSymbolForecastedBeta(String symbol, double[] value,
			long[] time) {

		if (!symbolsName.contains(symbol))
			return new MethodResult(String.format(MessageStrings.SYMBOL_NOT_IN_PORTFOLIO, symbol));

		int index = symbolsName.indexOf(symbol);

		try {
			forecastedSymbolValue[index][4] = new ArrayCache(value);
			forecastedSymbolValueTime[index][4] = new ArrayCache(time);
			

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new MethodResult(e.getMessage());
			else
				return new MethodResult(MessageStrings.ERROR_FILE);
		}

		isSymbolCumulantPresent[index][4] = true;

		return new MethodResult();
	}

	public MethodResult setSymbolForecastedVariance(String symbol,
			double[] value, long[] time) {

		if (!symbolsName.contains(symbol))
			return new MethodResult(String.format(MessageStrings.SYMBOL_NOT_IN_PORTFOLIO, symbol));

		int index = symbolsName.indexOf(symbol);

		try {
			forecastedSymbolValue[index][1] = new ArrayCache(value);
			
			forecastedSymbolValueTime[index][1] = new ArrayCache(time);
			

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new MethodResult(e.getMessage());
			else
				return new MethodResult(MessageStrings.ERROR_FILE);
		}

		isSymbolCumulantPresent[index][1] = true;

		return new MethodResult();
	}

	public MethodResult setSymbolForecastedSkewness(String symbol,
			double[] value, long[] time) {

		if (!symbolsName.contains(symbol))
			return new MethodResult(String.format(MessageStrings.SYMBOL_NOT_IN_PORTFOLIO, symbol));

		int index = symbolsName.indexOf(symbol);

		try {
			forecastedSymbolValue[index][2] = new ArrayCache(value);
			
			forecastedSymbolValueTime[index][2] = new ArrayCache(time);
			

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new MethodResult(e.getMessage());
			else
				return new MethodResult(MessageStrings.ERROR_FILE);
		}

		isSymbolCumulantPresent[index][2] = true;

		isCumulants[index][0] = false;

		return new MethodResult();
	}

	public MethodResult setSymbolForecastedKurtosis(String symbol,
			double[] value, long[] time) {

		if (!symbolsName.contains(symbol))
			return new MethodResult(String.format(MessageStrings.SYMBOL_NOT_IN_PORTFOLIO, symbol));

		int index = symbolsName.indexOf(symbol);

		try {
			forecastedSymbolValue[index][3] = new ArrayCache(value);
			
			forecastedSymbolValueTime[index][3] = new ArrayCache(time);
			

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new MethodResult(e.getMessage());
			else
				return new MethodResult(MessageStrings.ERROR_FILE);
		}

		isSymbolCumulantPresent[index][3] = true;

		isCumulants[index][0] = false;

		return new MethodResult();
	}

	public MethodResult setSymbolForecastedCumulant1(String symbol,
			double[] value, long[] time) {

		return setSymbolForecastedExpReturn(symbol, value, time);

	}

	public MethodResult setSymbolForecastedCumulant2(String symbol,
			double[] value, long[] time) {

		return setSymbolForecastedVariance(symbol, value, time);

	}

	public MethodResult setSymbolForecastedCumulant3(String symbol,
			double[] value, long[] time) {

		MethodResult result = setSymbolForecastedSkewness(symbol, value, time);
		isCumulants[symbolsName.indexOf(symbol)][0] = true;
		return result;

	}

	public MethodResult setSymbolForecastedCumulant4(String symbol,
			double[] value, long[] time) {

		MethodResult result = setSymbolForecastedKurtosis(symbol, value, time);
		isCumulants[symbolsName.indexOf(symbol)][1] = true;
		return result;
	}

	public MethodResult setIndexForecastedVariance(double[] value, long[] time) {

		try {
			forecastedIndexValue[1] = new ArrayCache(value);
			
			forecastedIndexValueTime[1] = new ArrayCache(time);
			

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new MethodResult(e.getMessage());
			else
				return new MethodResult(MessageStrings.ERROR_FILE);
		}

		isIndexCumulantPresent[1] = true;

		return new MethodResult();
	}

	public MethodResult setIndexForecastedSkewness(double[] value, long[] time) {

		try {
			forecastedIndexValue[2] = new ArrayCache(value);
			
			forecastedIndexValueTime[2] = new ArrayCache(time);
			

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new MethodResult(e.getMessage());
			else
				return new MethodResult(MessageStrings.ERROR_FILE);
		}

		isIndexCumulantPresent[2] = true;

		isCumulantsIndex[0] = false;

		return new MethodResult();
	}

	public MethodResult setIndexForecastedKurtosis(double[] value, long[] time) {

		try {
			forecastedIndexValue[3] = new ArrayCache(value);
			
			forecastedIndexValueTime[3] = new ArrayCache(time);
			

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new MethodResult(e.getMessage());
			else
				return new MethodResult(MessageStrings.ERROR_FILE);
		}

		isIndexCumulantPresent[3] = true;

		isCumulantsIndex[0] = false;

		return new MethodResult();
	}

	
	public MethodResult setIndexForecastedCumulant2(double[] value, long[] time) {

		return setIndexForecastedVariance(value, time);

	}

	public MethodResult setIndexForecastedCumulant3(double[] value, long[] time) {
		MethodResult result = setIndexForecastedSkewness(value, time);
		isCumulantsIndex[0] = true;
		return result;
	}

	public MethodResult setIndexForecastedCumulant4(double[] value, long[] time) {

		MethodResult result = setIndexForecastedKurtosis(value, time);
		isCumulantsIndex[1] = true;
		return result;
	}

	public MethodResult makeSimpleCumulantsForecastT(Portfolio portfolio) {

		for(String e:portfolio.getSymbolNamesList()){
			portfolio.setPositionQuantity(e, 1);
		} 
		portfolio.setPortfolioMetricsMode("price");
		
		portfolio.startBatch();
		MetricStringBuilder metricStringbuilder = new MetricStringBuilder();
		try {
			MethodResult result;
			for (String symbol : symbolsName) {
 
				portfolio.addMetricToBatch(metricStringbuilder.setMetric("POSITION_BETA").setPosition(symbol).getJSON());

				portfolio.addMetricToBatch(metricStringbuilder.setMetric("POSITION_EXPECTED_RETURN").setPosition(symbol).getJSON());
				portfolio.addMetricToBatch(metricStringbuilder.setMetric("POSITION_VARIANCE").setPosition(symbol).getJSON());
				portfolio.addMetricToBatch(metricStringbuilder.setMetric("POSITION_CUMULANT3").setPosition(symbol).getJSON());
				portfolio.addMetricToBatch(metricStringbuilder.setMetric("POSITION_CUMULANT4").setPosition(symbol).getJSON());

			}

			//portfolio.addMetricToBatch(metricStringbuilder.setMetric("INDEX_EXPECTED_RETURN").getJSON());
			portfolio.addMetricToBatch(metricStringbuilder.setMetric("INDEX_VARIANCE").getJSON());
			portfolio.addMetricToBatch(metricStringbuilder.setMetric("INDEX_CUMULANT3").getJSON());
			portfolio.addMetricToBatch(metricStringbuilder.setMetric("INDEX_CUMULANT4").getJSON());

			portfolio.finishBatch();

			for (String symbol : symbolsName) {

				result = checkResult(portfolio
						.getMetric(metricStringbuilder.setMetric("POSITION_BETA").setPosition(symbol).getJSON()));
				checkResult(setSymbolForecastedBeta(symbol, result.getDoubleArray("value"),
						result.getLongArray("time")));

				result = checkResult(portfolio
						.getMetric(metricStringbuilder.setMetric("POSITION_EXPECTED_RETURN").setPosition(symbol).getJSON()));
								
				checkResult(setSymbolForecastedCumulant1(symbol,
						result.getDoubleArray("value"), result.getLongArray("time")));

				result = checkResult(portfolio
						.getMetric(metricStringbuilder.setMetric("POSITION_VARIANCE").setPosition(symbol).getJSON()));
				checkResult(setSymbolForecastedCumulant2(symbol,
						result.getDoubleArray("value"), result.getLongArray("time")));

				result = checkResult(portfolio
						.getMetric(metricStringbuilder.setMetric("POSITION_CUMULANT3").setPosition(symbol).getJSON()));
				checkResult(setSymbolForecastedCumulant3(symbol,
						result.getDoubleArray("value"), result.getLongArray("time")));

				result = checkResult(portfolio
						.getMetric(metricStringbuilder.setMetric("POSITION_CUMULANT4").setPosition(symbol).getJSON()));
				checkResult(setSymbolForecastedCumulant4(symbol,
						result.getDoubleArray("value"), result.getLongArray("time")));

			}


			result = checkResult(portfolio.getMetric(metricStringbuilder.setMetric("INDEX_VARIANCE").getJSON()));
			checkResult(setIndexForecastedCumulant2(result.getDoubleArray("value"),
					result.getLongArray("time")));

			result = checkResult(portfolio.getMetric(metricStringbuilder.setMetric("INDEX_CUMULANT3").getJSON()));
			checkResult(setIndexForecastedCumulant3(result.getDoubleArray("value"),
					result.getLongArray("time")));

			result = checkResult(portfolio.getMetric(metricStringbuilder.setMetric("INDEX_CUMULANT4").getJSON()));
			checkResult(setIndexForecastedCumulant4(result.getDoubleArray("value"),
					result.getLongArray("time")));

		} catch (Exception e) {
			if(portfolio.isDebug())
				Console.writeStackTrace(e);
			return new MethodResult(e.getMessage());
		}

		return new MethodResult();
	}
	
	/**
	 * 
	 * @param portfolio
	 * @param type "simple"|| "exp_smoothing"
	 * @return
	 */
	public MethodResult makeSimpleCumulantsForecast(Portfolio portfolio, String type) {

		if(type.equals("simple"))
			N=1;
		if(!(type.equals("simple")||type.equals("exp_smoothing")))
			return new MethodResult("Wrong type: " + type);
		
		for(String e:portfolio.getSymbolNamesList()){
			portfolio.setPositionQuantity(e, 1);
		} 
		portfolio.setPortfolioMetricsMode("price");
		portfolio.setSamplingInterval("1s");
		
		portfolio.startBatch();
		MetricStringBuilder metricStringbuilder = new MetricStringBuilder();
		try {
			MethodResult result;
			for (String symbol : symbolsName) {
 
				portfolio.addMetricToBatch(metricStringbuilder.setMetric("POSITION_BETA").setPosition(symbol).getJSON());

				portfolio.addMetricToBatch(metricStringbuilder.setMetric("POSITION_EXPECTED_RETURN").setPosition(symbol).getJSON());
				portfolio.addMetricToBatch(metricStringbuilder.setMetric("POSITION_VARIANCE").setPosition(symbol).getJSON());
				portfolio.addMetricToBatch(metricStringbuilder.setMetric("POSITION_CUMULANT3").setPosition(symbol).getJSON());
				portfolio.addMetricToBatch(metricStringbuilder.setMetric("POSITION_CUMULANT4").setPosition(symbol).getJSON());

			}

			//portfolio.addMetricToBatch(metricStringbuilder.setMetric("INDEX_EXPECTED_RETURN").getJSON());
			portfolio.addMetricToBatch(metricStringbuilder.setMetric("INDEX_VARIANCE").getJSON());
			portfolio.addMetricToBatch(metricStringbuilder.setMetric("INDEX_CUMULANT3").getJSON());
			portfolio.addMetricToBatch(metricStringbuilder.setMetric("INDEX_CUMULANT4").getJSON());

			portfolio.finishBatch();

			for (String symbol : symbolsName) {

				result = checkResult(portfolio
						.getMetric(metricStringbuilder.setMetric("POSITION_BETA").setPosition(symbol).getJSON()));
				checkResult(setSymbolForecastedBeta(symbol, forecasterExp( result.getDoubleArray("value"), N),
						result.getLongArray("time")));

				result = checkResult(portfolio
						.getMetric(metricStringbuilder.setMetric("POSITION_EXPECTED_RETURN").setPosition(symbol).getJSON()));
				checkResult(setSymbolForecastedCumulant1(symbol,
						forecasterExp(result.getDoubleArray("value"), N), result.getLongArray("time")));

				result = checkResult(portfolio
						.getMetric(metricStringbuilder.setMetric("POSITION_VARIANCE").setPosition(symbol).getJSON()));
				checkResult(setSymbolForecastedCumulant2(symbol,
						forecasterExp(result.getDoubleArray("value"), N), result.getLongArray("time")));

				result = checkResult(portfolio
						.getMetric(metricStringbuilder.setMetric("POSITION_CUMULANT3").setPosition(symbol).getJSON()));
				checkResult(setSymbolForecastedCumulant3(symbol,
						forecasterExp(result.getDoubleArray("value"), N), result.getLongArray("time")));

				result = checkResult(portfolio
						.getMetric(metricStringbuilder.setMetric("POSITION_CUMULANT4").setPosition(symbol).getJSON()));
				checkResult(setSymbolForecastedCumulant4(symbol,
						forecasterExp(result.getDoubleArray("value"), N), result.getLongArray("time")));

			}


			result = checkResult(portfolio.getMetric(metricStringbuilder.setMetric("INDEX_VARIANCE").getJSON()));
			checkResult(setIndexForecastedCumulant2(forecasterExp(result.getDoubleArray("value"), N),
					result.getLongArray("time")));

			result = checkResult(portfolio.getMetric(metricStringbuilder.setMetric("INDEX_CUMULANT3").getJSON()));
			checkResult(setIndexForecastedCumulant3(forecasterExp(result.getDoubleArray("value"), N),
					result.getLongArray("time")));

			result = checkResult(portfolio.getMetric(metricStringbuilder.setMetric("INDEX_CUMULANT4").getJSON()));
			checkResult(setIndexForecastedCumulant4(forecasterExp(result.getDoubleArray("value"), N),
					result.getLongArray("time")));

		} catch (Exception e) {
			if(portfolio.isDebug())
				Console.writeStackTrace(e);
			return new MethodResult(e.getMessage());
		}

		return new MethodResult();
	}

	
	private double[]  forecasterExp(double[] x, int N ){
		
		double alpha = 2.0/(1.0+N);
		double value=0;
		double[] a = new double[x.length];
		for(int i=0; i<x.length;i++){
			double beta=2.0/(2.0+i);
			
			beta = Math.max(beta, alpha);
			
			value=value*(1.0-beta) + beta*x[i];
			
			a[i]=value;
		}
		
		return a;			
	};
	

	private MethodResult checkResult(
			MethodResult result) throws Exception {

		if (result.hasError())
			throw new Exception(result.getErrorMessage());

		return result;

	}

	
	private int parseTimeInterval(String s, String where) throws Exception {

		String ERROR = String.format(MessageStrings.INCOR_PARAM_FORMAT,  where);

		String res[] = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

		String error = "";

		int number = 1;
		int scale = 0;
		if (res.length != 2) {
			error = ERROR;
			// 1s, 1m, 1d, 1w, 1mo, 1y
		} else {
			try {
				number = Integer.parseInt(res[0]);
			} catch (Exception e) {
				error = ERROR;
			}

			if (res[1].equals("s"))
				scale = 1;
			if (res[1].equals("m"))
				scale = 60;
			if (res[1].equals("h"))
				scale = 60 * 60;
			if (res[1].equals("d"))
				scale = 23400;
			if (res[1].equals("w"))
				scale = 23400 * 5;
			if (res[1].equals("mo"))
				scale = 23400 * 21;
			if (res[1].equals("y"))
				scale = 23400 * 256;

			if (scale == 0)
				error = ERROR;
		}

		if (error.length() != 0) {
			throw new Exception(error);
		}

		return number * scale;

	}

	public MethodResult addToPortfolio(Portfolio portfolio) {

		try {

			checkResult(isAllForecastedValuesPresent());
			checkResult(portfolio.addUserData("expTimeStep", timeStep,
					timeStepTimeMilliSec));

			checkResult(portfolio.addUserData("IndexVarince",
					forecastedIndexValue[1], forecastedIndexValueTime[1]));

			if (isIndexCumulantPresent[0])
				checkResult(portfolio.addUserData("IndexCumulant3",
						forecastedIndexValue[2], forecastedIndexValueTime[2]));
			else
				checkResult(portfolio.addUserData("IndexSkewness",
						forecastedIndexValue[2], forecastedIndexValueTime[2]));

			if (isIndexCumulantPresent[1])
				checkResult(portfolio.addUserData("IndexCumulant4",
						forecastedIndexValue[2], forecastedIndexValueTime[2]));
			else
				checkResult(portfolio.addUserData("IndexKurtosis",
						forecastedIndexValue[2], forecastedIndexValueTime[2]));

			for (int i = 0; i < symbolNamber; i++) {

				String symbol = symbolsName.get(i);

				checkResult(portfolio.addUserData(symbol + "ExpReturn",
						forecastedSymbolValue[i][0],
						forecastedSymbolValueTime[i][0]));

				checkResult(portfolio.addUserData(symbol + "Variance",
						forecastedSymbolValue[i][1],
						forecastedSymbolValueTime[i][1]));

				if (isSymbolCumulantPresent[i][0])
					checkResult(portfolio.addUserData(symbol + "Cumulant3",
							forecastedSymbolValue[i][2],
							forecastedSymbolValueTime[i][2]));
				else
					checkResult(portfolio.addUserData(symbol + "Skewness",
							forecastedSymbolValue[i][2],
							forecastedSymbolValueTime[i][2]));

				if (isSymbolCumulantPresent[i][1])
					checkResult(portfolio.addUserData(symbol + "Cumulant4",
							forecastedSymbolValue[i][2],
							forecastedSymbolValueTime[i][3]));
				else
					checkResult(portfolio.addUserData(symbol + "Kurtosis",
							forecastedSymbolValue[i][2],
							forecastedSymbolValueTime[i][3]));

				checkResult(portfolio.addUserData(symbol + "Beta",
						forecastedSymbolValue[i][4],
						forecastedSymbolValueTime[i][4]));
			}

		} catch (Exception e) {
			return new MethodResult(e.getMessage());
		}

		return new MethodResult();
	}

}
