package com.gnstudio.apdt.snippet.export;

import org.gnstudio.apdt.model.editor.snippets.SnippetHandle;
import org.gnstudio.apdt.model.editor.snippets.SnippetProvider;

import com.gnstudio.apdt.snippet.LocalSnippetProvider;

public class SnippetSelectionItem {
	private final SnippetHandle handle;
	private boolean selected = true;

	public SnippetSelectionItem(SnippetHandle handle) {
		this.handle = handle;
	}

	public SnippetHandle getHandle() {
		return handle;
	}

	@Override
	public String toString() {
		return handle.toString();
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public static SnippetSelectionItem[] getSelectionItems(
			SnippetProvider provider) {
		if (provider instanceof LocalSnippetProvider) {
			SnippetHandle[] snippets = provider.getSnippets();
			SnippetSelectionItem[] items = new SnippetSelectionItem[snippets.length];

			for (int i = 0; i < snippets.length; i++) {
				items[i] = new SnippetSelectionItem(snippets[i]);
			}
			return items;
		}
		return new SnippetSelectionItem[0];
	}
}
