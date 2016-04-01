package com.cbchot.plugin.xml;

import com.cbchot.plugin.util.DubboResolveFileSynchronizer;
import com.cbchot.plugin.util.MyIconLoader;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/31.
 */
public class DubboServerXMLTransformer extends AnAction {

    private static final String DUBBO_SERVER_FILE_NAME = "dubbo-server.xml";

    public DubboServerXMLTransformer() {
        super(MyIconLoader.DUBBO_ICON);
    }

    public void actionPerformed(AnActionEvent e) {
        final VirtualFile fileToTransform = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileToTransform.getInputStream()));
            File resolveFile = DubboResolveFileSynchronizer.synchronizeDubboResolve(reader);
            if (resolveFile == null) return;
            if ((resolveFile.exists()) && (resolveFile.length() > 0L))
                showMsg("Append To UserDir Success.");
        } catch (Exception ex) {
            showErrorMsg(ex);
        }
    }

    @Override
    public void update(AnActionEvent e) {
        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        final boolean isStringsXML = isStringsFile(file);
        e.getPresentation().setEnabled(isStringsXML);
        e.getPresentation().setVisible(isStringsXML);
    }

    @Contract("null -> false")
    private static boolean isStringsFile(@Nullable VirtualFile file) {
        return file != null && file.getName().equals(DUBBO_SERVER_FILE_NAME);
    }

    private void showErrorMsg(Exception e) {
        Messages.showErrorDialog("operate fail error:£º" + e.getMessage(), "Dubbo Config Transformer");
    }

    private void showMsg(String msg) {

        Messages.showMessageDialog(
                msg,
                "Dubbo Config Transformer",
                Messages.getInformationIcon()
        );
    }

}
