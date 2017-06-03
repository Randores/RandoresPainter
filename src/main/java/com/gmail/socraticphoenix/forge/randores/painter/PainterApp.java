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

import com.gmail.socraticphoenix.forge.randores.painter.git.GitDownload;
import com.gmail.socraticphoenix.forge.randores.painter.git.GitItem;
import com.gmail.socraticphoenix.forge.randores.painter.gui.LogScreen;
import com.gmail.socraticphoenix.forge.randores.painter.gui.SelectProjectScreen;
import com.gmail.socraticphoenix.forge.randores.painter.texture.image.ImageTexture;
import com.gmail.socraticphoenix.forge.randores.painter.texture.image.TemplateTexture;
import com.gmail.socraticphoenix.forge.randores.painter.texture.image.Texture;
import com.gmail.socraticphoenix.forge.randores.painter.texture.state.ImageState;
import com.gmail.socraticphoenix.forge.randores.painter.texture.state.ImageStateSerializer;
import com.gmail.socraticphoenix.forge.randores.painter.translation.Translation;
import com.gmail.socraticphoenix.jlsc.JLSCException;
import com.gmail.socraticphoenix.jlsc.registry.JLSCRegistry;

import javax.swing.JFrame;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PainterApp {
    public static File workingDir;
    public static File projects;
    public static File cache;
    public static File dictionary;
    public static File assets_lang;
    public static File randores_lang;
    public static File assets_textures;

    public static String dictionary_url = "https://raw.githubusercontent.com/SocraticPhoenix/Randores/master/src/main/resources/assets/randores/resources/dictionary/tex_dict.txt";
    public static String assets_lang_url = "https://raw.githubusercontent.com/SocraticPhoenix/Randores/master/src/main/resources/assets/randores/lang/en_US.lang";
    public static String randores_lang_url = "https://raw.githubusercontent.com/SocraticPhoenix/Randores/master/src/main/resources/com/gmail/socraticphoenix/forge/randore/resource/en_US.lang";
    public static String assets_textures_url = "https://api.github.com/repos/SocraticPhoenix/Randores/contents/src/main/resources/assets/randores/textures/";
    public static String[] assets_sub = {"items?ref=master", "blocks?ref=master", "gui?ref=master"};

    public static Map<String, Project> projectMap;
    private static List<Translation> translations = null;

    public static void main(String[] args) throws IOException, JLSCException {
        workingDir = new File(System.getProperty("user.home"), "randores_painter_app");
        projects = new File(workingDir, "projects");
        cache = new File(workingDir, "cache");
        dictionary = new File(cache, "dictionary.txt");
        assets_lang = new File(cache, "assets_lang.txt");
        randores_lang = new File(cache, "randores_lang.txt");
        assets_textures = new File(cache, "assets_textures.txt");
        workingDir.mkdirs();
        projects.mkdirs();
        cache.mkdirs();
        JLSCRegistry.register(new ImageStateSerializer());

        LogScreen loading = new LogScreen("Loading...", JFrame.EXIT_ON_CLOSE);
        loading.display();

        projectMap = new LinkedHashMap<>();

        loading.log("Attempting to download most recent resources...");
        loading.log("Downloading texture dictionary...");
        try {
            downloadFile(dictionary_url, dictionary);
        } catch (IOException e) {
            e.printStackTrace();
            loading.log("Unable to download dictionary, " + e.getMessage());
            if (dictionary.exists()) {
                loading.log("Using cached dictionary...");
            } else {
                loading.log("No dictionary is cached, failed to start.");
                stopError();
                return;
            }
        }

        loading.log("Downloading assets lang file...");
        try {
            downloadFile(assets_lang_url, assets_lang);
        } catch (IOException e) {
            e.printStackTrace();
            loading.log("Unable to download assets lang file, " + e.getMessage());
            if (assets_lang.exists()) {
                loading.log("Using cached assets lang file...");
            } else {
                loading.log("No assets lang file is cached, failed to start.");
                stopError();
                return;
            }
        }

        loading.log("Downloading randores lang file...");
        try {
            downloadFile(randores_lang_url, randores_lang);
        } catch (IOException e) {
            e.printStackTrace();
            loading.log("Unable to download randores lang file, " + e.getMessage());
            if (randores_lang.exists()) {
                loading.log("Using cached randores lang file...");
            } else {
                loading.log("No randores lang file is cached, failed to start.");
                stopError();
                return;
            }
        }

        loading.log("Downloading assets textures file...");
        try {
            List<String> lines = new ArrayList<>();
            for (String sub : assets_sub) {
                ImageTexture.Type type = sub.contains("item") ? ImageTexture.Type.ITEM : sub.contains("block") ? ImageTexture.Type.BLOCK : ImageTexture.Type.GUI;
                List<GitItem> items = GitDownload.listUrl(assets_textures_url + sub);
                items.stream().map(GitItem::getName).map(s -> s.replace(".png", "")).map(s -> s + "=" + type.name()).forEach(lines::add);
            }
            Files.write(assets_textures.toPath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
            loading.log("Unable to download assets texture file, " + e.getMessage());
            if (assets_textures.exists()) {
                loading.log("Using cached assets texture file...");
            } else {
                loading.log("No assets texture file is cahced, filed to start.");
                stopError();
                return;
            }
        }

        loading.log("Finished setting up cache, loading projects...");
        for (File file : projects.listFiles()) {
            loading.log("Loading project \"" + file.getName() + "\"");
            try {
                Project project = Project.load(file);
                projectMap.put(project.name, project);
                loading.log("Loaded project \"" + file.getName() + "\"");
            } catch (IOException | JLSCException e) {
                e.printStackTrace();
                loading.log("Failed to load project \"" + file.getName() + ",\" " + e.getMessage());
                e.printStackTrace();
            }
        }

        loading.log("Finished setting up");

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SelectProjectScreen screen = new SelectProjectScreen();
        screen.initValues();

        loading.display().dispose();
        screen.display();
    }

    public static List<Translation> getEnglishTranslations() throws IOException {
        if (translations == null) {
            translations = new ArrayList<>();
            for (String line : Files.readAllLines(assets_lang.toPath())) {
                if (line.contains("=")) {
                    String[] pieces = line.split("=", 2);
                    translations.add(new Translation(pieces[0], pieces[1], Translation.Type.ASSETS));
                }
            }

            for (String line : Files.readAllLines(randores_lang.toPath())) {
                if (line.contains("=")) {
                    String[] pieces = line.split("=", 2);
                    translations.add(new Translation(pieces[0], pieces[1], Translation.Type.RANDORES));
                }
            }
        }
        return translations;
    }

    public static List<Texture> getTextures(File dir) throws IOException {
        List<Texture> textures = new ArrayList<>();

        for (String line : Files.readAllLines(dictionary.toPath())) {
            if(!line.trim().isEmpty()) {
                textures.add(new TemplateTexture(new File(dir, line + ".png"), new ImageState()));
            }
        }

        for (String line : Files.readAllLines(assets_textures.toPath())) {
            if(!line.trim().isEmpty()) {
                String[] pieces = line.split("=", 2);
                textures.add(new ImageTexture(new File(dir, pieces[0] + ".png"), ImageTexture.Type.valueOf(pieces[1])));
            }
        }

        return textures;
    }


    private static void stopError() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    public static void downloadFile(String url, File target)
            throws IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream());
            fout = new FileOutputStream(target);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }

    public static void genMcMeta(File dir, String desc) throws IOException {
        File targ = new File(dir, "pack.mcmeta");
        FileWriter writer = new FileWriter(targ);
        writer.write(mcMeta(desc));
        writer.close();
    }

    public static String mcMeta(String desc) {
        return "{\n" +
                "   \"pack\":{\n" +
                "      \"pack_format\":3,\n" +
                "      \"description\":\"" + desc + "\"\n" +
                "   }\n" +
                "}";
    }

}
