package org.ideaplugins.syncedit;

import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.openapi.components.ProjectComponent;
import org.jetbrains.annotations.NotNull;

public class SyncEditProjectComponent
implements ProjectComponent {


    public SyncEditProjectComponent() {
        //System.out.println("SyncEditComponent(" + project + ")");
    }



    public void initComponent() {
        //System.out.println("SyncEditComponent initComponent()");
    }



    public void disposeComponent() {
        //System.out.println("SyncEditComponent disposeComponent()");
    }



    @NotNull
    public String getComponentName() {
        //System.out.println("SyncEditComponent getComponentName()");
        return "SyncEditIntentionAction";
    }



    public void projectOpened() {
        //System.out.println("SyncEditComponent projectOpened()");
        IntentionManager.getInstance().addAction(new SyncEditModeAction());
    }



    public void projectClosed() {
    }
}