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


import com.gmail.socraticphoenix.forge.randores.painter.gui.LogScreen;
import com.gmail.socraticphoenix.forge.randores.painter.texture.state.ImageState;
import com.gmail.socraticphoenix.forge.randores.painter.texture.state.PixelState;
import com.gmail.socraticphoenix.forge.randores.painter.util.ColorUtil;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImagePane extends JPanel {
    private BufferedImage image;
    private ImageState state;

    private PixelSettings settings;

    public ImagePane() {

    }

    public BufferedImage getImage() {
        return this.image;
    }

    public ImageState getState() {
        return this.state;
    }

    public PixelSettings getSettings() {
        return this.settings;
    }

    public void init(ImageState state, final BufferedImage image, final PixelSettings set, final LogScreen log, final LogScreen information) {
        this.image = image;
        this.state = state;
        this.settings = set;
        this.settings.providePane(this);
        this.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                Point mouse = e.getPoint();
                int x = (int) (mouse.getX() / (double) settings.scale());
                int y = (int) (mouse.getY() / (double) settings.scale());
                y = y < 0 ? 0 : y;
                int finalX = x = x < 0 ? 0 : x;
                int finalY = y;
                PixelState pstate = state.get(finalX, finalY);
                if (pstate != null) {
                    information.clear();
                    information.log("Pixel (", x, ", ", y, ")");
                    information.log("Vary Hue: ", pstate.isVary());
                    information.log("Shade: ", pstate.getShade());
                    information.log("Tint: ", pstate.getTint());
                } else {
                    information.clear();
                    information.log("Pixel (", x, ", ", y, ")");
                    information.log("Vary Hue: no setting");
                    information.log("Shade: no setting");
                    information.log("Tint: no setting");
                    information.log("Key: no setting");
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point mouse = e.getPoint();
                int x = (int) (mouse.getX() / (double) settings.scale());
                int y = (int) (mouse.getY() / (double) settings.scale());
                y = y < 0 ? 0 : y;
                int finalX = x = x < 0 ? 0 : x;
                int finalY = y;
                if (x < image.getWidth() && y < image.getHeight()) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        PixelState pstate = new PixelState(x, y, settings.tint(), settings.shade(), settings.vary());
                        state.remove(x, y);
                        state.add(pstate);
                        Graphics2D graphics = (Graphics2D) ImagePane.this.getGraphics();
                        graphics.setStroke(new BasicStroke(5.0f));
                        graphics.setColor(Color.BLACK);
                        graphics.drawOval(x * settings.scale(), y * settings.scale(), settings.scale(), settings.scale());
                        log.log("Set point (", x, ", ", y, ") to {hue=", pstate.isVary(), ", tint=", pstate.getTint(), ", shade=", pstate.getShade() + "}");
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        state.remove(x, y);
                        ImagePane.this.repaint();
                        log.log("Removed pixel mappings from point (", x, ", ", y, ")");
                    }
                }
            }
        });
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() % 2 == 1) {
                    Point mouse = e.getPoint();
                    int x = (int) (mouse.getX() / (double) settings.scale());
                    int y = (int) (mouse.getY() / (double) settings.scale());
                    y = y < 0 ? 0 : y;
                    x = x < 0 ? 0 : x;
                    if (x < image.getWidth() && y < image.getHeight()) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            PixelState pstate = new PixelState(x, y, settings.tint(), settings.shade(), settings.vary());
                            state.add(pstate);
                            Graphics2D graphics = (Graphics2D) ImagePane.this.getGraphics();
                            graphics.setStroke(new BasicStroke(5.0f));
                            graphics.setColor(Color.BLACK);
                            graphics.drawOval(x * settings.scale(), y * settings.scale(), settings.scale(), settings.scale());
                            log.log("Set point (", x, ", ", y, ") to {hue=", pstate.isVary(), ", tint=", pstate.getTint(), ", shade=", pstate.getShade() + "}");
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            state.remove(x, y);
                            ImagePane.this.repaint();
                            log.log("Removed pixel mappings from point (", x, ", ", y, ")");
                        }
                    }
                    int finalX = x;
                    int finalY = y;
                    PixelState pstate = state.get(finalX, finalY);
                    if (pstate != null) {
                        information.clear();
                        information.log("Pixel (", x, ", ", y, ")");
                        information.log("Vary Hue: ", pstate.isVary());
                        information.log("Shade: ", pstate.getShade());
                        information.log("Tint: ", pstate.getTint());
                    } else {
                        information.clear();
                        information.log("Pixel (", x, ", ", y, ")");
                        information.log("Vary Hue: no setting");
                        information.log("Shade: no setting");
                        information.log("Tint: no setting");
                        information.log("Key: no setting");
                    }
                } else {
                    Point mouse = e.getPoint();
                    int x = (int) (mouse.getX() / (double) settings.scale());
                    int y = (int) (mouse.getY() / (double) settings.scale());
                    y = y < 0 ? 0 : y;
                    x = x < 0 ? 0 : x;
                    Point center = new Point(x, y);
                    Color c = new Color(image.getRGB(x, y));
                    List<Point> visited = new ArrayList<>();
                    visited.add(center);

                    Set<Point> queued = new HashSet<>();
                    queued.add(new Point(center.x + 1, center.y));
                    queued.add(new Point(center.x, center.y + 1));
                    queued.add(new Point(center.x - 1, center.y));
                    queued.add(new Point(center.x, center.y - 1));

                    queued.add(new Point(center.x + 1, center.y + 1));
                    queued.add(new Point(center.x - 1, center.y + 1));
                    queued.add(new Point(center.x - 1, center.y - 1));
                    queued.add(new Point(center.x + 1, center.y - 1));

                    while (!queued.isEmpty()) {
                        Set<Point> newQueued = new HashSet<>();
                        for (Point p : queued) {
                            visited.add(p);
                            if (p.getX() >= 0 && p.getY() >= 0 && p.getX() < image.getWidth() && p.getY() < image.getHeight() && ColorUtil.distanceWithinThreshold(c, new Color(image.getRGB(p.x, p.y)), settings.blend())) {
                                if (SwingUtilities.isLeftMouseButton(e) && !state.contains(p.x, p.y)) {
                                    PixelState pstate = new PixelState(p.x, p.y, settings.tint(), settings.shade(), settings.vary());
                                    state.add(pstate);
                                    log.log("Set point (", p.x, ", ", p.y, ") to {hue=", pstate.isVary(), ", tint=", pstate.getTint(), ", shade=", pstate.getShade() + "}");
                                    Point[] pk = new Point[]{new Point(p.x + 1, p.y), new Point(p.x - 1, p.y), new Point(p.x, p.y + 1), new Point(p.x, p.y - 1), new Point(p.x + 1, p.y + 1), new Point(p.x - 1, p.y - 1), new Point(p.x - 1, p.y + 1), new Point(p.x + 1, p.y - 1)};
                                    for (Point k : pk) {
                                        if (!visited.contains(k)) {
                                            newQueued.add(k);
                                        }
                                    }
                                } else if (SwingUtilities.isRightMouseButton(e) && state.contains(p.x, p.y)) {
                                    state.remove(p.x, p.y);
                                    log.log("Removed pixel mappings from point (", p.x, ", ", p.y, ")");
                                    Point[] pk = new Point[]{new Point(p.x + 1, p.y), new Point(p.x - 1, p.y), new Point(p.x, p.y + 1), new Point(p.x, p.y - 1), new Point(p.x + 1, p.y + 1), new Point(p.x - 1, p.y - 1), new Point(p.x - 1, p.y + 1), new Point(p.x + 1, p.y - 1)};
                                    for (Point k : pk) {
                                        if (!visited.contains(k)) {
                                            newQueued.add(k);
                                        }
                                    }
                                }
                            }
                        }
                        queued = newQueued;
                    }

                    ImagePane.this.repaint();
                }
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D graphics2D = (Graphics2D) g;
        for (int y = 0; y < this.image.getHeight(); ++y) {
            for (int x = 0; x < this.image.getWidth(); ++x) {
                BufferedImage sub = this.image.getSubimage(x, y, 1, 1);
                BufferedImage large = new BufferedImage(this.settings.scale(), this.settings.scale(), sub.getType());
                Graphics2D gra = large.createGraphics();
                gra.drawImage(sub, 0, 0, this.settings.scale(), this.settings.scale(), null);
                gra.dispose();
                graphics2D.drawImage(large, x * this.settings.scale(), y * this.settings.scale(), this);
            }
        }
        for (int y = 0; y < this.image.getHeight(); ++y) {
            g.drawLine(0, y * this.settings.scale(), this.image.getWidth() * this.settings.scale(), y * this.settings.scale());
        }
        for (int x = 0; x < this.image.getWidth(); ++x) {
            g.drawLine(x * this.settings.scale(), 0, x * this.settings.scale(), this.image.getHeight() * this.settings.scale());
        }
        graphics2D.setStroke(new BasicStroke(5.0f));
        graphics2D.setColor(Color.BLACK);
        for (PixelState state : state.getStates()) {
            graphics2D.drawOval(state.getX() * this.settings.scale(), state.getY() * this.settings.scale(), this.settings.scale(), this.settings.scale());
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.image.getWidth() * this.settings.scale(), this.image.getHeight() * this.settings.scale());
    }

}

