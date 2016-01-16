package com.code.analyse;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yubing on 16/1/16.
 */
public class Analyse implements FinishFileLisenter{
    private boolean isRunning = false;

    private FileInfo rootFile = new FileInfo();

    private List<FileInfo> allFileList = new ArrayList<>();
    private List<FileInfo> finishList = new ArrayList<>();
    private List<FileInfo> noneList = new ArrayList<>();
    private AnalyseProgressDialog analyseProgressDialog = null;
    private VirtualFile root = null;

    private Object lock = new Object();

    private final int THREAD_NUM = 4;

    private AnalyseThread [] analyseThread= new AnalyseThread[THREAD_NUM];


    public void analyse(VirtualFile r){
        if(isRunning){
            return;
        }
        isRunning = true;
        root = r;
        analyseProgressDialog = new AnalyseProgressDialog(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                update();
                start();
            }
        }).start();
        analyseProgressDialog.setVisible(true);
    }

    public void update(){
        rootFile = new FileInfo();
        if(!root.isValid()){
            finish();
        }

        updateFile(root, rootFile);
        clearFile(rootFile);
    }

    private void clearFile(FileInfo fileInfo){
        VirtualFile file = fileInfo.getFile();
        if(file.isDirectory()){
            List<FileInfo> list = new ArrayList<>();
            list.addAll(fileInfo.getChildren());
            for(FileInfo info:list){
                clearFile(info);
            }
            if(fileInfo.getChildren().size() == 0){
                if(fileInfo.getParent() != null){
                    fileInfo.getParent().removeChild(fileInfo);
                }
            }
        }
        else{
            if(!allFileList.contains(fileInfo)){
                if(fileInfo.getParent() != null){
                    fileInfo.getParent().removeChild(fileInfo);
                }
            }
        }
    }

    private void updateFile(VirtualFile file,FileInfo parent){
        parent.setFile(file);
        if(file.isDirectory()){
            for(VirtualFile child:file.getChildren()){
                FileInfo fileInfo = new FileInfo();
                fileInfo.setParent(parent);
                parent.addChild(fileInfo);
                updateFile(child,fileInfo);
            }
        }
        else{
            if(file.getName().endsWith(".java")) {
                allFileList.add(parent);
            }
        }
    }

    public void start(){
        finishList.clear();
        noneList.clear();
        noneList.addAll(allFileList);
        analyseProgressDialog.maxValue(allFileList.size());

        for(int i = 0;i<THREAD_NUM;i++){
            analyseThread[i] = new AnalyseThread(this);
        }
        for(Thread thread:analyseThread){
            thread.start();
        }
    }

    public void finish(){
        isRunning = false;
        if(analyseProgressDialog != null){
            analyseProgressDialog.dispose();
            analyseProgressDialog = null;
        }

        CodeAnalyseDialog codeAnalyseDialog = new CodeAnalyseDialog(rootFile);
        codeAnalyseDialog.setVisible(true);
    }

    public void cancel(){
        isRunning = false;
        if(analyseProgressDialog != null){
            analyseProgressDialog.dispose();
            analyseProgressDialog = null;
        }
        for(AnalyseThread thread:analyseThread){
            thread.exit();
        }
    }

    @Override
    public FileInfo finish(FileInfo fileInfo) {
        if(fileInfo != null) {
            int value = (int) (Math.random() * THREAD_NUM);
            if (value % 2 == 0) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        analyseProgressDialog.finishFile(fileInfo.getFile().getName(), finishList.size());
                    }
                });
            }
        }
        synchronized (lock) {
            if(fileInfo != null) {
                finishList.add(fileInfo);
            }
            if(noneList.size() > 0) {
                FileInfo info = noneList.get(0);
                noneList.remove(0);
                return info;
            }
            else{
                int count = 0;
                for(AnalyseThread thread:analyseThread){
                    if(thread.isRunning()){
                        count ++;
                    }
                }
                if(count <= 1) {
                    finish();
                }
                return null;
            }
        }
    }
}
