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
package com.gmail.socraticphoenix.forge.randores.painter.translation;

import com.gmail.socraticphoenix.forge.randores.painter.PainterApp;
import com.gmail.socraticphoenix.forge.randores.painter.Project;
import com.gmail.socraticphoenix.forge.randores.painter.gui.ErrorScreen;
import com.gmail.socraticphoenix.forge.randores.painter.gui.VerifyScreen;
import com.gmail.socraticphoenix.forge.randores.painter.translation.gui.TranslationScreen;
import com.gmail.socraticphoenix.forge.randores.painter.util.FileUtil;
import com.gmail.socraticphoenix.jlsc.JLSCArray;
import com.gmail.socraticphoenix.jlsc.JLSCCompound;
import com.gmail.socraticphoenix.jlsc.JLSCConfiguration;
import com.gmail.socraticphoenix.jlsc.JLSCException;
import com.gmail.socraticphoenix.jlsc.JLSCFormat;
import com.gmail.socraticphoenix.jlsc.value.JLSCValue;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TranslationProject extends Project {
    private List<Translation> translations;
    public String lang;

    public TranslationProject(String name, File dir, JLSCConfiguration configuration) {
        super(name, dir, configuration);
        this.translations = new ArrayList<>();
    }

    @Override
    public void importProject() throws IOException, JLSCException {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".lang") || f.getName().endsWith(".txt") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "lang files";
            }
        });

        chooser.setDialogTitle("Choose assets lang file");
        int res = chooser.showOpenDialog(null);
        if(res != JFileChooser.APPROVE_OPTION) {
            throw new IllegalStateException("File selection cancelled");
        }

        File assets = chooser.getSelectedFile();

        if(assets.isDirectory()) {
            throw new IllegalStateException("Directory selected");
        }

        chooser.setDialogTitle("Choose randores lang file");
        res = chooser.showOpenDialog(null);
        if(res != JFileChooser.APPROVE_OPTION) {
            throw new IllegalStateException("File selection cancelled");
        }

        File randores = chooser.getSelectedFile();

        if(randores.isDirectory()) {
            throw new IllegalStateException("Directory selected");
        }

        List<Translation> translations = new ArrayList<>();
        for (String line : Files.readAllLines(assets.toPath())) {
            if (line.contains("=")) {
                String[] pieces = line.split("=", 2);
                translations.add(new Translation(pieces[0], pieces[1], Translation.Type.ASSETS));
            }
        }

        for (String line : Files.readAllLines(randores.toPath())) {
            if (line.contains("=")) {
                String[] pieces = line.split("=", 2);
                translations.add(new Translation(pieces[0], pieces[1], Translation.Type.RANDORES));
            }
        }
        this.translations = translations;
        this.lang = assets.getName();
        this.saveResources();
    }

    public void cleanTranslations() throws IOException {
        List<Translation> english = PainterApp.getEnglishTranslations();
        Iterator<Translation> translationIterator = this.translations.iterator();
        while (translationIterator.hasNext()) {
            Translation next = translationIterator.next();
            if(english.stream().noneMatch(t -> t.key.equals(next.key))) {
                this.translations.remove(next);
            }
        }
    }

    public List<Translation> getTranslations() {
        try {
            this.cleanTranslations();
        } catch (IOException ignore) {

        }
        return this.translations;
    }

    public List<Translation> getUnfinished() throws IOException {
        this.cleanTranslations();
        List<Translation> unfinished = new ArrayList<>();
        List<Translation> english = PainterApp.getEnglishTranslations();
        for(Translation translation : english) {
            if(this.translations.stream().noneMatch(t -> t.key.equals(translation.key))) {
                unfinished.add(translation);
            }
        }

        return unfinished;
    }

    @Override
    public void setupNewProject() throws IOException, JLSCException {
        dir.mkdirs();
        JLSCConfiguration configuration = new JLSCConfiguration(new JLSCCompound(), new File(this.dir, "project.jlsc"), JLSCFormat.TEXT, true);
        configuration.put("type", Type.TRANSLATION);
        configuration.put("name", this.name);
        configuration.put("lang", "unknown");
        configuration.put("translations", new JLSCArray());
        configuration.save();
        this.configuration = configuration;
    }

    @Override
    public void loadResources() throws IOException, JLSCException {
        this.lang = this.configuration.getString("lang").get();
        JLSCArray translations = this.configuration.getArray("translations").get();
        for(JLSCValue value : translations) {
            this.translations.add(value.convert(Translation.class).get());
        }
    }

    @Override
    public void loadProject() {
        TranslationScreen screen = new TranslationScreen(this);
        screen.display();
    }

    @Override
    public void saveResources() throws IOException, JLSCException {
        JLSCArray translations = new JLSCArray();
        this.translations.forEach(translations::add);
        this.configuration.put("lang", this.lang);
        this.configuration.put("translations", translations);
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
            File assets = new File(tmp, "assets/randores/lang/" + lang.toLowerCase() + ".lang");
            File randores = new File(tmp, "assets/randores/resources/lang/" + lang.toLowerCase() + ".lang");
            assets.getParentFile().mkdirs();
            randores.getParentFile().mkdirs();

            try (FileWriter aw = new FileWriter(assets);
                 FileWriter rw = new FileWriter(randores);) {
                for (Translation translation : this.getTranslations()) {
                    if (translation.type == Translation.Type.ASSETS) {
                        aw.write(translation.key + "=" + translation.value + System.lineSeparator());
                    } else if (translation.type == Translation.Type.RANDORES) {
                        rw.write(translation.key + "=" + translation.value + System.lineSeparator());
                    }
                }

                aw.close();
                rw.close();

                PainterApp.genMcMeta(this.tmp, "Randores translations: " + this.lang);

                FileUtil.zipFile(tmp, target);
                FileUtil.deleteDirectory(tmp);
            } catch (IOException e2) {
                e2.printStackTrace();
                new ErrorScreen("Unable to export project: " + e2.getMessage()).display();
            }
        };

        if (target.exists()) {
            new VerifyScreen(save, "Overwrite?").display();
        } else {
            save.run();
        }
    }

}
