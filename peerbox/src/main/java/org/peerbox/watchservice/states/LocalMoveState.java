package org.peerbox.watchservice.states;

import java.nio.file.Path;

import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.peerbox.FileManager;
import org.peerbox.watchservice.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * if a move or renaming (which actually is a move at the same path location) occurs,
 * this move state will be assigned. The transition to another state except the delete state
 * will not be accepted.
 * 
 * @author winzenried
 *
 */
public class LocalMoveState extends AbstractActionState {

	private final static Logger logger = LoggerFactory.getLogger(LocalMoveState.class);

	private Path sourcePath;
	private boolean reversePaths;

	public LocalMoveState(Action action, Path sourcePath) {
		super(action);
		this.sourcePath = sourcePath;
		reversePaths = false;
	}
	
	public LocalMoveState(Action action, Path sourcePath, boolean reversePaths) {
		super(action);
		this.sourcePath = sourcePath;
		this.reversePaths = reversePaths;
	}


	public Path getSourcePath() {
		return sourcePath;
	}

	@Override
	public AbstractActionState handleLocalCreateEvent() {
		logger.debug("Local Create Event: not defined");
		throw new IllegalStateException("Local Create Event: not defined");
	}

	@Override
	public AbstractActionState handleLocalDeleteEvent() {
		logger.debug("Local Delete Event: not defined");
		throw new IllegalStateException("Local Delete Event: not defined");
	}

	@Override
	public AbstractActionState handleLocalUpdateEvent() {
		logger.debug("Local Update Event: not defined");
		throw new IllegalStateException("Local Update Event: not defined");
	}

	@Override
	public AbstractActionState handleLocalMoveEvent(Path oldFilePath, boolean isReversed) {
		logger.debug("Local Move Event: not defined");
		throw new IllegalStateException("Local Move Event: not defined");
	}

	@Override
	public AbstractActionState handleRemoteCreateEvent() {
		logger.debug("Remote Create Event: Local Move -> Conflict");
		return new ConflictState(action);
	}

	@Override
	public AbstractActionState handleRemoteDeleteEvent() {
		logger.debug("Remote Delete Event: Local Move -> Conflict");
		return new ConflictState(action);
	}

	@Override
	public AbstractActionState handleRemoteUpdateEvent() {
		logger.debug("Remote Update Event: Local Move -> Conflict");
		return new ConflictState(action);
	}

	@Override
	public AbstractActionState handleRemoteMoveEvent(Path oldFilePath) {
		logger.debug("Remote Move Event: Local Move -> Conflict");
		return new ConflictState(action);
	}

	@Override
	public void execute(FileManager fileManager) throws NoSessionException,
			NoPeerConnectionException {
		
		if(reversePaths){
			fileManager.move(action.getFilePath().toFile(), sourcePath.toFile());
			logger.debug("Task \"Move File\" executed from: " + action.getFilePath().toFile().toPath() + " to " + sourcePath.toString());
		} else {
			fileManager.move(sourcePath.toFile(), action.getFilePath().toFile());
			logger.debug("Task \"Move File\" executed from: " + sourcePath.toString() + " to " + action.getFilePath().toFile().toPath());
		}
		
	}
}
