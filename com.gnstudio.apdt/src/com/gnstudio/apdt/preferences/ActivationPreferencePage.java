/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/

package com.gnstudio.apdt.preferences;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.gnstudio.apdt.APDTImages;
import org.gnstudio.apdt.APDTLog;

import com.gnstudio.apdt.APDTContext;
import com.gnstudio.apdt.APDTMessages;
import com.gnstudio.apdt.APDTPlugin;

public class ActivationPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public void init(IWorkbench workbench) {
		noDefaultAndApplyButton();
	}

	private Label lblInfo;
	private Text txtUserId;
	private Button btnActivation;
	private Text txtEmail;
	private Text txtSerial;

	private void validateActivationBtn() {
		btnActivation.setEnabled(txtUserId.getText().length() > 0
				&& txtSerial.getText().length() > 0
				&& txtEmail.getText().length() > 0);

	}

	private void refreshUI() {
		APDTContext apdtContext = APDTPlugin.getDefault().getApdtContext();

		String activeStatus = apdtContext.getAcivationStatus();
		lblInfo.setText(String.format(APDTMessages.ActivationPage_summary,
				APDTPlugin.VERSION, activeStatus));
		if (apdtContext.isActivated()) {
			txtUserId.setEditable(false);
			txtEmail.setEditable(false);
			txtSerial.setEditable(false);
			btnActivation.setVisible(false);
		} else {
			btnActivation.setVisible(true);
			validateActivationBtn();
		}

	}

	@Override
	protected Control createContents(Composite parent) {
		final APDTContext apdtContext = APDTPlugin.getDefault()
				.getApdtContext();
		Composite base = new Composite(parent, SWT.NULL);
		base.setLayout(new GridLayout());
		base.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite summary = new Composite(base, SWT.NULL);
		summary.setLayout(new GridLayout(2, false));
		summary.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label apdtIcon = new Label(summary, SWT.NONE);
		apdtIcon.setAlignment(SWT.RIGHT);
		lblInfo = new Label(summary, SWT.NONE);
		apdtIcon.setImage(APDTImages.getImage(APDTImages.DESC_APDT_ICON));

		new Label(summary, SWT.NONE);
		Link link = new Link(summary, SWT.NONE);
		link.setText("<A>http://www.modeling.io</A>");
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evet) {
				try {
					PlatformUI.getWorkbench().getBrowserSupport()
							.getExternalBrowser()
							.openURL(new URL("http://www.modeling.io"));
				} catch (Exception e) {
					APDTLog.log(e);
				}
			}
		});

		Group body = new Group(base, SWT.NULL);
		body.setText("Registration Information");
		body.setLayout(new GridLayout());
		body.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite descripterBody = new Composite(body, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 15;
		layout.marginLeft = 10;
		descripterBody.setLayout(layout);
		descripterBody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// user name
		Label lblUserId = new Label(descripterBody, SWT.NULL);
		lblUserId.setText("User Name:");
		txtUserId = new Text(descripterBody, SWT.SINGLE | SWT.BORDER);
		addControlLayoutData(txtUserId);
		txtUserId.setText(apdtContext.getUser());

		// email
		Label lblEmail = new Label(descripterBody, SWT.NULL);
		lblEmail.setText("Email:");
		txtEmail = new Text(descripterBody, SWT.SINGLE | SWT.BORDER);
		addControlLayoutData(txtEmail);
		txtEmail.setText(apdtContext.getUserEmail());

		// serial number
		Label lblSerial = new Label(descripterBody, SWT.NULL);
		GridData lgd = new GridData();
		lgd.verticalAlignment = SWT.BEGINNING;
		lblSerial.setLayoutData(lgd);
		lblSerial.setText("Serial Number:");
		txtSerial = new Text(descripterBody, SWT.MULTI | SWT.WRAP | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL);
		gd.widthHint = 20;
		gd.heightHint = 100;
		gd.horizontalSpan = 1;
		gd.horizontalIndent = 3;
		txtSerial.setLayoutData(gd);
		txtSerial.setText(apdtContext.getActivationKey());
		// activation button
		new Label(descripterBody, SWT.NULL);
		btnActivation = new Button(descripterBody, SWT.PUSH);
		GridData btnGD = new GridData(GridData.GRAB_HORIZONTAL);
		btnGD.widthHint = 120;
		btnActivation.setLayoutData(btnGD);
		btnActivation.setText("Activate");
		btnActivation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String userName = txtUserId.getText();
				String email = txtEmail.getText();
				String serial = txtSerial.getText();
				invokeActivation(userName, email, serial);

			}
		});
		ModifyListener validateListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				validateActivationBtn();

			}
		};
		txtUserId.addModifyListener(validateListener);
		txtSerial.addModifyListener(validateListener);
		txtEmail.addModifyListener(validateListener);
		refreshUI();
		return body;
	}

	private void addControlLayoutData(Control control) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 20;
		gd.horizontalSpan = 1;
		gd.horizontalIndent = 3;
		control.setLayoutData(gd);
	}

	public static String encode(String fingerprint, String tag) {
		String encode = "";
		int key = extractBitValue(fingerprint);
		char[] toCharArray = tag.toCharArray();
		for (char c : toCharArray) {
			encode += (char) (key ^ c);
		}
		return encode;
	}

	public static String decode(String fingerprint, String encodedTag) {
		String decode = "";
		int key = extractBitValue(fingerprint);
		char[] toCharArray = encodedTag.toCharArray();
		for (char c : toCharArray) {
			decode += (char) (c ^ key);
		}
		return decode;
	}

	private static int extractBitValue(String username) {
		int i = 0;
		char[] toCharArray = username.toCharArray();
		for (char c : toCharArray) {
			i += c;
		}
		return i;

	}

	private void invokeActivation(final String user, final String email,
			final String serial) {
		final IRunnableWithProgress activation = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) {
				try {
					monitor.beginTask("APDT: Licence Activation", 2);
					monitor.worked(1);
					APDTContext apdtContext = APDTPlugin.getDefault()
							.getApdtContext();
					apdtContext.activate(user, email, serial);
					monitor.worked(2);
				} finally {
					monitor.done();
					APDTPlugin.getStandardDisplay().asyncExec(new Runnable() {

						public void run() {
							refreshUI();
						}
					});
				}

			}
		};
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		try {
			dialog.run(true, true, activation);
		} catch (InvocationTargetException e) {
			APDTLog.log(e);
		} catch (InterruptedException e) {
			APDTLog.log(e);
		}

	}
}
