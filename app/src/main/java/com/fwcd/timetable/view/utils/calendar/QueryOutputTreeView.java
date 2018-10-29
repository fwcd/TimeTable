package com.fwcd.timetable.view.utils.calendar;

import com.fwcd.timetable.model.query.QueryOutputNode;
import com.fwcd.timetable.api.view.FxView;
import com.fwcd.timetable.viewmodel.TimeTableAppContext;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class QueryOutputTreeView implements FxView {
	private final TreeView<QueryOutputNode> node;
	
	public QueryOutputTreeView(TimeTableAppContext context) {
		node = new TreeView<>();
		node.setShowRoot(false);
		node.setCellFactory(tree -> new QueryOutputTreeCell(context));
	}
	
	public void setRoot(QueryOutputNode root) {
		node.setRoot(toFxTree(root));
	}
	
	private TreeItem<QueryOutputNode> toFxTree(QueryOutputNode treeNode) {
		TreeItem<QueryOutputNode> treeItem = new TreeItem<>(treeNode);
		treeNode.getChilds().stream()
			.map(this::toFxTree)
			.forEach(treeItem.getChildren()::add);
		return treeItem;
	}
	
	@Override
	public Node getNode() { return node; }
}