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
package com.gmail.socraticphoenix.forge.randores.painter.texture;

import com.gmail.socraticphoenix.collect.Items;
import com.gmail.socraticphoenix.forge.randores.painter.PainterApp;
import com.gmail.socraticphoenix.forge.randores.painter.Project;
import com.gmail.socraticphoenix.forge.randores.painter.gui.ErrorScreen;
import com.gmail.socraticphoenix.forge.randores.painter.gui.VerifyScreen;
import com.gmail.socraticphoenix.forge.randores.painter.texture.gui.TextureScreen;
import com.gmail.socraticphoenix.forge.randores.painter.texture.image.ImageTexture;
import com.gmail.socraticphoenix.forge.randores.painter.texture.image.TemplateTexture;
import com.gmail.socraticphoenix.forge.randores.painter.texture.image.Texture;
import com.gmail.socraticphoenix.forge.randores.painter.texture.state.ImageState;
import com.gmail.socraticphoenix.forge.randores.painter.util.FileUtil;
import com.gmail.socraticphoenix.jlsc.JLSCArray;
import com.gmail.socraticphoenix.jlsc.JLSCCompound;
import com.gmail.socraticphoenix.jlsc.JLSCConfiguration;
import com.gmail.socraticphoenix.jlsc.JLSCException;
import com.gmail.socraticphoenix.jlsc.JLSCFormat;
import com.gmail.socraticphoenix.jlsc.value.JLSCValue;
import com.google.common.io.Files;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TextureProject extends Project {
    private List<Texture> textures;

    public TextureProject(String name, File dir, JLSCConfiguration configuration) {
        super(name, dir, configuration);
        this.textures = new ArrayList<>();
    }

    public List<Texture> getTextures() {
        return this.textures;
    }

    public List<Texture> getUnfinished() {
        return this.textures.stream().filter(t -> !t.getFile().exists()).collect(Collectors.toList());
    }

    public List<Texture> getFinished() {
        return this.textures.stream().filter(t -> t.getFile().exists()).collect(Collectors.toList());
    }

    @Override
    public void importProject() throws IOException, JLSCException {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".zip") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "ZIP files";
            }
        });

        chooser.setDialogTitle("Choose resource pack");

        int res = chooser.showOpenDialog(null);
        if (res != JFileChooser.APPROVE_OPTION) {
            throw new IllegalStateException("File selection cancelled");
        }

        File zip = chooser.getSelectedFile();
        this.tmp.mkdirs();
        FileUtil.unZipFile(zip, this.tmp);
        List<Texture> textures = new ArrayList<>();
        File items = new File(this.tmp, "assets/randores/textures/items");
        File blocks = new File(this.tmp, "assets/randores/textures/blocks");
        File gui = new File(this.tmp, "assets/randores/textures/gui");
        File templates = new File(this.tmp, "assets/randores/resources/templates");
        List<String> tmp = new ArrayList<>();
        File[] tmps = templates.listFiles();
        if (tmps != null) {
            for (File f : tmps) {
                if(f.getName().endsWith(".txt")) {
                    tmp.add(f.getName().replace(".txt", ""));
                }
            }
        }
        for(String k : tmp) {
            File t = new File(this.dir, k + ".png");
            TemplateTexture templateTexture = new TemplateTexture(t, ImageState.fromString(new String(java.nio.file.Files.readAllBytes(new File(templates, k + ".txt").toPath()))));
            Files.copy(new File(templates, k + ".png"), t);
            textures.add(templateTexture);
        }

        List<File> img = new ArrayList<>();
        this.addAll(img, items.listFiles());
        this.addAll(img, blocks.listFiles());
        this.addAll(img, gui.listFiles());
        for (File f : img) {
            File t = new File(this.dir, f.getName());
            String s = f.getAbsolutePath();
            ImageTexture texture = new ImageTexture(t, s.contains("items") ? ImageTexture.Type.ITEM : s.contains("blocks") ? ImageTexture.Type.BLOCK : ImageTexture.Type.GUI);
            Files.copy(f, t);
            textures.add(texture);
        }
        FileUtil.deleteDirectory(this.tmp);

        this.textures = textures;
        Items.looseClone(PainterApp.getTextures(this.dir)).stream().filter(t -> this.textures.stream().noneMatch(k -> k.getFile().equals(t.getFile()))).forEach(this.textures::add);
        this.saveResources();
    }

    private void addAll(List<File> l, File[] r) {
        if (r != null) {
            Collections.addAll(l, r);
        }
    }

    @Override
    public void setupNewProject() throws IOException, JLSCException {
        dir.mkdirs();
        JLSCConfiguration configuration = new JLSCConfiguration(new JLSCCompound(), new File(this.dir, "project.jlsc"), JLSCFormat.TEXT, true);
        configuration.put("type", Type.TEXTURE);
        configuration.put("name", this.name);
        configuration.put("textures", new JLSCArray());
        configuration.save();
        this.configuration = configuration;
        this.textures = Items.looseClone(PainterApp.getTextures(this.dir));
    }

    @Override
    public void loadResources() throws IOException, JLSCException {
        JLSCArray textures = this.configuration.getArray("textures").get();
        for (JLSCValue value : textures) {
            if (value.convert(ImageTexture.class).isPresent()) {
                this.textures.add(value.convert(ImageTexture.class).get());
            } else {
                this.textures.add(value.convert(TemplateTexture.class).get());
            }
        }

        List<Texture> all = PainterApp.getTextures(this.dir);
        for (Texture texture : all) {
            if (this.textures.stream().noneMatch(t -> t.getFile().getName().equals(texture.getFile().getName()))) {
                this.textures.add(texture);
            }
        }
    }

    @Override
    public void loadProject() {
        TextureScreen screen = new TextureScreen(this);
        screen.display();
    }

    @Override
    public void saveResources() throws IOException, JLSCException {
        JLSCArray textures = new JLSCArray();
        this.textures.forEach(textures::add);
        this.configuration.put("textures", textures);
        this.configuration.save();
    }

    @Override
    public void closeProject() {

    }

    @Override
    public void deleteProject() throws IOException, JLSCException {
        FileUtil.deleteDirectory(this.dir);
    }

    @Override
    public void exportProject(File target) throws IOException, JLSCException {
        Runnable save = () -> {
            try {
                this.tmp.mkdirs();
                File items = new File(this.tmp, "assets/randores/textures/items");
                File blocks = new File(this.tmp, "assets/randores/textures/blocks");
                File gui = new File(this.tmp, "assets/randores/textures/gui");
                File templates = new File(this.tmp, "assets/randores/resources/templates");
                items.mkdirs();
                blocks.mkdirs();
                gui.mkdirs();
                templates.mkdirs();

                for (Texture texture : this.getFinished()) {
                    if (texture instanceof TemplateTexture) {
                        TemplateTexture templateTexture = (TemplateTexture) texture;
                        File img = new File(templates, templateTexture.name() + ".png");
                        File conf = new File(templates, templateTexture.name() + ".txt");
                        BufferedImage image = templateTexture.getImage();
                        if (image == null) {
                            templateTexture.load();
                            image = templateTexture.getImage();
                        }
                        ImageIO.write(image, "png", img);
                        FileWriter w = new FileWriter(conf);
                        w.write(templateTexture.getState().toString());
                        w.close();
                    } else if (texture instanceof ImageTexture) {
                        ImageTexture image = (ImageTexture) texture;
                        ImageTexture.Type type = image.getType();
                        File img = null;
                        if (type == ImageTexture.Type.BLOCK) {
                            img = new File(blocks, image.name() + ".png");
                        } else if (type == ImageTexture.Type.ITEM) {
                            img = new File(items, image.name() + ".png");
                        } else if (type == ImageTexture.Type.GUI) {
                            img = new File(gui, image.name() + ".png");
                        }

                        if (img != null) {
                            BufferedImage bi = image.getImage();
                            if (bi == null) {
                                image.load();
                                bi = image.getImage();
                            }
                            ImageIO.write(bi, "png", img);
                        }
                    }
                }

                PainterApp.genMcMeta(this.tmp, "Randores textures: " + target.getName().replace(".zip", ""));

                FileUtil.zipFile(this.tmp, target);
                FileUtil.deleteDirectory(this.tmp);
            } catch (IOException e) {
                e.printStackTrace();
                new ErrorScreen("Unable to export project: " + e.getMessage()).display();
            }
        };

        if (target.exists()) {
            new VerifyScreen(save, "Overwrite?").display();
        } else {
            save.run();
        }
    }

}
