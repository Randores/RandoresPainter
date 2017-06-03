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
package com.gmail.socraticphoenix.forge.randores.painter.texture.gui;

import com.gmail.socraticphoenix.forge.randores.painter.gui.ErrorScreen;
import com.gmail.socraticphoenix.forge.randores.painter.gui.LogScreen;
import com.gmail.socraticphoenix.forge.randores.painter.gui.SelectProjectScreen;
import com.gmail.socraticphoenix.forge.randores.painter.gui.VerifyScreen;
import com.gmail.socraticphoenix.forge.randores.painter.texture.TextureProject;
import com.gmail.socraticphoenix.forge.randores.painter.texture.image.TemplateTexture;
import com.gmail.socraticphoenix.forge.randores.painter.texture.image.Texture;
import com.gmail.socraticphoenix.jlsc.JLSCException;
import com.google.common.io.Files;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileFilter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TextureScreen {
    public JList templates;
    public JList textures;
    public JList unfinishedTextures;
    public JList unfinishedTemplates;
    public TextureProject project;
    public AtomicBoolean open = new AtomicBoolean(false);
    public AtomicBoolean changing = new AtomicBoolean(false);
    private JButton saveButton;
    private JButton exportButton;
    private JLabel label;
    private JPanel panel1;
    private JTextField textField1;
    private JButton copyTemplateButton;
    private JButton copyImageTemplate;
    private JFrame frame;
    private JFileChooser chooseExport;
    private JFileChooser chooseTexture;
    private ImageEditScreen current;
    private boolean previous;

    public TextureScreen(TextureProject project) {
        this.project = project;
        this.chooseExport = new JFileChooser();
        this.chooseTexture = new JFileChooser();
        this.chooseExport.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.chooseTexture.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.chooseExport.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".zip") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "ZIP files";
            }
        });
        this.chooseTexture.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".png") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "PNG files";
            }
        });
    }

    public JFrame display() {
        if (this.frame == null) {
            this.frame = new JFrame("Texture Project");

            this.unfinishedTemplates.setCellRenderer(new TextureCellRenderer());
            this.unfinishedTextures.setCellRenderer(new TextureCellRenderer());
            this.templates.setCellRenderer(new TextureCellRenderer());
            this.textures.setCellRenderer(new TextureCellRenderer());

            this.frame.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {

                }

                @Override
                public void windowClosing(WindowEvent e) {
                    new VerifyScreen(() -> {
                        frame.dispose();
                        if (current != null) {
                            current.dispose();
                        }
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

            this.copyTemplateButton.addActionListener(e -> {
                new SelectTemplateScreen(this, false).display();
            });

            this.copyImageTemplate.addActionListener(e -> {
                new SelectTemplateScreen(this, true).display();
            });

            this.unfinishedTextures.addListSelectionListener(e -> {
                if (!changing.get() && !e.getValueIsAdjusting() && !open.get() && this.unfinishedTextures.getSelectedValue() != null) {
                    changing.set(true);
                    Texture selected = (Texture) this.unfinishedTemplates.getSelectedValue();
                    this.unfinishedTemplates.clearSelection();
                    this.textures.clearSelection();
                    this.templates.clearSelection();
                    this.unfinishedTextures.clearSelection();

                    this.textField1.setText("unknown");

                    int res = this.chooseTexture.showOpenDialog(this.frame);
                    if (res == JFileChooser.APPROVE_OPTION) {
                        File file = this.chooseTexture.getSelectedFile();
                        File target = selected.getFile();
                        try {
                            Files.copy(file, target);
                            updateListData();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            new ErrorScreen("Unable to load image: " + e1.getMessage()).display();
                        }
                    }
                    changing.set(false);
                }
            });

            this.unfinishedTemplates.addListSelectionListener(e -> {
                if (!changing.get() && !e.getValueIsAdjusting() && !open.get() && this.unfinishedTemplates.getSelectedValue() != null) {
                    changing.set(true);
                    Texture selected = (Texture) this.unfinishedTemplates.getSelectedValue();
                    this.unfinishedTemplates.clearSelection();
                    this.textures.clearSelection();
                    this.templates.clearSelection();
                    this.unfinishedTextures.clearSelection();

                    if (open.get()) {
                        new ErrorScreen("An image is already being edited...").display();
                        changing.set(false);
                        return;
                    }

                    this.textField1.setText("unknown");

                    int res = this.chooseTexture.showOpenDialog(this.frame);
                    if (res == JFileChooser.APPROVE_OPTION) {
                        File file = this.chooseTexture.getSelectedFile();
                        File target = selected.getFile();
                        try {
                            Files.copy(file, target);
                            LogScreen log = new LogScreen("Log: " + selected.name(), JFrame.DO_NOTHING_ON_CLOSE);
                            LogScreen info = new LogScreen("Pixel Information: " + selected.name(), JFrame.DO_NOTHING_ON_CLOSE);

                            BufferedImage image = selected.getImage();
                            if (image == null) {
                                try {
                                    selected.load();
                                    image = selected.getImage();
                                    updateListData();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                    new ErrorScreen("Unable to load image: " + e1.getMessage()).display();
                                    return;
                                }
                            }

                            int x = image.getWidth();
                            int y = image.getHeight();

                            PixelSettings settings = new PixelSettings(selected.name(), x == 16 && y == 16 ? 50 : 4000 / (x + y));

                            open.set(true);
                            ImageEditScreen screen = new ImageEditScreen(this, (TemplateTexture) selected, settings, info, log, "Edit Texture: " + selected.name(), open);
                            screen.display();
                            settings.display();
                            log.display();
                            info.display();
                            this.frame.setState(Frame.ICONIFIED);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            new ErrorScreen("Unable to load image: " + e1.getMessage()).display();
                        }
                    }


                    this.textField1.setText(selected.getFile().getAbsolutePath());
                    changing.set(false);
                }
            });

            this.textures.addListSelectionListener(e -> {
                if (!changing.get() && !e.getValueIsAdjusting() && !open.get() && this.textures.getSelectedValue() != null) {
                    changing.set(true);
                    Texture selected = (Texture) this.textures.getSelectedValue();
                    this.unfinishedTemplates.clearSelection();
                    this.textures.clearSelection();
                    this.templates.clearSelection();
                    this.unfinishedTextures.clearSelection();

                    this.textField1.setText(selected.getFile().getAbsolutePath());

                    int res = this.chooseTexture.showOpenDialog(this.frame);
                    if (res == JFileChooser.APPROVE_OPTION) {
                        File file = this.chooseTexture.getSelectedFile();
                        File target = selected.getFile();
                        try {
                            Files.copy(file, target);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            new ErrorScreen("Unable to load image: " + e1.getMessage()).display();
                        }
                    }

                    this.textField1.setText(selected.getFile().getAbsolutePath());
                    changing.set(false);
                }
            });

            this.templates.addListSelectionListener(e -> {
                if (!changing.get() && !e.getValueIsAdjusting() && !open.get() && this.templates.getSelectedValue() != null) {
                    changing.set(true);
                    Texture selected = (Texture) this.templates.getSelectedValue();
                    this.unfinishedTemplates.clearSelection();
                    this.textures.clearSelection();
                    this.templates.clearSelection();
                    this.unfinishedTextures.clearSelection();

                    if (open.get()) {
                        new ErrorScreen("An image is already being edited...").display();
                        changing.set(false);
                        return;
                    }

                    this.textField1.setText(selected.getFile().getAbsolutePath());

                    LogScreen log = new LogScreen("Log: " + selected.name(), JFrame.DO_NOTHING_ON_CLOSE);
                    LogScreen info = new LogScreen("Pixel Information: " + selected.name(), JFrame.DO_NOTHING_ON_CLOSE);

                    BufferedImage image = selected.getImage();
                    if (image == null) {
                        try {
                            selected.load();
                            image = selected.getImage();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            new ErrorScreen("Unable to load image: " + e1.getMessage()).display();
                            return;
                        }
                    }

                    int x = image.getWidth();
                    int y = image.getHeight();

                    PixelSettings settings = new PixelSettings(selected.name(), x == 16 && y == 16 ? 50 : 4000 / (x + y) + 1);

                    open.set(true);
                    ImageEditScreen screen = new ImageEditScreen(this, (TemplateTexture) selected, settings, info, log, "Edit Texture: " + selected.name(), open);
                    screen.display();
                    settings.display();
                    log.display();
                    this.frame.setState(Frame.ICONIFIED);

                    this.textField1.setText(selected.getFile().getAbsolutePath());
                    changing.set(false);
                }
            });

            this.saveButton.addActionListener(e -> {
                try {
                    this.project.saveResources();
                } catch (IOException | JLSCException e1) {
                    e1.printStackTrace();
                    new ErrorScreen("Unable to save project: " + e1.getMessage()).display();
                }
            });

            this.exportButton.addActionListener(e -> {
                int status = this.chooseExport.showSaveDialog(this.frame);
                if (status == JFileChooser.APPROVE_OPTION) {
                    File file = this.chooseExport.getSelectedFile();
                    try {
                        this.project.exportProject(file);
                    } catch (IOException | JLSCException e1) {
                        e1.printStackTrace();
                        new ErrorScreen("Failed to export project: " + e1.getMessage()).display();
                    }
                }
            });

            this.updateListData();

            this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            this.frame.setSize(1300, 800);
            this.frame.add(this.panel1);
            this.frame.setVisible(true);
            return this.frame;
        } else {
            this.frame.setVisible(true);
            return this.frame;
        }
    }

    public void updateListData() {
        this.changing.set(true);
        this.unfinishedTextures.clearSelection();
        this.unfinishedTemplates.clearSelection();
        this.textures.clearSelection();
        this.templates.clearSelection();

        List<Texture> textures = this.project.getTextures();

        List<Texture> unfinishedImage = new ArrayList<>();
        List<Texture> unfinishedTemplate = new ArrayList<>();
        List<Texture> image = new ArrayList<>();
        List<Texture> template = new ArrayList<>();

        for (Texture texture : textures) {
            if (texture instanceof TemplateTexture) {
                if (texture.getFile().exists()) {
                    template.add(texture);
                } else {
                    unfinishedTemplate.add(texture);
                }
            } else {
                if (texture.getFile().exists()) {
                    image.add(texture);
                } else {
                    unfinishedImage.add(texture);
                }
            }
        }

        this.unfinishedTextures.setListData(unfinishedImage.toArray());
        this.unfinishedTemplates.setListData(unfinishedTemplate.toArray());
        this.textures.setListData(image.toArray());
        this.templates.setListData(template.toArray());

        this.changing.set(false);
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
        panel1.setLayout(new GridLayoutManager(10, 5, new Insets(0, 0, 0, 0), -1, -1));
        label = new JLabel();
        label.setFont(new Font(label.getFont().getName(), label.getFont().getStyle(), 20));
        label.setText("Texture Project: ");
        panel1.add(label, new GridConstraints(0, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setFont(new Font(saveButton.getFont().getName(), saveButton.getFont().getStyle(), 18));
        saveButton.setText("Save");
        panel1.add(saveButton, new GridConstraints(8, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exportButton = new JButton();
        exportButton.setFont(new Font(exportButton.getFont().getName(), exportButton.getFont().getStyle(), 18));
        exportButton.setText("Export");
        panel1.add(exportButton, new GridConstraints(9, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textField1 = new JTextField();
        textField1.setEditable(false);
        panel1.add(textField1, new GridConstraints(5, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setFont(new Font(label1.getFont().getName(), label1.getFont().getStyle(), 18));
        label1.setText("File:");
        panel1.add(label1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(2, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel3.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textures = new JList();
        scrollPane1.setViewportView(textures);
        final JLabel label2 = new JLabel();
        label2.setFont(new Font(label2.getFont().getName(), label2.getFont().getStyle(), 18));
        label2.setText("Textures:");
        panel3.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel4.add(scrollPane2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        templates = new JList();
        scrollPane2.setViewportView(templates);
        final JLabel label3 = new JLabel();
        label3.setFont(new Font(label3.getFont().getName(), label3.getFont().getStyle(), 18));
        label3.setText("Templates:");
        panel4.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel5, new GridConstraints(4, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane3 = new JScrollPane();
        panel6.add(scrollPane3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        unfinishedTextures = new JList();
        scrollPane3.setViewportView(unfinishedTextures);
        final JLabel label4 = new JLabel();
        label4.setFont(new Font(label4.getFont().getName(), label4.getFont().getStyle(), 18));
        label4.setText("Unselected Textures:");
        panel6.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane4 = new JScrollPane();
        panel7.add(scrollPane4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        unfinishedTemplates = new JList();
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        unfinishedTemplates.setModel(defaultListModel1);
        scrollPane4.setViewportView(unfinishedTemplates);
        final JLabel label5 = new JLabel();
        label5.setFont(new Font(label5.getFont().getName(), label5.getFont().getStyle(), 18));
        label5.setText("Unselected Templates:");
        panel7.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        copyTemplateButton = new JButton();
        copyTemplateButton.setFont(new Font(copyTemplateButton.getFont().getName(), copyTemplateButton.getFont().getStyle(), 18));
        copyTemplateButton.setText("Copy Template");
        panel1.add(copyTemplateButton, new GridConstraints(6, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        copyImageTemplate = new JButton();
        copyImageTemplate.setFont(new Font(copyImageTemplate.getFont().getName(), copyImageTemplate.getFont().getStyle(), 18));
        copyImageTemplate.setText("Copy Template and Image");
        panel1.add(copyImageTemplate, new GridConstraints(7, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    public static class TextureCellRenderer extends JLabel implements ListCellRenderer<Texture> {

        public TextureCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Texture> list, Texture value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.name());
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
