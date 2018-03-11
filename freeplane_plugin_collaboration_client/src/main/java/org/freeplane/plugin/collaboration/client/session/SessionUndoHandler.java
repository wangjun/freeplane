package org.freeplane.plugin.collaboration.client.session;

import javax.swing.event.ChangeListener;

import org.freeplane.core.undo.IActor;
import org.freeplane.core.undo.IUndoHandler;
import org.freeplane.features.map.MapModel;
import org.freeplane.plugin.collaboration.client.event.batch.Updates;

public class SessionUndoHandler implements IUndoHandler{
	
	public void setChangeEventSource(IUndoHandler source) {
		delegate.setChangeEventSource(source);
	}

	private IUndoHandler delegate;
	private final MapModel map;

	public SessionUndoHandler(MapModel map) {
		this.map = map;
		this.delegate = map.removeExtension(IUndoHandler.class);
		delegate.setChangeEventSource(this);
		map.addExtension(IUndoHandler.class, this);
	}
	
	public void removeFromMap() {
		if(map == null)
			throw new IllegalStateException();
		final IUndoHandler currentHandler = map.removeExtension(IUndoHandler.class);
		if (this == currentHandler) {
			delegate.setChangeEventSource(delegate);
			map.addExtension(IUndoHandler.class, delegate);
		}
		else {
			if(currentHandler != null)
				map.addExtension(currentHandler);
			throw new IllegalStateException();
		}
	}
	
	

	public boolean controlsCurrentMap() {
		return delegate.controlsCurrentMap();
	}

	public void addActor(IActor actor) {
		if(getTransactionLevel() == 0)
			startTransaction();
		delegate.addActor(actor);
	}

	public boolean canRedo() {
		return getTransactionLevel() == 0 && delegate.canRedo();
	}

	public boolean canUndo() {
		return getTransactionLevel() == 0 && delegate.canUndo();
	}

	public void addChangeListener(ChangeListener listener) {
		delegate.addChangeListener(listener);
	}

	public void removeChangeListener(ChangeListener listener) {
		delegate.removeChangeListener(listener);
	}

	public void commit() {
		delegate.commit();
	}

	public String getLastDescription() {
		return delegate.getLastDescription();
	}

	public boolean isUndoActionRunning() {
		return delegate.isUndoActionRunning();
	}

	public void redo() {
		flush();
		delegate.redo();
		flush();
	}

	public void resetRedo() {
		delegate.resetRedo();
	}

	public void rollback() {
		delegate.rollback();
	}

	public void startTransaction() {
		delegate.startTransaction();
	}

	public void forceNewTransaction() {
		delegate.forceNewTransaction();
	}

	public void undo() {
			flush();
		delegate.undo();
			flush();
	}

	private void flush() {
		if(map == null || getTransactionLevel() != 1)
			return;
		Updates updates = map.getExtension(Updates.class);
		if(updates == null)
			return;
		updates.flush();
	}

	public void deactivate() {
		delegate.deactivate();
	}

	public void delayedCommit() {
		delegate.delayedCommit();
	}

	public void delayedRollback() {
		delegate.delayedRollback();
	}

	public int getTransactionLevel() {
		return delegate.getTransactionLevel();
	}
	
	
}