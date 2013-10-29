/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt;

import java.net.URL;
import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.gnstudio.apdt.APDTLog;
import org.json.JSONException;
import org.json.JSONObject;

import com.gnstudio.apdt.service.APDTService;

public class APDTContext {

	private static final String PREFERENCES_USERNAME = "com.gnstudio.apdt.pref.username";
	private static final String PREFERENCES_EMAIL = "com.gnstudio.apdt.pref.email";
	private static final String PREFERENCES_KEY = "com.gnstudio.apdt.pref.key";
	private static final String PREFERENCES_VALIDATED_DT = "com.gnstudio.apdt.pref.vdt";
	private static final String PREFERENCES_ACT = "com.gnstudio.apdt.pref.act";
	private static final String PREFERENCES_TRIAL_DAYS = "com.gnstudio.apdt.pref.trld";

	private static final String KEY_ACT = new String(new byte[] { 97, 112, 100,
			116, 95, 97, 99, 116, 95, 108, 105, 118, 101 });
	private static final String KEY_DACT = new String(new byte[] { 105, 118,
			101 });

	private final static String IS_NUMBER_REGEX = "((-|\\+)?[0-9]+(\\.[0-9]+)?)+";
	//
	private boolean activated = false;

	private int trialRemDays = 0;

	APDTContext() {
	}

	void bootup() {
		final long DAY = 86400000;
		activated = false;
		trialRemDays = 0;

		
		String valdatedDt = APDTPref.getDecrypt(PREFERENCES_VALIDATED_DT, "");
		String decryptAct = APDTPref.getDecrypt(PREFERENCES_ACT, null);
		if (KEY_ACT.equals(decryptAct)) {
			 activated = true;
		}

		if (valdatedDt.matches(IS_NUMBER_REGEX)) {
			long validated_dt = Long.parseLong(valdatedDt);
			long today = new Date().getTime();
			if (activated) {
				// check is valid activation
				// if last check day is more than 120 days old revalidate
				if (today < validated_dt
						|| ((today - validated_dt) / DAY) > 120) {
					VerificationJob job = new VerificationJob();
					job.setSystem(true);
					job.schedule();
				}
			} else {
				if (today < validated_dt || ((today - validated_dt) / DAY) > 30) {
					TrialTestJob job = new TrialTestJob();
					job.setSystem(true);
					job.schedule();
				} else {
					String trialDays = APDTPref.getDecrypt(
							PREFERENCES_TRIAL_DAYS, "");
					int days = 0;
					if (trialDays.matches(IS_NUMBER_REGEX))
						days = Integer.parseInt(trialDays);

					trialRemDays = (int) (((validated_dt + (days * DAY) - today)) / DAY);
					if (!(trialRemDays > 0)) {
						TrialTestJob job = new TrialTestJob();
						job.setSystem(true);
						job.schedule();
					}
				}
			}
		} else {
			if (activated) {
				// check is valid activation
				// revalidate
				VerificationJob job = new VerificationJob();
				job.setSystem(true);
				job.schedule();
			} else {
				// run validate
				TrialTestJob job = new TrialTestJob();
				job.setSystem(true);
				job.schedule();
			}
		}

	}

	public boolean isActivated() {
		return activated;
	}

	public boolean isTrial() {
		return trialRemDays > 0;
	}

	public boolean validateActivation() {
		if (!isActivated() && !isTrial()) {
			final Display display = APDTPlugin.getStandardDisplay();
			display.asyncExec(new Runnable() {

				public void run() {
					Shell shell = display.getActiveShell();
					boolean activateNow = MessageDialog
							.openQuestion(shell, "PDT Pro  feature",
									"PDT Pro not activated, Do you want to activate now ?");
					if (activateNow) {
						PreferenceDialog preferenceDialog = PreferencesUtil
								.createPreferenceDialogOn(shell,
										"com.gnstudio.apdt.activation.page",
										null, null);
						preferenceDialog.open();
					}
				}
			});
			return false;
		}
		return true;
	}

	public String getAcivationStatus() {
		if (activated)
			return "Activated";
		if (trialRemDays > 0)
			return "Trial Period -[" + trialRemDays + " days remaining]";

		return "Trial Period Expired";
	}

