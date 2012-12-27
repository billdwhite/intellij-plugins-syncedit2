package org.ideaplugins.syncedit;

import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

public class SyncEditProjectComponent
implements ProjectComponent {

    private final Project _project;



    public SyncEditProjectComponent(Project project) {
        this._project = project;
        //System.out.println("SyncEditComponent(" + project + ")");
    }



    public void initComponent() {
        //System.out.println("SyncEditComponent initComponent()");
    }



    public void disposeComponent() {
        //System.out.println("SyncEditComponent disposeComponent()");
    }



    public String getComponentName() {
        //System.out.println("SyncEditComponent getComponentName()");
        return "SyncEditIntentionAction";
    }



    public void projectOpened() {
        //System.out.println("SyncEditComponent projectOpened()");
        IntentionManager.getInstance(this._project).addAction(new SyncEditModeAction());
    }



    public void projectClosed() {
    }
}