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
package com.gmail.socraticphoenix.forge.randores.painter;

import com.gmail.socraticphoenix.forge.randores.painter.texture.TextureProject;
import com.gmail.socraticphoenix.forge.randores.painter.translation.TranslationProject;
import com.gmail.socraticphoenix.jlsc.JLSCConfiguration;
import com.gmail.socraticphoenix.jlsc.JLSCException;

import java.io.File;
import java.io.IOException;

public abstract class Project {
    public String name;
    public File dir;
    public File tmp;
    public JLSCConfiguration configuration;

    public Project(String name, File dir, JLSCConfiguration configuration) {
        this.dir = dir;
        this.tmp = new File(dir, "tmp");
        this.configuration = configuration;
        this.name = name;
    }

    public static Project load(File dir) throws IOException, JLSCException {
        JLSCConfiguration configuration = JLSCConfiguration.fromText(new File(dir, "project.jlsc"));
        Project.Type type = configuration.getAs("type", Project.Type.class).get();
        String name = configuration.getString("name").get();
        if(type == Type.TEXTURE) {
            Project project = new TextureProject(name, dir, configuration);
            return project;
        } else if (type == Type.TRANSLATION) {
            Project project = new TranslationProject(name, dir, configuration);
            return project;
        } else {
            throw new IllegalStateException("Unrecognized type: " + type.name());
        }
    }

    public static Project create(String name, File dir, Project.Type type) throws IOException, JLSCException {
        if(type == Type.TEXTURE) {
            TextureProject project = new TextureProject(name, dir, null);
            project.setupNewProject();
            return project;
        } else if (type == Type.TRANSLATION) {
            TranslationProject project = new TranslationProject(name, dir, null);
            project.setupNewProject();
            return project;
        } else {
            throw new IllegalStateException("Unrecognized type: " + type.name());
        }
    }

    public File getDir() {
        return this.dir;
    }

    public abstract void importProject() throws IOException, JLSCException;

    public abstract void setupNewProject() throws IOException, JLSCException;

    public abstract void loadResources() throws IOException, JLSCException;

    public abstract void loadProject() throws IOException, JLSCException;

    public abstract void saveResources() throws IOException, JLSCException;

    public abstract void closeProject() throws IOException, JLSCException;

    public abstract void deleteProject() throws IOException, JLSCException;

    public abstract void exportProject(File target) throws IOException, JLSCException;

    public enum Type {
        TRANSLATION,
        TEXTURE
    }
}
