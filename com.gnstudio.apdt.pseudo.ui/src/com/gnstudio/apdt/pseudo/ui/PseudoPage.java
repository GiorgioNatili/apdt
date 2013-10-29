/*******************************************************************************
 * Copyright (c) 2010, 2012 GNstudio s.r.l. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.html
 *
 * Contributors:
 *   GNstudio s.r.l. - initial API and implementation
 *******************************************************************************/
package com.gnstudio.apdt.pseudo.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextPrintOptions;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.gnstudio.apdt.APDTLog;
import org.gnstudio.apdt.APDTUIConstants;
import org.gnstudio.apdt.model.editor.APDEditorImages;
import org.gnstudio.apdt.model.editor.APDEditorPlugin;
import org.gnstudio.apdt.model.editor.APDModelEditor;
import org.gnstudio.apdt.model.editor.AbstractEditorPage;
import org.gnstudio.apdt.model.editor.handlers.PageActionHandler;
import org.gnstudio.apdt.model.editor.handlers.PageActionHandlerProvider;

import com.gnstudio.apdt.APDTPlugin;
import com.gnstudio.apdt.pseudo.core.PseudoGenerator;
import com.gnstudio.apdt.pseudo.core.PseudoKeywords;
import com.gnstudio.apdt.pseudo.ui.wizard.PseudoExportWizard;

