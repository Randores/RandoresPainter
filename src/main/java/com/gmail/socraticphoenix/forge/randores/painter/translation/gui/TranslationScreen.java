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
package com.gmail.socraticphoenix.forge.randores.painter.translation.gui;

import com.gmail.socraticphoenix.forge.randores.painter.gui.ErrorScreen;
import com.gmail.socraticphoenix.forge.randores.painter.gui.SelectProjectScreen;
import com.gmail.socraticphoenix.forge.randores.painter.gui.VerifyScreen;
import com.gmail.socraticphoenix.forge.randores.painter.translation.TranslationProject;
import com.gmail.socraticphoenix.forge.randores.painter.translation.Translation;
import com.gmail.socraticphoenix.jlsc.JLSCException;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class TranslationScreen {
    private JList translations;
    private JList unfinishedTranslations;
    private JTextField key;
    private JTextArea english;
    private JTextArea translation;
    private JButton saveButton;
    private JLabel translationProjectLabel;
    private JPanel panel1;
    private JButton exportButton;
    private JTextField language;

    private TranslationProject project;

    private JFileChooser chooser;

    private JFrame frame;

    public TranslationScreen(TranslationProject project) {
        this.project = project;
        this.chooser = new JFileChooser();
        this.chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".zip") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "ZIP files";
            }
        });
    }

    public JFrame display() {
        if (this.frame == null) {
            this.frame = new JFrame("Translation Project");
            this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            this.frame.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {

                }

                @Override
                public void windowClosing(WindowEvent e) {
                    new VerifyScreen(() -> {
                        frame.dispose();
                        SelectProjectScreen screen = new SelectProjectScreen();
                        screen.initValues();
                        screen.display();
                    }).display();
                }

                @Override
                public void windowClosed(WindowEvent e) {

                }

                @Override
                public void windowIconified(WindowEvent e) {

                }

                @Override
                public void windowDeiconified(WindowEvent e) {

                }

                @Override
                public void windowActivated(WindowEvent e) {

                }

                @Override
                public void windowDeactivated(WindowEvent e) {

                }
            });
            this.frame.setSize(1300, 800);
            this.frame.add(this.panel1);

            this.translationProjectLabel.setText("Translation Project: " + this.project.name);

            this.translations.setCellRenderer(new TranslationRenderer());
            this.unfinishedTranslations.setCellRenderer(new TranslationRenderer());

            this.translations.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.unfinishedTranslations.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            AtomicBoolean changing = new AtomicBoolean(false);

            this.language.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    onUpdate();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    onUpdate();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    onUpdate();
                }

                private void onUpdate() {
                    project.lang = language.getText();
                }

            });

            try {
                this.translations.setListData(this.project.getTranslations().toArray());
                this.unfinishedTranslations.setListData(this.project.getUnfinished().toArray());
            } catch (IOException e) {
                new ErrorScreen("Unable to load cache: " + e.getMessage()).display();
                return this.frame;
            }

            this.exportButton.addActionListener(e -> {
                String lang = this.language.getText();
                if (lang.isEmpty()) {
                    new ErrorScreen("Language cannot be blank").display();
                } else {

                    int res = this.chooser.showSaveDialog(this.panel1);
                    if (res == JFileChooser.APPROVE_OPTION) {
                        File file = this.chooser.getSelectedFile();
                        try {
                            this.project.exportProject(file);
                        } catch (IOException | JLSCException e1) {
                            e1.printStackTrace();
                            new ErrorScreen("Unable to export project: " + e1.getMessage()).display();
                        }
                    }
                }
            });

            this.saveButton.addActionListener(e -> {
                try {
                    this.project.saveResources();
                } catch (IOException | JLSCException e1) {
                    new ErrorScreen("Unable to save project: " + e1.getMessage()).display();
                }
            });

            this.translations.addListSelectionListener(e -> {
                if (!changing.get()) {
                    changing.set(true);
                    this.unfinishedTranslations.clearSelection();
                    Translation selected = (Translation) this.translations.getSelectedValue();
                    this.key.setText(selected.key);
                    try {
                        this.english.setText(selected.getEnglish().value);
                    } catch (IOException ignore) {

                    }
                    this.translation.setText(selected.value);
                    changing.set(false);
                }
            });

            this.unfinishedTranslations.addListSelectionListener(e -> {
                if (!changing.get()) {
                    changing.set(true);
                    this.translations.clearSelection();
                    Translation selected = (Translation) this.unfinishedTranslations.getSelectedValue();
                    this.key.setText(selected.key);
                    this.english.setText(selected.value);
                    this.translation.setText("");
                    changing.set(false);
                }
            });

            this.translation.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    onChange();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    onChange();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    onChange();
                }

                private void onChange() {
                    if (!changing.get()) {
                        changing.set(true);
                        String trans = translation.getText();
                        if (trans.isEmpty() && translations.getSelectedValue() != null) {
                            Translation translation = (Translation) translations.getSelectedValue();
                            project.getTranslations().remove(translation);
                            updateListData();
                            translations.clearSelection();
                            try {
                                Translation unused = project.getUnfinished().stream().filter(t -> t.key.equals(translation.key)).findFirst().get();
                                unfinishedTranslations.setSelectedValue(unused, true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (!trans.isEmpty() && unfinishedTranslations.getSelectedValue() != null) {
                            Translation t = (Translation) unfinishedTranslations.getSelectedValue();
                            Translation finished = new Translation(t.key, translation.getText(), t.type);
                            project.getTranslations().add(finished);
                            updateListData();
                            unfinishedTranslations.clearSelection();
                            translations.setSelectedValue(finished, true);
                        } else if (!trans.isEmpty() && translations.getSelectedValue() != null) {
                            Translation translation = (Translation) translations.getSelectedValue();
                            translation.value = trans;
                        }
                        changing.set(false);
                    }
                }
            });

            this.language.setText(this.project.lang);

            this.frame.setVisible(true);
            return this.frame;
        } else {
            this.frame.setVisible(true);
            return this.frame;
        }
    }

    private void updateListData() {
        try {
            this.translations.setListData(this.project.getTranslations().toArray());
            this.unfinishedTranslations.setListData(this.project.getUnfinished().toArray());
        } catch (IOException e) {
            new ErrorScreen("Unable to load cache: " + e.getMessage()).display();
        }
    }

    public Translation getSelectedTranslation() {
        return (Translation) (this.translations.getSelectedValue() == null ? this.translations.getSelectedValue() : this.unfinishedTranslations.getSelectedValue());
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
        panel1.setLayout(new GridLayoutManager(10, 4, new Insets(0, 0, 0, 0), -1, -1));
        key = new JTextField();
        key.setEditable(false);
        key.setFont(new Font(key.getFont().getName(), key.getFont().getStyle(), 16));
        panel1.add(key, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setFont(new Font(label1.getFont().getName(), label1.getFont().getStyle(), 18));
        label1.setText("Key:");
        panel1.add(label1, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setFont(new Font(label2.getFont().getName(), label2.getFont().getStyle(), 18));
        label2.setText("English:");
        panel1.add(label2, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setFont(new Font(label3.getFont().getName(), label3.getFont().getStyle(), 18));
        label3.setText("Translation:");
        panel1.add(label3, new GridConstraints(5, 2, 3, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setFont(new Font(saveButton.getFont().getName(), saveButton.getFont().getStyle(), 18));
        saveButton.setText("Save");
        panel1.add(saveButton, new GridConstraints(8, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        translationProjectLabel = new JLabel();
        translationProjectLabel.setFont(new Font(translationProjectLabel.getFont().getName(), translationProjectLabel.getFont().getStyle(), 20));
        translationProjectLabel.setText("Translation Project: ");
        panel1.add(translationProjectLabel, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(3, 0, 3, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        translations = new JList();
        translations.setFont(new Font(translations.getFont().getName(), translations.getFont().getStyle(), 14));
        scrollPane1.setViewportView(translations);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel1.add(scrollPane2, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        unfinishedTranslations = new JList();
        unfinishedTranslations.setFont(new Font(unfinishedTranslations.getFont().getName(), unfinishedTranslations.getFont().getStyle(), 14));
        scrollPane2.setViewportView(unfinishedTranslations);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel1.add(scrollPane3, new GridConstraints(5, 3, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        translation = new JTextArea();
        translation.setFont(new Font(translation.getFont().getName(), translation.getFont().getStyle(), 18));
        translation.setLineWrap(true);
        translation.setWrapStyleWord(true);
        scrollPane3.setViewportView(translation);
        final JScrollPane scrollPane4 = new JScrollPane();
        panel1.add(scrollPane4, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        english = new JTextArea();
        english.setEditable(false);
        english.setFont(new Font(english.getFont().getName(), english.getFont().getStyle(), 18));
        english.setLineWrap(true);
        english.setWrapStyleWord(true);
        scrollPane4.setViewportView(english);
        final JLabel label4 = new JLabel();
        label4.setFont(new Font(label4.getFont().getName(), label4.getFont().getStyle(), 18));
        label4.setText("Language:");
        panel1.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        language = new JTextField();
        language.setFont(new Font(language.getFont().getName(), language.getFont().getStyle(), 16));
        panel1.add(language, new GridConstraints(1, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setFont(new Font(label5.getFont().getName(), label5.getFont().getStyle(), 18));
        label5.setText("Translations:");
        panel1.add(label5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setFont(new Font(label6.getFont().getName(), label6.getFont().getStyle(), 18));
        label6.setText("Empty Translations:");
        panel1.add(label6, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exportButton = new JButton();
        exportButton.setFont(new Font(exportButton.getFont().getName(), exportButton.getFont().getStyle(), 18));
        exportButton.setText("Export");
        panel1.add(exportButton, new GridConstraints(9, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    private static class TranslationRenderer extends JLabel implements ListCellRenderer<Translation> {

        public TranslationRenderer() {
            setOpaque(true);
            setFont(getFont().deriveFont(getFont().getStyle(), 14f));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Translation> list, Translation value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.key);
            if (isSelected) {
                setBackground(new Color(0, 0, 128));
                setForeground(Color.WHITE);
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
            return this;
        }

    }
}
