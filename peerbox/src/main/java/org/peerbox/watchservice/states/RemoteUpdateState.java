package org.peerbox.watchservice.states;

import java.nio.file.Path;

import org.hive2hive.core.exceptions.IllegalFileLocation;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.peerbox.FileManager;
import org.peerbox.watchservice.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteUpdateState extends AbstractActionState {

	private final static Logger logger = LoggerFactory.getLogger(RemoteUpdateState.class);

	public RemoteUpdateState(Action action) {
		super(action);
	}

	@Override
	public AbstractActionState handleLocalCreateEvent() {
		logger.debug("Local Create Event in RemoteUpdateState!  ({})", action.getFilePath());
		return new InitialState(action);
	}

	@Override
	public AbstractActionState handleLocalUpdateEvent() {
		logger.debug("Local Update Event:  ({})", action.getFilePath());
		
		return this;
	}

	@Override
	public AbstractActionState handleLocalDeleteEvent() {
		logger.debug("Local Delete Event in RemoteUpdateState ({})", action.getFilePath());
		return this;
	}

	@Override
	public AbstractActionState handleLocalMoveEvent(Path oldFilePath) {
		logger.debug("Local Move Event:  ({})", action.getFilePath());
		return this;
	}

	@Override
	public AbstractActionState handleRemoteUpdateEvent() {
		logger.debug("Remote Update Event:  ({})", action.getFilePath());
		return this;
	}

	@Override
	public AbstractActionState handleRemoteDeleteEvent() {
		logger.debug("Remote Delete Event:  ({})", action.getFilePath());
		return this;
	}

	@Override
	public AbstractActionState handleRemoteMoveEvent(Path oldFilePath) {
		logger.debug("Remote Move Event:  ({})", action.getFilePath());
		return this;
	}

	@Override
	public void execute(FileManager fileManager) throws NoSessionException,
			NoPeerConnectionException, IllegalFileLocation, InvalidProcessStateException {
		Path path = action.getFilePath();
		logger.debug("Execute REMOTE UPDATE, download the file: {}", path);
		IProcessComponent process = fileManager.download(path.toFile());
		if(process != null){
			process.attachListener(new FileManagerProcessListener());
		} else {
			System.err.println("process is null");
		}
		
		notifyActionExecuteSucceeded();
	}

	@Override
	public AbstractActionState handleRecoverEvent(int versionToRecover) {
		// TODO Auto-generated method stub
		return null;
	}

}
