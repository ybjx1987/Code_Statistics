package com.code.analyse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AnalyseProgressDialog extends JDialog implements AnalyseListener{
    private JPanel contentPane;
    private JProgressBar progressBar1;
    private JButton cancelButton;
    private JLabel fileName;
    private Analyse analyse;

    private Object lock = new Object();

    public AnalyseProgressDialog(Analyse analyse) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(cancelButton);

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
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
        this.analyse = analyse;
    }

    private void onCancel() {
        analyse.cancel();
    }
    private void setToMiddle(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
        int dw = 600;
        int dh = 100;

        setBounds((width - dw) / 2, (height - dh) / 2, dw, dh);
        Dimension size = new Dimension(dw,dh);
        setMinimumSize(size);
        setMaximumSize(size);
        setPreferredSize(size);
    }

    @Override
    public void maxValue(int value) {
        progressBar1.setMaximum(value);
    }

    @Override
    public void finishFile(String name,int value) {
        fileName.setText("文件：" + name);
        progressBar1.setValue(value);
    }
}
