package com.cbchot.plugin;

import com.cbchot.plugin.dubbo.*;
import com.cbchot.plugin.util.DubboResolveFileSynchronizer;
import com.intellij.codeInsight.actions.OptimizeImportsProcessor;
import com.intellij.codeInsight.actions.RearrangeCodeProcessor;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.checkin.CheckinHandlerUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DubboServiceDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea textArea1;
    private JTextField textField1;
    private JTextField textField2;
    private JButton deleteButton;

    private String interfaceModulePath = null;
    private String dubboModulePath = null;

    private List<AbstarctDubboService> beans;

    private Project project;

    public DubboServiceDialog(String interfaceModulePath, String dubboModulePath, Project project) {
        this();
        this.interfaceModulePath = interfaceModulePath;
        this.dubboModulePath = dubboModulePath;
        this.project = project;
    }

    private DubboServiceDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDel();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });


// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


    }

    private void onDel() {
        if (beans != null && beans.size() > 0) {
            for (AbstarctDubboService bean : beans) {
                bean.deleteFile();
            }
            Messages.showMessageDialog(
                    "delete all files done",
                    "create dubbo service",
                    Messages.getInformationIcon()
            );
        }
    }

    private void showErrorMsg(String msg) {
        Messages.showErrorDialog(msg, "create dubbo service");
    }

    private void onOK() {
// add your code here
        beans = new ArrayList<AbstarctDubboService>();
        String pojoName = this.textField1.getText().trim();
        String serviceName = this.textField2.getText().trim();
        String methodsContent = this.textArea1.getText();

        if(StringUtils.isEmpty(pojoName) || StringUtils.isEmpty(serviceName) || StringUtils.isEmpty(methodsContent)) {
            return;
        }

        DubboServiceInfo serviceInfo = new DubboServiceInfo(serviceName, pojoName, methodsContent);
        PojoBean pojoBean = new PojoBean(serviceInfo, interfaceModulePath);

        try {
            pojoBean.createFile();
        } catch (IOException e) {
            showErrorMsg(pojoBean + " create file fail");
        }

        IFacadeBean iFacadeBean = new IFacadeBean(serviceInfo, interfaceModulePath);
        try {
            iFacadeBean.createFile();
        } catch (IOException e) {
            showErrorMsg(iFacadeBean + " create file fail");
        }

        FacadeBean facadeBean = new FacadeBean(serviceInfo, dubboModulePath);
        try {
            facadeBean.createFile();
        } catch (IOException e) {
            showErrorMsg(facadeBean + " create file fail");
        }

        ServiceBean serviceBean = new ServiceBean(serviceInfo, dubboModulePath);
        try {
            serviceBean.createFile();
        } catch (IOException e) {
            showErrorMsg(serviceBean + " create file fail");
        }

        if (methodsContent.contains("@m")) {
            MasterMapperBean masterMapperBean = new MasterMapperBean(serviceInfo, dubboModulePath);
            beans.add(masterMapperBean);
            try {
                masterMapperBean.createFile();
            } catch (IOException e) {
                showErrorMsg(masterMapperBean + " create file fail");
            }
        }
        if (methodsContent.contains("@s")) {
            SlaveMapperBean slaveMapperBean = new SlaveMapperBean(serviceInfo, dubboModulePath);
            beans.add(slaveMapperBean);
            try {
                slaveMapperBean.createFile();
            } catch (IOException e) {
                showErrorMsg(slaveMapperBean + " create file fail");
            }
        }


        // 写dubbo-server.xml文件
        String dubboServerStr = "<dubbo:service interface=\"com.cbchot.ifacade.I%s\" version=\"1.0\" ref=\"%s\" actives=\"500\" executes=\"500\"/>";
        String lowServiceName = serviceName.replaceFirst(serviceName.substring(0, 1), serviceName.substring(0, 1).toLowerCase());
        dubboServerStr = String.format(dubboServerStr, serviceName, lowServiceName);
        File dubboServerXML = new File(dubboModulePath + "\\src\\main\\resources\\dubbo-server.xml");
        if (!dubboServerXML.exists()) {
            showMsg("append service to dubbo-server.xml fail, file not exists");
        }

        try {
            List<String> lines = FileUtils.readLines(dubboServerXML);
            lines.remove("</beans>");
            lines.add(dubboServerStr);
            lines.add("</beans>");
            FileUtils.writeLines(dubboServerXML, lines);
            showMsg("dubbo-server.xml append success");
        } catch (Exception e) {
            e.printStackTrace();
        }


        // 同步本地 properteis文件
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(FileUtils.openInputStream(dubboServerXML)));
            File resolveFile = DubboResolveFileSynchronizer.synchronizeDubboResolve(reader);
            if (resolveFile.exists() && resolveFile.length() > 0L) {
                showMsg("dubbo-resolve.properties synchronize success");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        beans.add(pojoBean);
        beans.add(iFacadeBean);
        beans.add(facadeBean);
        beans.add(serviceBean);

        VirtualFileManager.getInstance().syncRefresh();

        for (AbstarctDubboService bean : beans) {
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(bean.getFullFilePath());

            try {
                PsiFile toFormatPsiFile = PsiManager.getInstance(project).findFile(virtualFile);

                OptimizeImportsProcessor importsProcessor = new OptimizeImportsProcessor(project, toFormatPsiFile);
                importsProcessor.run();

                ReformatCodeProcessor reformatProcessor = new ReformatCodeProcessor(project, toFormatPsiFile, null, false);
                reformatProcessor.run();

//                RearrangeCodeProcessor rearrangeCodeProcessor = new RearrangeCodeProcessor(project, new PsiFile[]{toFormatPsiFile}, RearrangeCodeProcessor.COMMAND_NAME, null);
//                rearrangeCodeProcessor.run();
            } catch (Exception e) {
                System.out.println(bean.getFullFilePath());
                e.printStackTrace();
            }
        }
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        VirtualFileManager.getInstance().syncRefresh();
        showMsg("create success");
        //        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public void open() {
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void showMsg(String msg) {
        Messages.showMessageDialog(msg, "create dubbo service", Messages.getInformationIcon());
    }

    public static void main(String[] args) {
        DubboServiceDialog dialog = new DubboServiceDialog();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        System.exit(0);
    }
}
