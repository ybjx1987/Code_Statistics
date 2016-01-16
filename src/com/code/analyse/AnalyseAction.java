package com.code.analyse;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

/**
 * Created by yubing on 16/1/16.
 */
public class AnalyseAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        Analyse analyse = new Analyse();
        if(project != null) {
            analyse.analyse(project.getBaseDir());
        }
    }
}
