package com.code.analyse;

import com.intellij.openapi.vfs.VirtualFile;
import com.sun.xml.internal.ws.encoding.MtomCodec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yubing on 16/1/16.
 */
public class AnalyseThread extends Thread {
    private FinishFileLisenter lisenter = null;
    public AnalyseThread(FinishFileLisenter lisenter){
        this.lisenter = lisenter;
    }

    private boolean exit = false;

    private boolean isRunning = true;

    private static final int CODE = 0;
    private static final int COMMENT_LINE = 1;
    private static final int COMMENT_MUL = 2;
    private static final int STRING = 3;

    class LineInfo{
        public int index = 0;
        public boolean has_comment = false;
        public boolean has_code = false;
    }


    @Override
    public void run() {
        FileInfo fileInfo = lisenter.finish(null);
        while(fileInfo != null && !exit){
            calc(fileInfo);
            fileInfo = lisenter.finish(fileInfo);
        }
        if(!exit) {
            isRunning = false;
        }
    }

    private void calc(FileInfo fileInfo){
        VirtualFile file = fileInfo.getFile();
        try {
            byte [] data = file.contentsToByteArray();
            int code_count = 0;
            int empty_count = 0;
            int comment_count = 0;
            int total_count = 0;
            int status = CODE;
            byte last = 10;
            byte last_last = 10;
            List<LineInfo> list = new ArrayList<>();
            LineInfo lineInfo = new LineInfo();
            for (byte aData : data) {
                if (aData == 10) {
                    if(status == COMMENT_MUL){
                        lineInfo.has_comment = true;
                    }
                    else if(status == CODE){
                        if(last == '/' && last_last != '/' && last_last != '*'){
                            lineInfo.has_code = true;
                        }
                    }
                    list.add(lineInfo);
                    lineInfo = new LineInfo();
                    if(status == COMMENT_LINE){
                        status = CODE;
                    }
                } else if (aData == '/') {
                    if (status == CODE) {
                        if (last == '/') {
                            status = COMMENT_LINE;
                        }
                    }
                    else if(status == COMMENT_MUL){
                        if (last == '*') {
                            status = CODE;
                            lineInfo.has_comment = true;
                        }
                    }
                } else if (aData == '*') {
                    if (status == CODE) {
                        if (last == '/') {
                            status = COMMENT_MUL;
                        }
                    }
                } else if (aData == '\"') {
                    if (status == CODE) {
                        status = STRING;
                    } else if (status == STRING) {
                        status = CODE;
                    }
                }
                else{
                    if(status == COMMENT_MUL || status == COMMENT_LINE){
                        if(aData != '\t' && aData != ' '){
                            lineInfo.has_comment = true;
                        }
                    }
                    else if(status == STRING){
                        lineInfo.has_code = true;
                    }
                    else{
                        if((aData != '\t' && aData != ' ') ||
                                (last == '/' && last_last != '/' && last_last != '*')){
                            lineInfo.has_code = true;
                        }
                    }
                }
                if (aData != '\r') {
                    last_last = last;
                    last = aData;
                }
            }
            list.add(lineInfo);
            for(LineInfo info:list){
                if(info.has_code){
                    code_count++;
                }
                if(info.has_comment){
                    comment_count++;
                }
                if(!info.has_comment && !info.has_code){
                    empty_count++;
                }
                else{
                    total_count++;
                }
            }
            fileInfo.setCodeLine(code_count);
            fileInfo.setEmptyLine(empty_count);
            fileInfo.setCommentLine(comment_count);
            fileInfo.setTotalLine(total_count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void exit(){
        exit = true;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
