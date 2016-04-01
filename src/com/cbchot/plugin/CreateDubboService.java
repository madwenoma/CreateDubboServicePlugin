package com.cbchot.plugin;

import com.cbchot.plugin.util.MyIconLoader;
import com.intellij.codeInsight.actions.RearrangeCodeProcessor;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.ProjectCoreUtil;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.ListUtil;
import jetbrains.buildServer.messages.serviceMessages.Message;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;

/**
 * Created by Administrator on 2016/3/29.
 */
public class CreateDubboService extends AnAction {

    private static final String DUBBO_MODULE_NAME = "cbchot-dubbo";
    private static final String INTERFACE_MODULE_NAME = "cbchot-interface";

    public CreateDubboService() {
        super(MyIconLoader.DUBBO_ICON);
    }

    public void actionPerformed(AnActionEvent e) {

//        ProjectManager.getInstance().getOpenProjects();
        Module[] modules = ModuleManager.getInstance(e.getProject()).getModules();
        String interfaceModulePath = null;
        String dubboModulePath = null;

        for (Module module : modules) {
            String moduleFilePath = module.getModuleFilePath();//module iml文件path

            if (module.getName().equals(DUBBO_MODULE_NAME)) {
                dubboModulePath = StringUtils.substringBeforeLast(moduleFilePath, "/");
            }
            if (module.getName().equals(INTERFACE_MODULE_NAME)) {
                interfaceModulePath = StringUtils.substringBeforeLast(moduleFilePath, "/");
            }
        }

        if (StringUtils.isEmpty(interfaceModulePath) || StringUtils.isEmpty(dubboModulePath)) {
//            Messages.showMessageDialog(
//                    "Hello World!",
//                    "Sample",
//                    Messages.getInformationIcon()
//            );
            Messages.showErrorDialog(e.getProject(), "project必须包含cbchot-dubbo和cbchot-interface", "创建dubbo服务失败");
            return;
        }

        DubboServiceDialog dubboServiceDialog = new DubboServiceDialog(interfaceModulePath, dubboModulePath, e.getProject());
        dubboServiceDialog.open();
        // TODO: insert action logic here

//        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File("C:\\Users\\Administrator\\IdeaProjects\\untitled\\cbchot-interface\\src\\main\\java\\com\\cbchot\\ifacade\\ICommentFacade.java"));
//        PsiFile file = PsiManager.getInstance(e.getProject()).findFile(virtualFile);
//        ReformatCodeProcessor s = new ReformatCodeProcessor(e.getProject(), file, null, false);
//
//        s.run();
//
//        RearrangeCodeProcessor rearrangeCodeProcessor = new RearrangeCodeProcessor(e.getProject(), new PsiFile[]{file}, RearrangeCodeProcessor.COMMAND_NAME, null);
//
//        rearrangeCodeProcessor.run();


    }

    public static void main(String[] args) {
        Icon icon = IconLoader.getIcon("/icons/dubbo.png");
    }

//    private PsiMethod getPsiMethodFromContext(AnActionEvent e) {
//        PsiElement elementAt = getPsiElement(e);
//
//        if (elementAt == null) {
//            return null;
//        }
//        return PsiTreeUtil.getParentOfType(elementAt, PsiMethod.class);
//    }
//
//    private PsiElement getPsiElement(AnActionEvent e) {
//        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
//        Editor editor = e.getData(PlatformDataKeys.EDITOR);
//        if (psiFile == null || editor == null) {
//            e.getPresentation().setEnabled(false);
//            return null;
//        }
//        //用来获取当前光标处的PsiElement
//        int offset = editor.getCaretModel().getOffset();
//        return psiFile.findElementAt(offset);
//    }
}
