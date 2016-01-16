package com.code.analyse;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yubing on 16/1/16.
 */
public class FileInfo {
    private VirtualFile file;
    private int     codeLine;
    private int     emptyLine;
    private int     commentLine;
    private int     totalLine;
    private List<FileInfo> children = new ArrayList<>();
    private FileInfo parent;

    public VirtualFile getFile() {
        return file;
    }

    public void setFile(VirtualFile file) {
        this.file = file;
    }

    public int getCodeLine() {
        return codeLine;
    }

    public void setCodeLine(int codeLine) {
        this.codeLine = codeLine;
    }

    public int getEmptyLine() {
        return emptyLine;
    }

    public void setEmptyLine(int emptyLine) {
        this.emptyLine = emptyLine;
    }

    public int getCommentLine() {
        return commentLine;
    }

    public void setCommentLine(int commentLine) {
        this.commentLine = commentLine;
    }

    public List<FileInfo> getChildren() {
        return children;
    }

    public void addChild(FileInfo child) {
        children.add(child);
    }

    public void removeChild(FileInfo child){
        children.remove(child);
    }

    public FileInfo getParent() {
        return parent;
    }

    public void setParent(FileInfo parent) {
        this.parent = parent;
    }

    public int getTotalLine() {
        return totalLine;
    }

    public void setTotalLine(int totalLine) {
        this.totalLine = totalLine;
    }

    @Override
    public String toString() {
        if(file == null){
            return "None";
        }
        if(children.size() == 0){
            return file.getName();
        }
        if(parent == null){
            return file.getName();
        }
        else{
            if(parent.getChildren().size() == 1) {
                return parent.toString() + "/" + file.getName();
            }
            else{
                return file.getName();
            }
        }
    }
}