	public void activate(String user, String email, String serial) {
		APDTPref.put(PREFERENCES_USERNAME, user);
		APDTPref.put(PREFERENCES_EMAIL, email);
		APDTPref.putEncrypt(PREFERENCES_KEY, serial);

		String fpIds = APDTService.getFPIds();
		if (fpIds != null) {
			APDTPref.putEncrypt(PREFERENCES_ACT, KEY_DACT);

			final JSONObject jsonObject = APDTService.insertLicense(user,
					email, serial, fpIds, APDTPlugin.VERSION);

			if (jsonObject != null && jsonObject.has("#data")) {
				try {
					final boolean data = jsonObject.getBoolean("#data");
					if (data == false) {
						APDTPref.putEncrypt(PREFERENCES_ACT, KEY_DACT);
					} else
						APDTPref.putEncrypt(PREFERENCES_ACT, KEY_ACT);

				} catch (JSONException e) {
					APDTPref.putEncrypt(PREFERENCES_ACT, KEY_DACT);
					final Display display = APDTPlugin.getStandardDisplay();
					display.asyncExec(new Runnable() {

						public void run() {
							Shell shell = display.getActiveShell();
							try {
								String nl = System.getProperty("line.separator");
								String error = jsonObject.getString(("#data"))+
								nl+nl+"If you have still problem" +
										" please login into the web site and open a ticket using the following link" +
										" <a>http://www.modeling.io/node/add/to-do</a>," +
										" we will reply in the next business day.";		

								
								
								 MessageDialog dialog = new MessageDialog(shell, //
										 "PDT Pro activation error", //
								            null, error, MessageDialog.ERROR, //
								            new String[] {IDialogConstants.OK_LABEL}, //
								            0) {
								          protected Control createMessageArea(Composite composite) {
								            Image image = getImage();
								            if(image != null) {
								              imageLabel = new Label(composite, SWT.NULL);
								              image.setBackground(imageLabel.getBackground());
								              imageLabel.setImage(image);
								              GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(imageLabel);
								            }

								            Link link = new Link(composite, getMessageLabelStyle());
								            link.setText(message);
								            link.addSelectionListener(new SelectionAdapter() {
								              public void widgetSelected(SelectionEvent e) {
								            	  try {
													PlatformUI.getWorkbench().getBrowserSupport()
														.getExternalBrowser()
														.openURL(new URL("http://www.modeling.io/node/add/to-do"));
								            	  } catch (Exception ex) {
								  					APDTLog.log(ex);
								  				}
								              }
								            });

								            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
								                .hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
								                .applyTo(link);

								            return composite;
								          }
								        };
								        dialog.open();
							} catch (JSONException ex) {
								APDTLog.log(ex);
							}
						}
					});

				} finally {
					bootup();
				}
			}

			// reboot
			bootup();
		}
	}

	public String getUser() {
		return APDTPref.get(PREFERENCES_USERNAME, "");
	}

	public String getUserEmail() {
		return APDTPref.get(PREFERENCES_EMAIL, "");
	}

	public String getActivationKey() {
		return APDTPref.getDecrypt(PREFERENCES_KEY, "");
	}

	private class VerificationJob extends Job {

		public VerificationJob() {
			super("APDT: Licence Verification ");

		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			String fpIds = APDTService.getFPIds();
			if (fpIds != null) {
				// reset trial related fields
				APDTPref.putEncrypt(PREFERENCES_VALIDATED_DT,
						String.valueOf(new Date().getTime()));
				JSONObject jsonObject = APDTService.licenceVerification(getUser(),
						APDTPref.getDecrypt(PREFERENCES_KEY, ""), fpIds,
						APDTPlugin.VERSION);

				if (jsonObject != null && jsonObject.has("#data")) {
					try {
						final boolean data = jsonObject.getBoolean("#data");
						if (data == false) {
							APDTPref.putEncrypt(PREFERENCES_ACT, KEY_DACT);
						} else
							APDTPref.putEncrypt(PREFERENCES_ACT, KEY_ACT);

					} catch (JSONException e) {
						APDTPref.putEncrypt(PREFERENCES_ACT, KEY_DACT);
						APDTLog.log(e);
					} finally {
						bootup();
					}
				}

			}
			return Status.OK_STATUS;
		}
	}

	private class TrialTestJob extends Job {

		public TrialTestJob() {
			super("APDT: validating trial");

		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			String fpIds = APDTService.getFPIds();
			if (fpIds != null) {
				// reset trial related fields
				APDTPref.putEncrypt(PREFERENCES_VALIDATED_DT,
						String.valueOf(new Date().getTime()));
				APDTPref.putEncrypt(PREFERENCES_TRIAL_DAYS, "0");
				trialRemDays = 0;

				JSONObject jsonObject = APDTService.firstRun(fpIds,
						APDTPlugin.VERSION);

				if (jsonObject != null && jsonObject.has("#data")) {
					try {
						String data = jsonObject.getString("#data");
						if (data.matches(IS_NUMBER_REGEX)) {
							APDTPref.putEncrypt(PREFERENCES_TRIAL_DAYS, data);
							bootup();
						} else {
							// already notified server with mac call
							// trial_expite to see still on trial
							jsonObject = APDTService.trialExpire(fpIds,
									APDTPlugin.VERSION);
							data = jsonObject.getString("#data");
							if (jsonObject != null && jsonObject.has("#data")) {

								if (data.matches(IS_NUMBER_REGEX)) {
									APDTPref.putEncrypt(PREFERENCES_TRIAL_DAYS,
											data);
									bootup();
								} else {
									APDTLog.logWarnningMessage(data);
								}
							}
						}
					} catch (JSONException e) {
						APDTLog.log(e);
					}
				}

			}
			return Status.OK_STATUS;
		}
	}
}
