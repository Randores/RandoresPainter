/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 socraticphoenix@gmail.com
 * Copyright (c) 2016 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gmail.socraticphoenix.forge.randores.painter.gui;

import com.gmail.socraticphoenix.forge.randores.painter.PainterApp;
import com.gmail.socraticphoenix.forge.randores.painter.Project;
import com.gmail.socraticphoenix.inversey.many.DangerousConsumer1;
import com.gmail.socraticphoenix.jlsc.JLSCException;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class SelectProjectScreen {
    public JComboBox projects;
    public JButton createNew;
    public JTextField newName;
    public JComboBox newType;
    public JPanel panel1;
    public JTextField newFile;
    public JButton open;
    private JButton delete;
    private JButton importNew;

    private JFrame frame;

    public JFrame display() {
        if (this.frame == null) {
            this.delete.addActionListener(e -> {
                new VerifyScreen(() -> {
                    String name = this.projects.getSelectedItem().toString();
                    Project project = PainterApp.projectMap.get(name);
                    try {
                        PainterApp.projectMap.remove(name);
                        project.deleteProject();
                    } catch (IOException | JLSCException e1) {
                        new ErrorScreen("Unable to delete project: " + e1.getMessage()).display();
                    }
                    this.initValues();
                }).display();
            });
            this.open.addActionListener(e -> {
                Project project = PainterApp.projectMap.get(this.projects.getSelectedItem().toString());
                if (project == null) {
                    new ErrorScreen("Unable to find project: ", this.projects.getSelectedItem()).display();
                } else {
                    try {
                        project.loadResources();
                        project.loadProject();
                        this.frame.dispose();
                    } catch (IOException | JLSCException e1) {
                        new ErrorScreen("Unable to open project: " + e1.getMessage()).display();
                        e1.printStackTrace();
                    }
                }
            });

            this.importNew.addActionListener(e -> {
                this.create(Project::importProject);
            });

            this.createNew.addActionListener(e -> {
                this.create(p -> {
                });
            });

            this.newFile.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || Character.isDigit(c) || c == '_' || c == '\b')) {
                        e.consume();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });

            this.frame = new JFrame("Select Project");
            this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.frame.setSize(600, 400);
            this.frame.add(this.panel1);
            this.frame.setVisible(true);
            return this.frame;
        } else {
            this.frame.setVisible(true);
            return this.frame;
        }
    }

    private void create(DangerousConsumer1<Project, Exception> endAction) {
        String fileName = this.newFile.getText();
        if (fileName.isEmpty()) {
            new ErrorScreen("File name cannot be empty!").display();
        } else {
            String projectName = this.newName.getText();
            if (projectName.isEmpty()) {
                new ErrorScreen("Project name cannot be empty").display();
            } else if (PainterApp.projectMap.containsKey(projectName)) {
                new ErrorScreen("Project \"" + projectName + "\" already exists").display();
            } else {
                File file = new File(PainterApp.projects, fileName);
                if (file.exists()) {
                    new ErrorScreen("File already exists!");
                } else {
                    Project.Type type = Project.Type.valueOf(this.newType.getSelectedItem().toString());
                    try {
                        Project project = Project.create(projectName, file, type);
                        PainterApp.projectMap.put(project.name, project);
                        this.initValues();
                        endAction.call(project);
                        project.loadProject();
                        this.frame.dispose();
                    } catch (Exception e1) {
                        new ErrorScreen("Unable to load project: " + e1.getMessage()).display();
                    }
                }
            }
        }
    }

    public void initValues() {
        projects.removeAllItems();
        newType.removeAllItems();

        for (Map.Entry<String, Project> entry : PainterApp.projectMap.entrySet()) {
            projects.addItem(entry.getKey());
        }

        for (Project.Type type : Project.Type.values()) {
            newType.addItem(type.name());
        }

        File project = new File(PainterApp.projects, "project");
        int i = 0;
        while (project.exists()) {
            project = new File(PainterApp.projects, "project" + ++i);
        }
        newFile.setText(project.getName());
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(10, 3, new Insets(0, 0, 0, 0), -1, -1));
        projects = new JComboBox();
        panel1.add(projects, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        createNew = new JButton();
        createNew.setText("Create New");
        panel1.add(createNew, new GridConstraints(8, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newName = new JTextField();
        panel1.add(newName, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        newType = new JComboBox();
        panel1.add(newType, new GridConstraints(7, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setFont(new Font(label1.getFont().getName(), label1.getFont().getStyle(), 20));
        label1.setText("Create New Project");
        panel1.add(label1, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setFont(new Font(label2.getFont().getName(), label2.getFont().getStyle(), 20));
        label2.setText("Open Project");
        panel1.add(label2, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Name");
        panel1.add(label3, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Type");
        panel1.add(label4, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Project");
        panel1.add(label5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newFile = new JTextField();
        panel1.add(newFile, new GridConstraints(6, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("File");
        panel1.add(label6, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        open = new JButton();
        open.setText("Open");
        panel1.add(open, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        delete = new JButton();
        delete.setText("Delete");
        panel1.add(delete, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        importNew = new JButton();
        importNew.setText("Import New");
        panel1.add(importNew, new GridConstraints(9, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