public class PseudoPage extends AbstractEditorPage implements
		PageActionHandlerProvider {
	public static final String PAGE_ID = "pseudo-code-view"; //$NON-NLS-1$
	private StyledText pseudoText;
	private final Job codeGenJob;
	private final Job copyGenJob;
	private final Clipboard clipboard = new Clipboard(
			APDEditorPlugin.getStandardDisplay());

	public PseudoPage(final APDModelEditor editor) {
		super(editor, PAGE_ID, APDPseudoMessages.PseudoPage_title);
		codeGenJob = new GenJob(APDPseudoMessages.PseudoPage_job_pseudo_gen) {
			@Override
			public void setText(String text) {
				pseudoText.setText(text);
			}
		};

		copyGenJob = new GenJob(APDPseudoMessages.PseudoPage_job_pseudo_gen,
				true) {
			@Override
			public void setText(final String text) {
				clipboard.setContents(new Object[] { text },
						new Transfer[] { TextTransfer.getInstance() },
						DND.CLIPBOARD);
			}
		};
		copyGenJob.setUser(true);
	}

	@Override
	protected void buildBody(IManagedForm managedForm, FormToolkit toolkit) {
		Composite body = managedForm.getForm().getBody();
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		body.setLayout(gridLayout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH
				| GridData.GRAB_VERTICAL));
		CompositeRuler verticalRuler = new CompositeRuler(12);
		int styles = SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION;
		SourceViewer sourceViewer = new SourceViewer(body, verticalRuler,
				styles);
		sourceViewer.getControl().setLayoutData(
				new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL));
		Document document = new Document();
		sourceViewer.setDocument(document);
		pseudoText = sourceViewer.getTextWidget();

		Font blockFont = JFaceResources.getFont(JFaceResources.TEXT_FONT);
		if (blockFont != null) {
			pseudoText.setFont(blockFont);
		}
		verticalRuler.addDecorator(0, new LineNumberRulerColumn());
		pseudoText.setEditable(false);

		connectContextMenu();
		connectStyle();
	}

	@Override
	protected String getPageHeader() {
		return APDPseudoMessages.PseudoPage_header;
	}

	@Override
	protected Image getPageHeaderImage() {
		return APDEditorImages.getImage(APDEditorImages.DESC_MODEL_OVERVIEW);
	}

	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		if (active) {
			pseudoText.setText(APDPseudoMessages.PseudoPage_info_pseudo_gen);
			codeGenJob.schedule();
		}
	}

	private void connectContextMenu() {

		final MenuManager popupMenuManager = new MenuManager();
		IMenuListener listener = new IMenuListener() {
			public void menuAboutToShow(IMenuManager mng) {
				getEditor().getContributor().addCopyAction(popupMenuManager);
				popupMenuManager.add(createCopySelectedAction());
				popupMenuManager.add(createExportAction());
				popupMenuManager.add(new Separator());
				getEditor().getContributor().addPrintAction(popupMenuManager);
			}
		};
		popupMenuManager.addMenuListener(listener);
		popupMenuManager.setRemoveAllWhenShown(true);
		Control control = pseudoText;
		Menu menu = popupMenuManager.createContextMenu(control);
		control.setMenu(menu);

	}

	private void connectStyle() {
		// TODO change this

		final Color BLUE = APDEditorPlugin.getStandardDisplay().getSystemColor(
				SWT.COLOR_BLUE);

		pseudoText.addLineStyleListener(new LineStyleListener() {
			public void lineGetStyle(LineStyleEvent event) {

				String line = event.lineText;
				List<StyleRange> list = new LinkedList<StyleRange>();
				String[] split = line.split(" ");
				for (String word : split) {
					word = word.trim();
					if (word.length() > 0 && PseudoKeywords.iskeyWord(word)) {
						list.add(getHighlightStyle(
								event.lineOffset + line.indexOf(word),
								word.length()));
					}
				}
				// for (String keyword : keywords) {
				// int cursor = -1;
				//
				// while ((cursor = line.indexOf(keyword, cursor + 1)) >= 0) {
				// list.add(getHighlightStyle(event.lineOffset + cursor,
				// keyword.length()));
				// }
				// }

				event.styles = list.toArray(new StyleRange[list.size()]);
			}

			private StyleRange getHighlightStyle(int startOffset, int length) {
				StyleRange styleRange = new StyleRange();
				styleRange.start = startOffset;
				styleRange.length = length;
				styleRange.foreground = BLUE;
				return styleRange;
			}
		}

		);
	}

	public boolean isHandlerActive(String commandId) {
		return true;
	}

	public PageActionHandler getActionHandler(String commandId) {
		if (ActionFactory.CUT.getCommandId().equals(commandId)
				|| ActionFactory.PASTE.getCommandId().equals(commandId)) {
			return new PageActionHandler() {

				public boolean isEnable() {
					return false;
				}

				public void excecute() {
					// disable
				}
			};
		} else if (ActionFactory.PRINT.getCommandId().equals(commandId)) {
			return new PageActionHandler() {

				public boolean isEnable() {
					// TODO validate pro feature
					return true;
				}

				public void excecute() {
					PrintDialog printDialog = new PrintDialog(getSite()
							.getShell(), SWT.NONE);
					printDialog.setText("Print");
					PrinterData printerData = printDialog.open();
					if ((printerData == null))
						return;

					Printer p = new Printer(printerData);
					String userName = APDEditorPlugin
							.getDefault()
							.getPreferenceStore()
							.getString(
									APDTUIConstants.PREFERENCES_MODEL_DISCUSSION_AUTHOR);
					StyledTextPrintOptions options = new StyledTextPrintOptions();

					options.header = new StringBuilder()
							.append("[")
							.append(DateFormat.getDateInstance().format(
									new Date())).append("] ").append(userName)
							.append(StyledTextPrintOptions.SEPARATOR)
							.toString();
					options.footer = new StringBuilder()
							.append(StyledTextPrintOptions.SEPARATOR)
							.append("Page ")
							.append(StyledTextPrintOptions.PAGE_TAG)
							.append(StyledTextPrintOptions.SEPARATOR)
							.toString();

					options.printTextFontStyle = true;
					options.printTextBackground = true;
					options.printTextForeground = true;
					options.printLineBackground = true;
					// options.printLineNumbers = true;

					pseudoText.print(p, options).run();
				}

			};
		} else if (ActionFactory.COPY.getCommandId().equals(commandId)) {
			return new PageActionHandler() {

				public boolean isEnable() {
					return true;
				}

				public void excecute() {

					if (APDTPlugin.getDefault().getApdtContext()
							.validateActivation()) {
						Clipboard clipboard = new Clipboard(
								APDEditorPlugin.getStandardDisplay());
						if (pseudoText.getSelectionText().length() > 0) {
							clipboard
									.setContents(new Object[] { pseudoText
											.getSelectionText() },
											new Transfer[] { TextTransfer
													.getInstance() },
											DND.CLIPBOARD);
						} else {
							copyGenJob.schedule();
						}
					}
				}
			};
		}

		return null;
	}

	@Override
	protected Action[] getPageToolbarActions(ScrolledForm form,
			FormToolkit toolkit) {
		Action refresh = new Action(APDPseudoMessages.PseudoPage_refresh,
				APDEditorImages.DESC_REFRESH) {
			@Override
			public void run() {
				pseudoText
						.setText(APDPseudoMessages.PseudoPage_info_pseudo_gen);
				codeGenJob.schedule();
			}

		};
		Action export = createExportAction();
		return new Action[] { export, refresh };
	}

	private Action createExportAction() {
		return new Action(APDPseudoMessages.PseudoPage_export,
				APDPseudoImages.DESC_EXPORT_PSEUDO) {
			@Override
			public void run() {

				if (APDTPlugin.getDefault().getApdtContext()
						.validateActivation()) {
					PseudoExportWizard wizard = new PseudoExportWizard();
					wizard.init(getEditor());
					WizardDialog dialog = new WizardDialog(
							getSite().getShell(), wizard);
					dialog.create();
					dialog.open();
				}
			}

		};
	}

	private Action createCopySelectedAction() {
		return new Action(APDPseudoMessages.PseudoPage_copy_selected) {
			@Override
			public void run() {

				if (APDTPlugin.getDefault().getApdtContext()
						.validateActivation()) {
					clipboard.setContents(
							new Object[] { pseudoText.getSelectionText() },
							new Transfer[] { TextTransfer.getInstance() },
							DND.CLIPBOARD);
				}

			}

			@Override
			public boolean isEnabled() {
				return (pseudoText.getSelectionText().length() > 0);
			}
		};
	}

	class GenJob extends Job {
		boolean keywordCase;

		public GenJob(String name) {
			super(name);
		}

		public GenJob(String name, boolean keywordCase) {
			super(name);
			this.keywordCase = keywordCase;
		}

		protected IStatus run(IProgressMonitor monitor) {
			File pesudoFile = null;
			try {
				pesudoFile = File.createTempFile("pseudo-out", "");//$NON-NLS-1$

				PseudoGenerator pseudoGenerator = new PseudoGenerator(
						PseudoPage.this.getEditor().getModel(), pesudoFile,
						keywordCase);

				pseudoGenerator.doGenerate(BasicMonitor.toMonitor(monitor));

				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				FileInputStream stream = new FileInputStream(pesudoFile);
				try {
					FileChannel fc = stream.getChannel();
					final MappedByteBuffer bb = fc.map(
							FileChannel.MapMode.READ_ONLY, 0, fc.size());
					/* Instead of using default, pass in a decoder. */
					APDEditorPlugin.getStandardDisplay().asyncExec(
							new Runnable() {

								public void run() {
									setText(Charset.defaultCharset().decode(bb)
											.toString());

								}
							});

				} finally {
					stream.close();
				}
			} catch (IOException e) {
				APDTLog.logException(e);
				return Status.CANCEL_STATUS;
			} finally {
				if (pesudoFile != null) {
					boolean delete = pesudoFile.delete();
					if (!delete)
						pesudoFile.deleteOnExit();
				}
			}
			// Don't return any problems because we don't want an error
			// dialog
			return Status.OK_STATUS;
		}

		public void setText(String text) {
		}
	}

}
