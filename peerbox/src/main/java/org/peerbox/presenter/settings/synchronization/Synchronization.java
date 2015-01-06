package org.peerbox.presenter.settings.synchronization;

import java.net.URL;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.core.processes.files.list.FileNode;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;

import org.peerbox.FileManager;
import org.peerbox.IPeerboxFileManager;
import org.peerbox.IUserConfig;
import org.peerbox.UserConfig;
import org.peerbox.watchservice.FileEventManager;
import org.peerbox.watchservice.IFileEventManager;
import org.peerbox.watchservice.filetree.composite.FileComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.stage.Window;

public class Synchronization implements Initializable {
    
	private static final Logger logger = LoggerFactory.getLogger(Synchronization.class);
	private Set<Path> synchronizedFiles;
	
	@FXML private TreeView<PathItem> fileTreeView;
	@FXML private Button okButton;
	@FXML private Button cancelButton;
	@FXML private Button selectAllButton;
	@FXML private Button unselectAllButton;
	@FXML private Button refreshButton;
	
	private IFileEventManager eventManager;
	private IPeerboxFileManager fileManager;
	private TreeSet<FileNode> toSynchronize = new TreeSet<FileNode>(new FileNodeComparator());
	private TreeSet<FileNode> toDesynchronize = new TreeSet<FileNode>(new FileNodeComparator());
	
	public Set<FileNode> getToSynchronize(){
		return toSynchronize;
	}
	
	public Set<FileNode> getToDesynchronize(){
		return toDesynchronize;
	}
	
	private Vector<FileComponent> toSync = new Vector<FileComponent>();
	
	@Inject
	public Synchronization(FileManager fileManager, FileEventManager eventManager) {
		this.eventManager = eventManager;
		this.fileManager = fileManager;
	}
	
	public IFileEventManager getFileEventManager(){
		return eventManager;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//showDummyData();		
		logger.debug("Initialize Synchronization!");
		synchronizedFiles = eventManager.getFileTree().getSynchronizedPathsAsSet();
		createTreeWithFilesFromNetwork();
	}
	
	private void createTreeWithFilesFromNetwork() {
		try {
			FileNode filesFromNetwork = fileManager.listFiles();
			listFiles(filesFromNetwork);
		} catch (IllegalArgumentException | NoSessionException | NoPeerConnectionException
				| InvalidProcessStateException | ProcessExecutionException e) {
			e.printStackTrace();
		}
	}
	
	private void listFiles(FileNode fileNode){
//		boolean isSynched = synchronizedFiles.contains(fileNode.getFile().toPath());
//		logger.debug("File {} is selected: {}", fileNode.getFile().toPath(), isSynched);
		PathTreeItem invisibleRoot = new PathTreeItem(fileNode, this, false, true);
		fileTreeView.setCellFactory(CheckBoxTreeCell.<PathItem>forTreeView());    
	    fileTreeView.setRoot(invisibleRoot);
        fileTreeView.setEditable(false);
        fileTreeView.setShowRoot(false);
		
        for(FileNode topLevelNode : fileNode.getChildren()){
        	boolean isSynched = synchronizedFiles.contains(topLevelNode.getFile().toPath());
			PathTreeItem rootItem = new PathTreeItem(topLevelNode, this, isSynched);
			invisibleRoot.getChildren().add(rootItem);
		}

        for(FileNode child : fileNode.getChildren()){
			System.out.println("File " + child.getFile());
		}
	}

	public void acceptSyncAction(ActionEvent event) {
		synchronizedFiles = eventManager.getFileTree().getSynchronizedPathsAsSet();
		
		for(FileNode node : toSynchronize){
			if(!synchronizedFiles.contains(node.getFile().toPath()))
				eventManager.onFileSynchronized(node.getFile().toPath(), node.isFolder());
		}
		for(FileNode node: toDesynchronize.descendingSet()){
			eventManager.onFileDesynchronized(node.getFile().toPath());
		}
		
		toSynchronize.clear();
		toDesynchronize.clear();
		if(event.getTarget() != null && event.getTarget() instanceof Button){
			Button okButton = (Button)event.getTarget();
			Window window = okButton.getScene().getWindow();
			window.hide();
		}
	}
	
	@FXML
	public void selectAllAction(ActionEvent event) {
		PathTreeItem root = (PathTreeItem)fileTreeView.getRoot();
		root.setSelected(true);
	}
	
	@FXML
	public void unselectAllAction(ActionEvent event) {
		PathTreeItem root = (PathTreeItem)fileTreeView.getRoot();
		root.setSelected(false);
		root.setIndeterminate(false);
	}
	
	@FXML
	public void cancelAction(ActionEvent event) {
		if(event.getTarget() != null && event.getTarget() instanceof Button){
			Button cancelButton = (Button)event.getTarget();
			Window window = cancelButton.getScene().getWindow();
			window.hide();
		}
	}
	
	@FXML
	public void refreshAction(ActionEvent event){
		synchronizedFiles = eventManager.getFileTree().getSynchronizedPathsAsSet();
		createTreeWithFilesFromNetwork();
	}
	
//	private void showDummyData(){
//		FileNode root = new FileNode(null, userConfig.getRootPath().toFile(), userConfig.getRootPath().toString(), null, null);
//        PathTreeItem rootItem = 
//            new PathTreeItem(root, this, false);
//        rootItem.setExpanded(true);                  
//      
//        fileTreeView.setEditable(false);
//        
//        fileTreeView.setCellFactory(CheckBoxTreeCell.<PathItem>forTreeView());    
//
//        Path rootFolder = userConfig.getRootPath();
//        
//        try(Stream<Path> dirs = Files.list(rootFolder)){
//        	Iterator<Path> iter = dirs.iterator();
//        	while(iter.hasNext()){
//        		Path folder = iter.next();
//        		FileNode item = new FileNode(null, folder.toFile(), userConfig.getRootPath().toString(), null, null);
//        		PathTreeItem checkBoxTreeItem = new PathTreeItem(item, this, false);
//        		rootItem.getChildren().add(checkBoxTreeItem);
//        	}
//
//        } catch (IOException ex){
//        	ex.printStackTrace();
//        }
//        
//                       
//        fileTreeView.setRoot(rootItem);
//        fileTreeView.setShowRoot(true);
//	}
	
	private class FileNodeComparator implements Comparator<FileNode>{

		@Override
		public int compare(FileNode o1, FileNode o2) {
			String path1 = o1.getPath().toString();
			String path2 = o2.getPath().toString();
			return path1.compareTo(path2);
		}
	}
}
