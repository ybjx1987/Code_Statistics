package com.code.analyse;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class CodeAnalyseDialog extends JDialog implements TreeSelectionListener{
    private JPanel contentPane;
    private JButton buttonCancel;
    private JTree codeTree;
    private JLabel total_line;
    private JLabel code_line;
    private JLabel comment_line;
    private JLabel empty_line;
    private JLabel comment_rate;

    public CodeAnalyseDialog(FileInfo root) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonCancel);

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        setTitle("代码统计");
        setToMiddle();

        if(root.getFile() == null){
            codeTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("None")));
        }
        else{
            TreeModel model = new DefaultTreeModel(getNode(root));
            codeTree.setModel(model);
        }
        codeTree.addTreeSelectionListener(this);
        codeTree.setSelectionRow(0);
    }

    private DefaultMutableTreeNode getNode(FileInfo fileInfo){
        if(fileInfo.getChildren().size() > 1) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("");
            node.setUserObject(fileInfo);
            for (FileInfo info : fileInfo.getChildren()) {
                node.add(getNode(info));
            }
            return node;
        }
        else if(fileInfo.getChildren().size() == 1){
            if(fileInfo.getChildren().get(0).getChildren().size() == 0){
                DefaultMutableTreeNode node = new DefaultMutableTreeNode("");
                node.setUserObject(fileInfo);
                node.add(getNode(fileInfo.getChildren().get(0)));
                return node;
            }
            else {
                return getNode(fileInfo.getChildren().get(0));
            }
        }
        else{
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("");
            node.setUserObject(fileInfo);
            return node;
        }
    }

    private void onCancel() {
        dispose();
    }

    private void setToMiddle(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        setBounds(width / 4, height / 4, width / 2, height / 2);
        setMinimumSize(new Dimension(width / 2, height / 2));
        setMaximumSize(new Dimension(width / 2, height / 2));
        setPreferredSize(new Dimension(width / 2, height / 2));
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        TreePath [] paths = codeTree.getSelectionPaths();
        ArrayList<FileInfo> list = new ArrayList<>();
        if(paths != null) {
            for (TreePath path : paths) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                FileInfo fileInfo = (FileInfo) node.getUserObject();
                if (fileInfo == null) {
                    continue;
                }
                ArrayList<FileInfo> temp = new ArrayList<>();
                temp.add(fileInfo);
                while (!temp.isEmpty()) {
                    fileInfo = temp.get(0);
                    temp.remove(0);
                    if (fileInfo.getChildren().size() == 0) {
                        if(!list.contains(fileInfo)) {
                            list.add(fileInfo);
                        }
                    } else {
                        temp.addAll(fileInfo.getChildren());
                    }
                }
            }
        }
        int code_count = 0;
        int empty_count = 0;
        int comment_count = 0;
        int total_count = 0;
        for(FileInfo fileInfo:list){
            code_count+=fileInfo.getCodeLine();
            empty_count+=fileInfo.getEmptyLine();
            comment_count += fileInfo.getCommentLine();
            total_count += fileInfo.getTotalLine();
        }
        total_line.setText(""+total_count);
        code_line.setText(""+code_count);
        comment_line.setText(""+comment_count);
        empty_line.setText(""+empty_count);
        if(total_count>0){
            float rate = comment_count*100.0f/(total_count);
            comment_rate.setText(String.format("%2.2f%%",rate));
        }
        else{
            comment_rate.setText("-");
        }
    }
}
