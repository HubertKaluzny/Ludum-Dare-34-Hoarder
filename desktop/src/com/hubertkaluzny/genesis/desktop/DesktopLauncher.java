package com.hubertkaluzny.genesis.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.hubertkaluzny.genesis.Genesis;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 720;
		config.width = 1280;
		config.backgroundFPS = 60;
		config.foregroundFPS = 60;
		config.title = "Hoarder";
		config.resizable = false;
		config.addIcon("icon.png", Files.FileType.Classpath);
		new LwjglApplication(new Genesis(), config);
	}
}
