package org.peerbox.app;


import org.peerbox.app.manager.file.IFileManager;
import org.peerbox.app.manager.node.INodeManager;
import org.peerbox.app.manager.user.IUserManager;
import org.peerbox.watchservice.ActionExecutor;
import org.peerbox.watchservice.FileEventManager;
import org.peerbox.watchservice.FolderWatchService;
import org.peerbox.watchservice.filetree.persistency.H2HUserProfilePersister;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class ClientContext {

	private Injector clientInjector;

	private INodeManager nodeManager;
	private IUserManager userManager;
	private IFileManager fileManager;

	private ActionExecutor actionExecutor;
	private FileEventManager fileEventManager;
	private FolderWatchService folderWatchService;
	private H2HUserProfilePersister remoteProfilePersister;

	public ClientContext() {

	}

	public Injector getInjector() {
		return clientInjector;
	}

	@Inject
	public void setInjector(Injector clientInjector) {
		this.clientInjector = clientInjector;
	}

	public ActionExecutor getActionExecutor() {
		return actionExecutor;
	}

	@Inject
	public void setActionExecutor(ActionExecutor actionExecutor) {
		this.actionExecutor = actionExecutor;
	}

	public FileEventManager getFileEventManager() {
		return fileEventManager;
	}

	@Inject
	public void setFileEventManager(FileEventManager fileEventManager) {
		this.fileEventManager = fileEventManager;
	}

	public FolderWatchService getFolderWatchService() {
		return folderWatchService;
	}

	@Inject
	public void setFolderWatchService(FolderWatchService folderWatchService) {
		this.folderWatchService = folderWatchService;
	}

	public INodeManager getNodeManager() {
		return nodeManager;
	}

	@Inject
	public void setNodeManager(INodeManager nodeManager) {
		this.nodeManager = nodeManager;
	}

	public IUserManager getUserManager() {
		return userManager;
	}

	@Inject
	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	public IFileManager getFileManager() {
		return fileManager;
	}

	@Inject
	public void getFileManager(IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	public H2HUserProfilePersister getRemoteProfilePersister() {
		return remoteProfilePersister;
	}

	@Inject
	public void setRemoteProfilePersister(H2HUserProfilePersister persister) {
		this.remoteProfilePersister = persister;
	}
}
