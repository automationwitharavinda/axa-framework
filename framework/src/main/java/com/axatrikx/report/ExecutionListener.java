/**
 * Licensed to Axatrikx under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Axatrikx licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.axatrikx.report;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.axatrikx.executor.ExecutionController;
import com.axatrikx.webdriver.WebDriverFactory;

/**
 * @author amalbose
 *
 */
public class ExecutionListener extends TestListenerAdapter {

	private IExecutionReporter reporter;

	public ExecutionListener(IExecutionReporter reporter) {
		this.reporter = reporter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.TestListenerAdapter#onStart(org.testng.ITestContext)
	 */
	@Override
	public void onStart(ITestContext testContext) {
		super.onStart(testContext);
		System.out.println("Starting Test " + testContext.getCurrentXmlTest().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.TestListenerAdapter#onFinish(org.testng.ITestContext)
	 */
	@Override
	public void onFinish(ITestContext testContext) {
		super.onFinish(testContext);
		reporter.finish();
		System.out.println("Result folder " + new File(testContext.getOutputDirectory()).getParent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.TestListenerAdapter#onTestStart(org.testng.ITestResult)
	 */
	@Override
	public void onTestStart(ITestResult result) {
		reporter.startTest(result.getName(), "");
		for (String group : result.getMethod().getGroups())
			reporter.assignCategory(group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.TestListenerAdapter#onTestSuccess(org.testng.ITestResult)
	 */
	@Override
	public void onTestSuccess(ITestResult tr) {
		super.onTestSuccess(tr);
		reporter.log("Result", "Test Pass", ExecutionStatus.PASS);
		reporter.endTest();
		System.out.println(tr.getName() + " Passed");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.TestListenerAdapter#onTestFailure(org.testng.ITestResult)
	 */
	@Override
	public void onTestFailure(ITestResult tr) {
		super.onTestFailure(tr);
		WebDriver driver = WebDriverFactory.getInstance().getWebDriver();
		if (!driver.toString().contains("(null)")) {
			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			String screenShotPath = null;
			try {
				String screenShotFileName = tr.getName() + ".png";
				screenShotPath = ExecutionController.getController().getExecutionFolder() + "/" + screenShotFileName;
				FileUtils.copyFile(scrFile, new File(screenShotPath));
				screenShotPath = screenShotFileName;
			} catch (IOException e) {
				screenShotPath = null;
			}
			reporter.log("Result", "Test Failed", ExecutionStatus.FAIL, tr.getThrowable(), screenShotPath);
		} else {
			reporter.log("Result", "Test Failed", ExecutionStatus.FAIL, tr.getThrowable());
		}
		reporter.endTest();
		System.out.println(tr.getName() + " Failed");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.TestListenerAdapter#onTestSkipped(org.testng.ITestResult)
	 */
	@Override
	public void onTestSkipped(ITestResult tr) {
		// TODO Auto-generated method stub
		super.onTestSkipped(tr);
		reporter.log("Result", "Test Skipped", ExecutionStatus.SKIP);
		reporter.endTest();
		System.out.println(tr.getName() + " Skipped");
	}

	/**
	 * Returns the reporter.
	 * 
	 * @return
	 */
	public IExecutionReporter getReporter() {
		return this.reporter;
	}

}
