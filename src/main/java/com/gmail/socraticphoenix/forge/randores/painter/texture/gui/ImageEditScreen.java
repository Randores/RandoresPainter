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

import com.gmail.socraticphoenix.collect.coupling.Pair;
import com.gmail.socraticphoenix.forge.randores.painter.gui.LogScreen;
import com.gmail.socraticphoenix.forge.randores.painter.gui.VerifyScreen;
import com.gmail.socraticphoenix.forge.randores.painter.texture.image.TemplateTexture;
import com.gmail.socraticphoenix.forge.randores.painter.texture.state.PixelState;
import com.gmail.socraticphoenix.forge.randores.painter.util.ColorUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ImageEditScreen {
    private JPanel panel1;
    private JPanel image;
    private JButton algoritm;

    private TemplateTexture templateTexture;
    private String name;
    private PixelSettings settings;
    private LogScreen pixel;
    private LogScreen log;

    private JFrame frame;

    private TextureScreen screen;

    private AtomicBoolean open;

    public ImageEditScreen(TextureScreen screen, TemplateTexture templateTexture, PixelSettings settings, LogScreen pixel, LogScreen log, String name, AtomicBoolean open) {
        this.templateTexture = templateTexture;
        this.settings = settings;
        this.pixel = pixel;
        this.log = log;
        this.name = name;
        this.screen = screen;
        this.open = open;
        $$$setupUI$$$();
        ((ImagePane) this.image).init(templateTexture.getState(), templateTexture.getImage(), settings, log, pixel);
    }

    public JFrame display() {
        if (this.frame == null) {
            this.frame = new JFrame(this.name);
            this.frame.setLocation(800, 0);
            this.settings.display().setLocation(100, 5);
            this.log.display().setLocation(100, 415);
            this.pixel.display().setLocation(100, 720);
            this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            this.algoritm.addActionListener(e -> {
                new VerifyScreen(() -> {
                    ImagePane pane = (ImagePane) this.image;
                    if (!pane.getState().getStates().isEmpty()) {
                        List<Pair<Point, Color>> locations = new ArrayList<>();
                        List<PixelState> result = new ArrayList<>();
                        pane.getState().getStates().stream().map(p -> Pair.of(new Point(p.getX(), p.getY()), new Color(pane.getImage().getRGB(p.getX(), p.getY())))).forEach(locations::add);
                        List<Integer> brightness = new ArrayList<>();
                        locations.forEach(p -> {
                            int b = ColorUtil.scaledGeneralBrightness(p.getB());
                            if (!brightness.contains(b)) {
                                brightness.add(b);
                            }
                        });
                        Collections.sort(brightness);

                        int scaledBase = brightness.isEmpty() ? 5 : brightness.size() % 2 == 0 ? Math.round((brightness.get(brightness.size() / 2) + brightness.get(brightness.size() / 2 - 1)) / 2f) : brightness.get(brightness.size() / 2);
                        locations.forEach(p -> {
                            int bright = ColorUtil.scaledGeneralBrightness(p.getB());
                            int val = bright - scaledBase;

                            int tint = 0;
                            int shade = 0;
                            if (val < 0) {
                                shade = -val;
                            } else {
                                tint = val;
                            }

                            result.add(new PixelState(p.getA().x, p.getA().y, tint, shade, false));
                        });
                        pane.getState().getStates().clear();
                        pane.getState().getStates().addAll(result);
                        pane.repaint();
                    }
                }, "Overwrite all template data?").display();
            });
            this.frame.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {

                }

                @Override
                public void windowClosing(WindowEvent e) {
                    dispose();
                    open.set(false);
                    screen.display().setState(JFrame.NORMAL);
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
            this.frame.setSize(800, 800);
            this.frame.add(this.panel1);
            this.frame.setVisible(true);
            return this.frame;
        } else {
            this.frame.setVisible(true);
            return this.frame;
        }
    }

    public void dispose() {
        screen.open.set(false);
        frame.dispose();
        settings.display().dispose();
        pixel.display().dispose();
        log.display().dispose();
    }

    private void createUIComponents() {
        this.image = new ImagePane();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(32);
        scrollPane1.setVerticalScrollBarPolicy(22);
        panel1.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setViewportView(image);
        algoritm = new JButton();
        algoritm.setText("Apply Templating Algorithm (Not 100% Accurate)");
        panel1.add(algoritm, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
